package com.opentext.dropmerge.tasks.updatewiki

import com.opentext.dropmerge.dsl.JenkinsJob
import com.opentext.dropmerge.dsl.WipTrunkPair
import org.gradle.api.tasks.TaskAction

import static com.opentext.dropmerge.tasks.UpdateWiki.getJenkinsJob

abstract class QualityMetricCount extends SimpleField {
    Closure<JenkinsJob> projection

    void set(Closure p) {
        projection = p
    }

    @TaskAction
    public void calculateTestCount() {
        def labelToProjection = [Before: { it.trunk }, After: { it.wip }]
        didWork = labelToProjection.values().every { projection -> projection(metricPair) != null }
        if (!didWork)
            return
        labelToProjection.each { appendix, projection ->
            setResult appendix, getMetricFigure(getJenkinsJob(projection(metricPair)))
        }
    }

    protected abstract WipTrunkPair<JenkinsJob> getMetricPair()

    protected abstract String getMetricFigure(com.opentext.dropmerge.jenkins.JenkinsJob job)
}
