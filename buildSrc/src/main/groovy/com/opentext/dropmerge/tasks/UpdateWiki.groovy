package com.opentext.dropmerge.tasks

import com.opentext.dropmerge.dsl.DropMergeConfiguration
import com.opentext.dropmerge.jenkins.Jenkins
import com.opentext.dropmerge.jenkins.JenkinsJob
import com.opentext.dropmerge.tasks.updatewiki.*
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

import static com.opentext.dropmerge.DropMergeWikiPlugin.DROP_MERGE_GROUP
import static com.opentext.dropmerge.jenkins.TestCount.Fail
import static com.opentext.dropmerge.jenkins.TestCount.Pass
import static com.opentext.dropmerge.jenkins.WarningLevel.High
import static com.opentext.dropmerge.jenkins.WarningLevel.Normal

class UpdateWiki extends DefaultTask {

    static Task updateAllTask
    Map<String, String> resultingData = [:]

    DropMergeConfiguration getConfiguration() {
        return project.convention.plugins.dropMerge.dropMerge
    }

    public UpdateWiki() {
        super()
        if (!updateAllTask) {
            updateAllTask = project.task('updateWiki-all').dependsOn this
        }

        // 0
        registerDependencyTaskForField('TeamLink') { selectedOption = config.team.name }

        // 4
        registerDependencyTaskForField('IntegrationTestsPass') {
            selectedOption = config.integrationTests.every {
                getJenkinsJob(it).lastBuildResult == 'SUCCESS'
            } ? 'Yes' : 'No'
        }
        registerDependencyTaskForField('IntegrationTestsPassComment',
                JenkinsJobStatusComment,
                { set({ config.integrationTests }) })

        // 5, 6, and 7
        registerDependencyTaskForField('SuccesfulTestsBefore', ComparableTestCount, { configure Pass, { it.left } })
        registerDependencyTaskForField('SuccesfulTestsAfter', ComparableTestCount, { configure Pass, { it.right } })
        registerDependencyTaskForField('FailedTestsBefore', ComparableTestCount, { configure Fail, { it.left } })
        registerDependencyTaskForField('FailedTestsAfter', ComparableTestCount, { configure Fail, { it.right } })
        registerDependencyTaskForField('SuccessfulRegressionTestsComment', SuccessfulRegressionTestsComment)
        registerDependencyTaskForField('TotalRegressionTestsComment', TotalRegressionTestsComment)

        // 14
        registerDependencyTaskForField('UpgradeTested') {
            selectedOption = config.upgrade.every {
                getJenkinsJob(it).lastBuildResult == 'SUCCESS'
            } ? 'Yes' : 'No'
        }
        registerDependencyTaskForField('UpgradeTestedComment',
                JenkinsJobStatusComment,
                { set({ config.upgrade }) })

        // 17, and 18
        registerDependencyTaskForField('MBViolationsHighBefore', MBVCount, { configure High, { it.trunk } })
        registerDependencyTaskForField('MBViolationsHighAfter', MBVCount, { configure High, { it.wip } })
        registerDependencyTaskForField('MBViolationsMediumBefore', MBVCount, { configure Normal, { it.trunk } })
        registerDependencyTaskForField('MBViolationsMediumAfter', MBVCount, { configure Normal, { it.wip } })

        // 19
        registerDependencyTaskForField('CompilerWarningsBefore') {
            def j = config.compilerWarnings.trunk
            if (!j) {
                didWork = false
                return
            }
            result = getJenkinsJob(j).compilerWarningFigure
        }
        registerDependencyTaskForField('CompilerWarningsAfter') {
            def j = config.compilerWarnings.wip
            if (!j) {
                didWork = false
                return
            }
            result = getJenkinsJob(j).compilerWarningFigure
        }

        // 20, and 21
        registerDependencyTaskForField('PMDViolationsHighBefore', PMDCount, { configure High, { it.trunk } })
        registerDependencyTaskForField('PMDViolationsHighAfter', PMDCount, { configure High, { it.wip } })
        registerDependencyTaskForField('PMDViolationsMediumBefore', PMDCount, { configure Normal, { it.trunk } })
        registerDependencyTaskForField('PMDViolationsMediumAfter', PMDCount, { configure Normal, { it.wip } })
    }

    private Task registerDependencyTaskForField(String field,
                                                Class<? extends Task> type,
                                                Closure configure = null,
                                                Closure action = null) {
        def t = project.task("fillDropMergeField$field",
                group: DROP_MERGE_GROUP,
                type: type,
                description: "Calculate the data for the field \'$field\'.")
                .configure({ fieldName = field; results = resultingData })
                .configure(configure)
        if (action)
            t << action
        t.finalizedBy this
        updateAllTask.dependsOn t
        return t
    }

    private Task registerDependencyTaskForField(String field, Closure action) {
        registerDependencyTaskForField(field, SimpleField, {}, action)
    }

    @TaskAction
    public void updateWiki() {
        if (!configuration.wiki.userName) throw new IllegalArgumentException('wiki username not provided or empty')
        if (!configuration.wiki.password) throw new IllegalArgumentException('wiki password not provided or empty')
        if (!configuration.wiki.pageId) throw new IllegalArgumentException('wiki page id not provided or empty')

        println resultingData
//        def wiki = new CordysWiki()
//        wiki.authenticate(configuration.wiki.userName, configuration.wiki.password)
//        wiki.updateDropMergePage(configuration.wiki.pageId, new X(), configuration.wiki.updateProductionServer)
//        dependsOn()
    }

    public static JenkinsJob getJenkinsJob(com.opentext.dropmerge.dsl.JenkinsJob job) {
        Jenkins.getInstance(job.server.url).withJob(job.jobName, job.matrixAxes)
    }
}
