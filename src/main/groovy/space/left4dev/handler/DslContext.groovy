package space.left4dev.handler


import groovy.json.JsonSlurper

class DslContext {
    private final ActionManager actionManager
    private final Map<String, Object> vars

    DslContext(ActionManager actionManager, Map<String, Object> vars) {
        this.actionManager = actionManager
        this.vars = vars
    }

    void doAction(String actionName) {
        actionManager.doAction(actionName)
    }

    void activateAction(String actionName) {
        actionManager.activateAction(actionName)
    }

    void deactivateAction(String actionName) {
        actionManager.deactivateAction(actionName)
    }

    // Утилитный метод для парсинга JSON, доступный в DSL
    def parseJson(String text) {
        new JsonSlurper().parseText(text)
    }

    // Доступ к vars напрямую через DslContext
    Map<String, Object> getVars() {
        return vars
    }
}
