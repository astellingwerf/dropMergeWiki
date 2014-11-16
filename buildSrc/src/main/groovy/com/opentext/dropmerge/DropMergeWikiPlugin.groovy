package com.opentext.dropmerge

import com.opentext.dropmerge.dsl.*
import org.gradle.api.*;

class DropMergeWikiPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        applyTasks(project)
        applyConventions(project)
    }

    void applyTasks(project) {
    }

    void applyConventions(project) {
        def servers = project.container(JenkinsServer) { name -> new JenkinsServer(name) }
        def jobs = project.container(JenkinsJob) { name -> new JenkinsJob(name) }
        def regressionTests = project.container(RegressionTest) { name -> new RegressionTest(name) }

        def configuration = new DropMergeConfiguration(servers, jobs, regressionTests)
        project.convention.plugins.dropMerge = new DropMergeConfigurationConvention(configuration)
    }
}
