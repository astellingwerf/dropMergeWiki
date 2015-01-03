package com.opentext.dropmerge.tasks

import com.opentext.dropmerge.dsl.DropMergeConfiguration
import com.opentext.dropmerge.dsl.RegressionTest
import com.opentext.dropmerge.jenkins.Jenkins
import com.opentext.dropmerge.jenkins.JenkinsJob
import com.opentext.dropmerge.jenkins.TestCount
import com.opentext.dropmerge.wiki.CordysWiki
import com.opentext.dropmerge.wiki.FieldDataTransformer
import com.opentext.dropmerge.wiki.WikiTableBuilder
import groovy.time.TimeCategory
import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.jenkinsci.images.IconCSS

import java.text.SimpleDateFormat

class UpdateWiki extends DefaultTask {

    DropMergeConfiguration getConfiguration() {
        return project.convention.plugins.dropMerge.dropMerge
    }

    @TaskAction
    public void updateWiki() {
        if (!configuration.wiki.userName) throw new IllegalArgumentException("wiki username not provided or empty")
        if (!configuration.wiki.password) throw new IllegalArgumentException("wiki password not provided or empty")
        if (!configuration.wiki.pageId) throw new IllegalArgumentException("wiki page id not provided or empty")

        def wiki = new CordysWiki()
        wiki.authenticate(configuration.wiki.userName, configuration.wiki.password)
        wiki.updateDropMergePage(configuration.wiki.pageId, new X(), configuration.wiki.updateProductionServer)
    }

    class X extends FieldDataTransformer {
        public String transformTeamLink(def item) { CordysWiki.selectOption(item, configuration.team.name) }

        @SuppressWarnings("SpellCheckingInspection")
        public String transformSuccesfulTestsBefore() { getComparableTestCount(TestCount.Pass) { it.left } }

        @SuppressWarnings("SpellCheckingInspection")
        public String transformSuccesfulTestsAfter() { getComparableTestCount(TestCount.Pass) { it.right } }

        public String transformFailedTestsBefore() { getComparableTestCount(TestCount.Fail) { it.left } }

        public String transformFailedTestsAfter() { getComparableTestCount(TestCount.Fail) { it.right } }

        public String getComparableTestCount(TestCount testCount, Closure projection) {
            """${
                configuration.regressionTests.sum { RegressionTest tests ->
                    tests.comparables.collectMany(projection).sum {
                        getJenkinsJob(it).getTestFigure(testCount) as int
                    }
                }
            }"""
        }

        public String transformSuccessfulRegressionTestsComment() {
            WikiTableBuilder.table { WikiTableBuilder table ->
                table.setHeaders(['Type', 'OS', 'Successful', 'Failed', 'Skipped', 'Link'])

                int passCount = 0, failCount = 0, skipCount = 0
                configuration.regressionTests.collectEntries { RegressionTest tests ->
                    [(tests.name): tests.comparables.collectMany { it.left } + tests.others]
                }.each { String type, Collection<com.opentext.dropmerge.dsl.JenkinsJob> jobs ->
                    jobs.each { com.opentext.dropmerge.dsl.JenkinsJob job ->
                        JenkinsJob jj = getJenkinsJob(job)
                        passCount += jj.getTestFigure(TestCount.Pass) as int
                        failCount += jj.getTestFigure(TestCount.Fail) as int
                        skipCount += jj.getTestFigure(TestCount.Skip) as int
                        table << [type,
                                job.description,
                                jj.getTestFigure(TestCount.Pass),
                                jj.getTestFigure(TestCount.Fail),
                                jj.getTestFigure(TestCount.Skip),
                                getJenkinsUrlWithStatus(jj)
                        ]
                    }
                }

                table << ['All', 'All', "$passCount", "$failCount", "$skipCount", '']
            } + WikiTableBuilder.table { WikiTableBuilder table ->

                configuration.regressionTests.each { RegressionTest tests ->
                    tests.comparables.each {
                        String wipDescription = it.left*.description.unique().join(' / ')
                        it.right.each { com.opentext.dropmerge.dsl.JenkinsJob job ->
                            JenkinsJob jj = getJenkinsJob(job)
                            Date ts = jj.getBuildTimestamp(JenkinsJob.LAST_COMPLETED_BUILD)
                            String timestampText = new SimpleDateFormat('MMM dd \'at\' HH:mm z').format(ts)
                            def diff = TimeCategory.minus(new Date(), ts).days
                            if (diff > 2)
                                timestampText += ", $diff days ago"
                            table << ['Type': tests.name,
                                    'OS': wipDescription,
                                    'WIP was compared to trunk job': getJenkinsUrlWithStatus(jj),
                                    'Timestamp': timestampText
                            ]
                        }
                    }
                }
            } + WikiTableBuilder.withHtml { MarkupBuilder html ->
                html.style IconCSS.style
            }
        }

        public String transformIntegrationTestsPass(def item) {
            def option = configuration.integrationTests.every { com.opentext.dropmerge.dsl.JenkinsJob j ->
                getJenkinsJob(j).lastBuildResult == 'SUCCESS'
            } ? 'Yes' : 'No'
            CordysWiki.selectOption(item, option)
        }

        private static JenkinsJob getJenkinsJob(com.opentext.dropmerge.dsl.JenkinsJob job) {
            Jenkins.getInstance(job.server.url).withJob(job.jobName, job.matrixAxes)
        }

        private static Closure getJenkinsUrl(JenkinsJob job, String build = null, String linkText = null) {
            return {
                a(href: job.getBuildUrl(build), linkText ?: job.toString())
            }
        }

        private static Closure getJenkinsUrlWithStatus(JenkinsJob job, String build = null, String linkText = null) {
            return {
                span(class: "jenkinsJobStatus jenkinsJobStatus_${job.color}", getJenkinsUrl(job, build, linkText))
            }
        }

    }
}
