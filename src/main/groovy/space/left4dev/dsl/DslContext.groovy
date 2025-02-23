package space.left4dev.dsl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import space.left4dev.config.ClientConfig
import space.left4dev.config.action.ActionConfig
import space.left4dev.config.action.ActionType
import space.left4dev.config.action.PublishConfig
import space.left4dev.config.connect.ConnectConfig
import space.left4dev.config.subscribe.SubscribeConfig
import space.left4dev.handler.ActionManager
import space.left4dev.utils.TypedClosure

class DslContext {

    private static final Logger log = LoggerFactory.getLogger(DslContext)

    private final ClientConfig config
    private final ActionManager actionManager

    final Map<String, Object> env = [:]

    DslContext(ClientConfig config, ActionManager actionManager) {
        this.config = config
        this.actionManager = actionManager
    }

    void connect(Closure closure) {
        config.connect = new ConnectConfig()
        closure.delegate = config.connect
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }

    void action(String name, Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        config.actions[name] = new TypedClosure<>(
                ActionType.ACTION.name(),
                {
                    def newAction = new ActionConfig()
                    closure.rehydrate(newAction, this, this).call()
                    newAction // Явно возвращаем новый ActionConfig
                }
        )
    }

    void publish(String name, Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        config.actions[name] = new TypedClosure<>(
                ActionType.PUBLISH.name(),
                {
                    def newAction = new PublishConfig()
                    def cl2 = closure.rehydrate(newAction, this, this)
                    cl2.call()
                    newAction // Явно возвращаем новый ActionConfig
                }
        )
    }

    void subscribe(String topicFilter, Closure handler) {
        config.messageHandlers << new SubscribeConfig(topicFilter, handler)
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

    def propertyMissing(String name) {
        log.debug("propertyMissing() [get env] name = {}", name)
        env[name]
    }

    void propertyMissing(String name, value) {
        log.debug("propertyMissing [put env] name = {}, value = {}", name, value)
        env[name] = value
    }
}
