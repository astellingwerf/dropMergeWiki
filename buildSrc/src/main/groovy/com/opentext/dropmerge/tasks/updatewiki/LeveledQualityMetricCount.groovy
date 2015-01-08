package com.opentext.dropmerge.tasks.updatewiki

import com.opentext.dropmerge.jenkins.WarningLevel

abstract class LeveledQualityMetricCount extends QualityMetricCount {
    WarningLevel level

    void set(WarningLevel l) {
        level = l
    }
}
