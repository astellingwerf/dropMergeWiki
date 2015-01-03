package com.opentext.dropmerge.dsl;

class Wiki {
    String userName
    String password
    String pageId
    boolean updateProductionServer = true

    def userName(String userName) { this.userName = userName }

    def password(String password) { this.password = password }

    def pageId(String id) { this.pageId = id }

    def pageId(int id) { pageId("$id") }

    def updateProductionServer(boolean updateProductionServer) { this.updateProductionServer = updateProductionServer }
}
