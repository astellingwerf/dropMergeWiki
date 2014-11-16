package com.opentext.dropmerge.dsl

import org.gradle.api.NamedDomainObjectContainer

class DropMergeConfiguration {
    NamedDomainObjectContainer<JenkinsServer> jenkinsServers
    NamedDomainObjectContainer<JenkinsJob> jenkinsJobs
    NamedDomainObjectContainer<RegressionTest> regressionTests

    Team team = new Team()
    Wiki wiki = new Wiki()

    DropMergeConfiguration(NamedDomainObjectContainer<JenkinsServer> jenkinsServers,
                           NamedDomainObjectContainer<JenkinsJob> jenkinsJobs,
                           NamedDomainObjectContainer<RegressionTest> regressionTests) {
        this.jenkinsServers = jenkinsServers
        this.jenkinsJobs = jenkinsJobs
        this.regressionTests = regressionTests
    }

    def team(Closure closure) {
        team.with(closure)
    }

    def wiki(Closure closure) {
        wiki.with(closure)
    }

    def jenkinsServers(Closure closure) {
        jenkinsServers.configure(closure)
    }

    def jenkinsJobs(Closure closure) {
        jenkinsJobs.configure(closure)
    }

    def regressionTests(Closure closure) {
        regressionTests.configure(closure)
    }


    @Override
    public String toString() {
        return "DropMergeConfiguration {\n" +
                "\tteam {\n\t\t" + team +
                "\n\t}, jenkinsServers {\n" + jenkinsServers.collect() { '\t\t' + it.toString() }.join('\n') +
                "\n\t}, jenkinsJobs {\n" + jenkinsJobs.collect() { '\t\t' + it.toString() }.join('\n') +
                "\n\t}, regressionTests {\n" + regressionTests.collect() { '\t\t' + it.toString() }.join('\n') +
                '\n\t}\n}';
    }
}
