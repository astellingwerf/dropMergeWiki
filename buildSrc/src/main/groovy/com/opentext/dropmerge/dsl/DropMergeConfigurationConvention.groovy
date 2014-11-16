package com.opentext.dropmerge.dsl

import org.gradle.util.ConfigureUtil

class DropMergeConfigurationConvention {
    DropMergeConfiguration dropMerge

    public DropMergeConfigurationConvention(DropMergeConfiguration dropMerge) {
        this.dropMerge = dropMerge
    }

    def dropMerge(closure) {
        ConfigureUtil.configure(closure, dropMerge)
    }


    @Override
    public String toString() {
        dropMerge.toString()
    }
}
