// space.left4dev.handler/MqttClientHandler.groovy
package space.left4dev.handler

import space.left4dev.config.ClientConfig
import space.left4dev.dsl.DslBuilder
import space.left4dev.mqtt.MqttConnection
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MqttClientHandler {
    private static final Logger log = LoggerFactory.getLogger(MqttClientHandler)

    private final ClientConfig config
    private final MqttConnection mqttConnection
    private final MessageProcessor messageProcessor

    private final ActionManager actionManager

    MqttClientHandler(String configScript,
                      MqttConnection mqttConnection,
                      ActionManager actionManager) {

        this.actionManager = actionManager
        this.config = loadConfig(configScript)
        this.mqttConnection = mqttConnection
        // TODO change null
        this.messageProcessor = new MessageProcessor(null, config.properties)
    }

    // noinspection all
    private ClientConfig loadConfig(String scriptPath) {
        def shell = new GroovyShell()

        // Формируем корректный скрипт с импортами НА УРОВНЕ СКРИПТА
        // language=no
        def fullScript = """
            import space.left4dev.config.*
            import space.left4dev.dsl.*
            import groovy.json.JsonSlurper
            
            // Возвращаем замыкание с логикой из файла
            { -> 
                ${new File(scriptPath).text}
            }
        """

        // Выполняем скрипт, чтобы получить замыкание
        def closure = shell.evaluate(fullScript) as Closure

        DslBuilder.build(closure, actionManager)
    }

    void start() {
        // connect
        mqttConnection.connect(config.connect)

        // process subscriptions
        config.messageHandlers.each { handler ->
            mqttConnection.subscribe(handler.topicFilter, 1, {
                Map msg -> messageProcessor.process(handler, msg)
            })
        }

        // process actions
        config.actions.each { name, action ->
            actionManager.registerAction(name, action)
        }
    }
}
