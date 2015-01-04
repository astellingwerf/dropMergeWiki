package com.opentext.dropmerge.tasks.updatewiki

import com.opentext.dropmerge.dsl.DropMergeConfiguration
import com.opentext.dropmerge.wiki.CordysWiki
import org.gradle.api.DefaultTask

public class SimpleField extends DefaultTask {
    String fieldName
    Map<String,String> results

    DropMergeConfiguration getConfig() {
        return project.convention.plugins.dropMerge.dropMerge
    }

    def getFormField() {
        def wiki = new CordysWiki()
        wiki.authenticate(config.wiki.userName, config.wiki.password)
        wiki.getDropMergeFields(config.wiki.pageId)[fieldName]
    }

    void setResult(String value) {
        results[fieldName] = value
    }

    void setSelectedOption(String option) {
        result = CordysWiki.selectOption(formField.rawItem, option)
    }

}
