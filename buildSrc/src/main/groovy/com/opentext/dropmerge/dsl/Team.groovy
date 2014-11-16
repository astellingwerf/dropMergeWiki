package com.opentext.dropmerge.dsl


class Team {
    String name
    String scrumMaster
    List<String> architects = []
    String productManager
    List<String> otherMembers = []

    void name(String name) { this.name = name }

    void scrumMaster(String scrumMaster) { this.scrumMaster = scrumMaster }

    void architect(String... architect) { this.architects.addAll(architect) }

    void productManager(String productManager) { this.productManager = productManager }

    void otherMembers(String... member) { this.otherMembers.addAll(member) }

    List<String> getAllMembers() {
        ([scrumMaster, productManager] + architects + otherMembers)
    }

    @Override
    public String toString() {
        return name + ': ' +
                allMembers.sort().unique().collect {
                    def roles = (scrumMaster == it ? ['S'] : []) +
                            (productManager == it ? ['P'] : []) +
                            (architects.contains(it) ? ['A'] : [])
                    it + (roles ? " (${roles.join(',')})" : '')
                }.join(', ')
    }
}
