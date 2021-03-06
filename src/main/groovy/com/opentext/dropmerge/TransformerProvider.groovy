package com.opentext.dropmerge

import groovy.xml.MarkupBuilder

public abstract class TransformerProvider {
    private static final String COLUMNS = 'columns=type,key,summary,status'
    private static final String RENDER_MODE = 'renderMode=static'
    private static final String URL = 'url=https://jira.opentext.com/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery'
    private static final String AFTER = 'After'
    private static final String BEFORE = 'Before'

    public static Map<String, Closure<?>> transferFromPreviousPage(UpdateWikiProperties props, String previousWikiDropMergePageId, List<String> fieldsFromPreviousPage, transformers) {
        // Read fields ending with 'After' and beginning with one of the elements of 'fieldFromPreviousPage'
        // and transfer them to the corresponding 'Before'-field of this page
        CordysWiki wiki = new CordysWiki();
        wiki.authenticate(props.wikiUserName, props.wikiPassword)

        wiki.eachDropMergeField(previousWikiDropMergePageId) { CordysWiki.FormField formField ->
            if (formField.name.length() > AFTER.length() && fieldsFromPreviousPage.contains(formField.name[0..-(AFTER.length() + 1)])) {
                transformers.put(formField.name[0..-(AFTER.length() + 1)] + BEFORE) { formField.content }
            }
        }
    }

	static String getUserLink(String shortName) {
		return new StringWriter().with { writer ->
			newMarkupBuilder(writer).'ac:link' { 'ri:user'('ri:username':shortName) }

			return writer.toString()
		}
	}

	static String getJiraIssueLink(String key, String jiraServerName = 'OpenText JIRA') {
		return new StringWriter().with { writer ->
			newMarkupBuilder(writer).'ac:macro'('ac:name': 'jira' ) {
				'ac:parameter'('ac:name':'server', jiraServerName)
				'ac:parameter'('ac:name':'key', key)
			}

			return writer.toString()
		}
	}

    static String getLink(String url, String text) {
        Writer writer = new StringWriter()
        newMarkupBuilder(writer).a('href': url, text)

        return writer.toString()
    }

    static String getJiraIssues(String jiraQuery) {
        final String encodedJQ = jiraQuery.replace('=', '%3D').replace(',', '%2C')
        final String macro = '{jiraissues:' + RENDER_MODE + '|' + COLUMNS + '|' + TransformerProvider.URL + '=' + encodedJQ + '}'

        Writer writer = new StringWriter()
        MarkupBuilder htmlBuilder = newMarkupBuilder(writer)

        htmlBuilder.p {
            doubleQuotes = true
            img(
                    'class': 'editor-inline-macro',
                    'src': '/plugins/servlet/confluence/placeholder/macro?definition=' + macro.bytes.encodeBase64() + '&locale=en_GB&version=2',
                    'data-macro-name': 'jiraissues',
                    'data-macro-parameters': COLUMNS + '|' + RENDER_MODE + '|' + TransformerProvider.URL + '\\=' + encodedJQ
            )
        }

        return writer.toString()
    }

    static MarkupBuilder newMarkupBuilder(Writer writer) {
        return new MarkupBuilder(new IndentPrinter(writer, '', false))
    }

    static String selectOptionByStatus(def item, JenkinsJob job, Map<String, String> statusToOptionName) {
        String result = job.lastBuildResult
        if (statusToOptionName.containsKey(result)) {
            return CordysWiki.selectOption(item, statusToOptionName[result])
        } else {
            return null
        }
    }

    static Closure<String> withHtml(Closure<String> c) {
        return {
            Writer writer = new StringWriter()
            MarkupBuilder htmlBuilder = newMarkupBuilder(writer)
            c(htmlBuilder)
            return writer.toString()
        }
    }

    static Closure<String> withTable(Closure<Void> c) {
        return withHtml { MarkupBuilder html ->
            WikiTableBuilder table = new WikiTableBuilder(html)
            c(table)
            table.process()
        }
    }
}
