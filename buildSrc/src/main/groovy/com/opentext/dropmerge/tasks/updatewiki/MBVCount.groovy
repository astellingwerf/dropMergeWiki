package com.opentext.dropmerge.tasks.updatewiki

import com.opentext.dropmerge.dsl.JenkinsJob
import com.opentext.dropmerge.dsl.WipTrunkPair

class MBVCount extends QualityMetricCount {
    @Override
    protected WipTrunkPair<JenkinsJob> getMetricPair() {
        config.mbv
    }

    @Override
    protected String getMetricFigure(com.opentext.dropmerge.jenkins.JenkinsJob job) {
        job.getMBFigure(level)
    }
}
