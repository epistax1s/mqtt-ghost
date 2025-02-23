package space.left4dev.handler

import space.left4dev.config.action.ActionBaseConfig
import space.left4dev.config.action.ActionConfig
import space.left4dev.config.action.ActionType
import space.left4dev.config.action.PublishConfig
import space.left4dev.mqtt.MqttConnection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import space.left4dev.utils.TypedClosure

import java.util.concurrent.ScheduledFuture

class ActionExecutor {

    private static final Logger log = LoggerFactory.getLogger(ActionExecutor)

    private final MqttConnection mqttConnection
    private final Map<String, ScheduledFuture<?>> runningActions = [:]
    private final ScheduleManager scheduleManager;

    private final LinkedHashMap<String, Action<? extends ActionBaseConfig>> executors;

    ActionExecutor(MqttConnection mqttConnection) {
        this.mqttConnection = mqttConnection
        this.scheduleManager = new ScheduleManager()
        this.executors = [
                (ActionType.PUBLISH.name()): new PublishAction(mqttConnection),
                (ActionType.ACTION.name()) : new JustAction()
        ]
    }

    void executeAction(String actionName, TypedClosure<ActionBaseConfig> actionClosure) {
        try {
            def action = executors[actionClosure.type]
            action.execute(actionName, actionClosure.getClosure())
        } catch (Exception e) {
            log.error(e.getMessage(), e)
        }
    }

    void scheduleAction(String actionName, TypedClosure<ActionBaseConfig> actionClosure) {
        if (runningActions.containsKey(actionName)) {
            log.warn("Action ${actionName} is already running")
            return
        }
        if (!actionClosure.call().schedule) {
            log.error("Action ${actionName} is not a Scheduled action.")
            return
        }

        def task = () -> {
            executeAction(actionName, actionClosure)
        }

        def future = scheduleManager.schedule(task, actionClosure.call().schedule)
        runningActions[actionName] = future
        log.info("Action ${actionName} successfully scheduled")
    }

    void stopAction(String actionName) {
        def future = runningActions.remove(actionName)
        if (future) {
            future.cancel(true)
            log.info("Stopped action $actionName")
        } else {
            log.warn("No running action found for $actionName")
        }
    }

    boolean isRunningAction(String actionName) {
        return runningActions.containsKey(actionName)
    }
}

interface Action<T extends ActionBaseConfig> {
    execute(String actionName, Closure<T> actionClosure)
}

class PublishAction implements Action<PublishConfig> {

    private MqttConnection mqttConnection;

    PublishAction(MqttConnection mqttConnection) {
        this.mqttConnection = mqttConnection
    }

    @Override
    def execute(String actionName, Closure<PublishConfig> actionClosure) {
        // Пересоздаем ActionConfig перед каждым выполнением
        def action = actionClosure.call()

        if (action.before) {
            action.before()
        }

        mqttConnection.publish(action.topic, action.payload, action.qos, action.retain, action.expiryInterval)

        if (action.after) {
            action.after()
        }
    }
}

class JustAction implements Action<ActionConfig> {

    @Override
    def execute(String actionName, Closure<ActionConfig> actionClosure) {
        // Пересоздаем ActionConfig перед каждым выполнением
        def action = actionClosure.call() as ActionConfig
        if (action.before) {
            action.before()
        }

        action.justDo()

        if (action.after) {
            action.after()
        }
    }
}
