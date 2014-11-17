package com.opentext.dropmerge.dsl

import org.gradle.api.NamedDomainObjectContainer

class DropMergeConfiguration {
    NamedDomainObjectContainer<JenkinsServer> jenkinsServers
    NamedDomainObjectContainer<JenkinsJob> jenkinsJobs
    NamedDomainObjectContainer<RegressionTest> regressionTests

    Team team = new Team()
    Wiki wiki = new Wiki()

    WipTrunkPair<JenkinsJob> pmd = new WipTrunkPair<JenkinsJob>()
    WipTrunkPair<JenkinsJob> compilerWarnings = new WipTrunkPair<JenkinsJob>()
    Collection<JenkinsJob> upgrade = []
    Collection<JenkinsJob> integrationTests = []

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

    def pmd(Closure closure) {
        pmd.with(closure)
    }

    def compilerWarnings(Closure closure) {
        compilerWarnings.with(closure)
    }

    def upgrade(JenkinsJob job) {
        upgrade([job])
    }

    def upgrade(Collection<JenkinsJob> jobs) {
        this.upgrade.addAll(jobs)
    }

    def integrationTests(JenkinsJob job) {
        integrationTests([job])
    }

    def integrationTests(Collection<JenkinsJob> jobs) {
        this.integrationTests.addAll(jobs)
    }

    @Override
    public String toString() {
        return "DropMergeConfiguration {\n" +
                "\tteam {\n\t\t" + team +
                "\n\t}, jenkinsServers {\n" + jenkinsServers.collect() { '\t\t' + it.toString() }.join('\n') +
                "\n\t}, jenkinsJobs {\n" + jenkinsJobs.collect() { '\t\t' + it.toString() }.join('\n') +
                "\n\t}, regressionTests {\n" + regressionTests.collect() { '\t\t' + it.toString() }.join('\n') +
                "\n\t}, pmd {\n\t\t" + pmd +
                "\n\t}, compilerWarnings {\n\t\t" + compilerWarnings +
                "\n\t}, upgrade {\n\t\t" + upgrade.collect { it.name } +
                "\n\t}, integrationTests {\n\t\t" + integrationTests.collect { it.name } +
                '\n\t}\n}';
    }
}
