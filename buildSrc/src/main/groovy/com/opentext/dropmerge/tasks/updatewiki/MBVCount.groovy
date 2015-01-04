package com.opentext.dropmerge.tasks.updatewiki

import com.opentext.dropmerge.dsl.JenkinsJob
import com.opentext.dropmerge.jenkins.WarningLevel
import org.gradle.api.tasks.TaskAction

import static com.opentext.dropmerge.tasks.UpdateWiki.getJenkinsJob

class MBVCount extends SimpleField {
    WarningLevel level
    Closure<JenkinsJob> projection

    void configure(WarningLevel l, Closure p) {
        level = l
        projection = p
    }

    @TaskAction
    public void calculateTestCount() {
        def job = projection(config.mbv)
        if (!job) {
            didWork = false
            return
        }
        result = getJenkinsJob(job).getMBFigure(level)
    }
}
