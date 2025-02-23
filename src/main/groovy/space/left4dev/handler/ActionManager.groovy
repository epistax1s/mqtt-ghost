package space.left4dev.handler

import space.left4dev.config.action.ActionBaseConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import space.left4dev.utils.TypedClosure

/**
 * {@link ActionManager#activateAction}, {@link ActionManager#deactivateAction}, {@link ActionManager#isActionActive} -
 * методы, доступные только для Action у которых настроен Schedule. Если у Action не настроен Schedule, вызов этих
 * методов выдаст ошибку.
 *
 * Schedule позволяет создать Action который будет выполняться по заданному интервалу, интервал задается в
 * миллисекундах и пересчитывается при каждом перезапуски Action, соответственно это значение можно изменить.
 *
 * Методы {@link ActionManager#activateAction}, {@link ActionManager#deactivateAction} позволяют активировать и
 * деактивировать такой Action. Если он активирован, то он выполняется по заданному интервалу, если он деактивирован
 * то не выполняется - все просто). При этом деактивированный Action можно запустить используя команду
 * {@link ActionManager#doAction}. Это не активирует Action но заставит его моментально исполниться 1 раз.
 *
 * Можно изменять значения внутри Schedule, но нельзя динамически из Action без Schedule, сделать Action со Schedule.
 *
 */
class ActionManager {

    private static final Logger log = LoggerFactory.getLogger(ActionManager)

    private final ActionExecutor executor

    private final Map<String, TypedClosure<ActionBaseConfig>> actions = [:]

    ActionManager(ActionExecutor executor) {
        this.executor = executor
    }

    // TODO возможно сразу MAP'y
    // Передает Action в управление менеджеру
    void registerAction(String actionName, TypedClosure<ActionBaseConfig> actionClosure) {
        actions[actionName] = actionClosure
        log.info("registerAction() actionName = ${actionName}")
        if (actionClosure.call().schedule) {
            log.info("registerAction() actionName = ${actionName} // try activation action")
            activateAction(actionName)
        }
    }

    // Немедленный запуск любого Action
    void doAction(String actionName) {
        if (!actions.containsKey(actionName)) {
            log.warn("No action found with name: $actionName")
            return
        }
        executor.executeAction(actionName, actions[actionName])
        log.info("doAction() with name ${actionName}")
    }

    void activateAction(String actionName) {
        if (!actions.containsKey(actionName)) {
            log.warn("No action found with name: $actionName")
            return
        }

        def actionClosure = actions[actionName]

        if (!actionClosure.call().schedule) {
            log.error("Action ${actionName} is not a Scheduled action.") // TODO error
            return
        }

        if (isActionActive(actionName)) {
            log.warn("Action ${actionName} is already running")
            return
        }

        executor.scheduleAction(actionName, actionClosure)
    }

    void deactivateAction(String actionName) {
        if (!actions.containsKey(actionName)) {
            log.warn("No action found with name: $actionName")
            return
        }

        def actionClosure = actions[actionName]

        if (!actionClosure.call().schedule) {
            log.error("Action ${actionName} is not a Scheduled action.") // TODO error
            return
        }

        if (!isActionActive(actionName)) {
            log.warn("Action ${actionName} is not running")
            return
        }

        executor.stopAction(actionName)

        log.info("Deactivated action $actionName")
    }

    boolean isActionActive(String actionName) {
        return executor.isRunningAction(actionName)
    }
}