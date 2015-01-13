package com.opentext.dropmerge

import com.opentext.dropmerge.dsl.*
import com.opentext.dropmerge.tasks.*
import org.gradle.api.*

class DropMergeWikiPlugin implements Plugin<Project> {

    public static final String DROP_MERGE_GROUP = 'Drop merge'

    @Override
    void apply(Project project) {
        applyTasks(project)
        applyConventions(project)
    }

    void applyTasks(project) {
        project.task('printConfiguration', type: PrintConfiguration, group: DROP_MERGE_GROUP)
        project.task('updateWiki', type: UpdateWiki, group: DROP_MERGE_GROUP)
    }

    void applyConventions(project) {
        def servers = project.container(JenkinsServer)
        def jobs = project.container(JenkinsJob)
        def regressionTests = project.container(RegressionTest)
        def qualityQuestions = project.container(QualityAndProcessQuestion)

        def configuration = new DropMergeConfiguration(servers, jobs, regressionTests, qualityQuestions)
        project.convention.plugins.dropMerge = new DropMergeConfigurationConvention(configuration)
    }
}
