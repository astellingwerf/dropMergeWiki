package com.opentext.dropmerge.dsl

import groovy.time.TimeCategory
import groovy.xml.MarkupBuilder

import java.text.SimpleDateFormat

import org.jenkinsci.images.IconCSS

import com.opentext.dropmerge.*

class JenkinsSpec {

    private Map<String, Closure<String>> inputs

    JenkinsSpec(Map<String, Closure<String>> inputs) {
        this.inputs = inputs
    }

    void regressionTests(@DelegatesTo(TestTypesSpec) Closure jobsByType) {
        TestTypesSpec jobSpec = new TestTypesSpec()
        jobSpec.with jobsByType

        inputs['FailedRegressionTestsComment'] = { jobSpec.extraComment.sb.toString() }
    }

    void pmd(@DelegatesTo(ComparableJobsSpec) Closure jobs) {
        ComparableJobsSpec jobSpec = new ComparableJobsSpec()
        jobSpec.with jobs

        use(StringClosureCategories) {
            [HIGH: 'High', NORMAL: 'Medium'].each { String jenkinsTerm, String wikiFieldTerm ->
                inputs["PMDViolations${wikiFieldTerm}Comment"] = createQualityMetricComment(jobSpec, "pmdResult/$jenkinsTerm", 'PMD results')
                inputs["PMDViolations${wikiFieldTerm}Comment"] += TransformerProvider.withTable { table ->
                    Jenkins.DifferenceDetails differenceDetails = Jenkins.getDetailedPMDDiffsPerSuite(jobSpec.trunk, jobSpec.wip, [jenkinsTerm])
                    differenceDetails.diffsPerSuite.each buildDiffTable(table, differenceDetails, "pmdResult/$jenkinsTerm", jobSpec)
                }
            }
        }
    }

    private Closure buildDiffTable(WikiTableBuilder table, Jenkins.DifferenceDetails diffDetails, String reportUrl, ComparableJobsSpec jobSpec) {
        return { k, v ->
            String linkTrunk, linkWip
            boolean b2a, a2b;

            if ((b2a = diffDetails.beforeToAfter.containsKey(k)) || diffDetails.onlyBefore.contains(k)) {
                linkTrunk = getFileReportUrl(jobSpec.trunk, reportUrl, k)
                if (b2a)
                    linkWip = getFileReportUrl(jobSpec.wip, reportUrl, diffDetails.beforeToAfter[k])
            } else if ((a2b = diffDetails.afterToBefore.containsKey(k)) || diffDetails.onlyAfter.contains(k)) {
                linkWip = getFileReportUrl(jobSpec.wip, reportUrl, k)
                if (a2b)
                    linkTrunk = getFileReportUrl(jobSpec.trunk, reportUrl, diffDetails.afterToBefore[k])
            }

            table.addRow(
                    'File': k,
                    'Link': {
                        nobr {
                            if (linkTrunk)
                                a(href: linkTrunk, 'T')
                            if (linkTrunk && linkWip)
                                mkp.yield ' / '
                            if (linkWip)
                                a(href: linkWip, 'W')
                        }
                    },
                    'Diff': String.format('%+d', v))
        }
    }

    private String getFileReportUrl(JenkinsJob job, String reportUrl, String fileName) {
        return "${job.getBuildUrl(JenkinsJob.LAST_SUCCESSFUL_BUILD)}/$reportUrl/file.${fileName.hashCode()}/"
    }

    void compilerWarnings(@DelegatesTo(ComparableJobsSpec) Closure jobs) {
        ComparableJobsSpec jobSpec = new ComparableJobsSpec()
        jobSpec.with jobs

        use(StringClosureCategories) {
            inputs['CompilerWarningsComment'] = createQualityMetricComment(jobSpec, 'warnings3Result', 'Compile Warning results')
            inputs['CompilerWarningsComment'] += TransformerProvider.withTable { table ->
                Jenkins.DifferenceDetails differenceDetails = Jenkins.getDetailedCompilerWarningsDiffsPerSuite(jobSpec.trunk, jobSpec.wip)
                differenceDetails.diffsPerSuite.each buildDiffTable(table, differenceDetails, 'warnings3Result', jobSpec)
            }
        }
    }

    void mbv(@DelegatesTo(ComparableJobsSpec) Closure jobs) {
        ComparableJobsSpec jobSpec = new ComparableJobsSpec()
        jobSpec.with jobs

        use(StringClosureCategories) {
            [HIGH: 'High', NORMAL: 'Medium'].each { String jenkinsTerm, String wikiFieldTerm ->
                inputs["MultibrowserViolations${wikiFieldTerm}Comment"] = createQualityMetricComment(jobSpec, "muvipluginResult/$jenkinsTerm", 'MBV results')
                inputs["MultibrowserViolations${wikiFieldTerm}Comment"] += TransformerProvider.withTable { table ->
                    Jenkins.DifferenceDetails differenceDetails = Jenkins.getDetailedMBVDiffsPerSuite(jobSpec.trunk, jobSpec.wip, [jenkinsTerm])
                    differenceDetails.diffsPerSuite.each buildDiffTable(table, differenceDetails, "muvipluginResult/$jenkinsTerm", jobSpec)
                }
            }
        }
    }

    private static Closure<String> createQualityMetricComment(ComparableJobsSpec jobPairSpec, String reportUrl, String reportTitle) {
        return TransformerProvider.withHtml { html ->
            html.p {
                html.b 'Trunk:'
                html.mkp.yieldUnescaped '&nbsp;'
                html.a(href: jobPairSpec.trunk.getBuildUrl(JenkinsJob.LAST_SUCCESSFUL_BUILD) + '/' + reportUrl + '/', reportTitle)
                html.mkp.yield ' from our own trunk build.'
                html.br()
                html.b 'WIP:'
                html.mkp.yieldUnescaped '&nbsp;'
                html.a(href: jobPairSpec.wip.getBuildUrl(JenkinsJob.LAST_SUCCESSFUL_BUILD) + '/' + reportUrl + '/', reportTitle)
                html.mkp.yield ' from our WIP build.'
            }
        }
    }
}
