package space.left4dev.handler

import space.left4dev.config.subscribe.SubscribeConfig
import groovy.json.JsonSlurper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MessageProcessor {

    private static final Logger log = LoggerFactory.getLogger(MessageProcessor)

    private final ActionExecutor actionExecutor
    private final Map<String, Object> properties

    MessageProcessor(ActionExecutor actionExecutor, Map<String, Object> properties) {
        this.actionExecutor = actionExecutor
        this.properties = properties
    }

    void process(SubscribeConfig handler, Map msg) {
        handler.handler.delegate = [
                properties     : properties,
                parseJson: { String text -> new JsonSlurper().parseText(text) },
                // space.left4dev.handler  : actionExecutor
        ]
        handler.handler.resolveStrategy = Closure.DELEGATE_FIRST
        handler.handler(msg)
    }
}
