package com.opentext.dropmerge.tasks.updatewiki

import com.opentext.dropmerge.dsl.RegressionTest
import com.opentext.dropmerge.jenkins.TestCount
import org.gradle.api.tasks.TaskAction

import static com.opentext.dropmerge.tasks.UpdateWiki.getJenkinsJob

class ComparableTestCount extends SimpleField {
    TestCount testCount
    Closure projection

    void configure(TestCount tc, Closure p) {
        testCount = tc
        projection = p
    }

    @TaskAction
    public void calculateTestCount() {
        result = String.valueOf(config.regressionTests.sum { RegressionTest tests ->
            tests.comparables.collectMany(projection).sum {
                getJenkinsJob(it).getTestFigure(testCount) as int
            }
        })
    }
}
