package com.opentext.dropmerge.tasks.updatewiki

import com.opentext.dropmerge.dsl.JenkinsJob
import com.opentext.dropmerge.dsl.WipTrunkPair
import com.opentext.dropmerge.jenkins.WarningLevel
import org.gradle.api.tasks.TaskAction

import static com.opentext.dropmerge.tasks.UpdateWiki.getJenkinsJob

abstract class QualityMetricCount extends SimpleField {
    WarningLevel level
    Closure<JenkinsJob> projection

    void configure(WarningLevel l, Closure p) {
        level = l
        projection = p
    }

    @TaskAction
    public void calculateTestCount() {
        def job = projection(metricPair)
        if (!job) {
            didWork = false
            return
        }
        result = getMetricFigure(getJenkinsJob(job))
    }

    protected abstract WipTrunkPair<JenkinsJob> getMetricPair()

    protected abstract String getMetricFigure(com.opentext.dropmerge.jenkins.JenkinsJob job)
}
