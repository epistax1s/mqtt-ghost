package space.left4dev

// Main.groovy

import space.left4dev.cli.CommandLineInterface
import space.left4dev.handler.ActionExecutor
import space.left4dev.handler.ActionManager
import space.left4dev.handler.MqttClientHandler
import space.left4dev.mqtt.HiveMqttClient

class GhostApp {
    static void main(String[] args) {
        if (args.length == 0) {
            println "Usage: groovy Main.groovy <config_file.groovy>"
            System.exit(1)
        }

        def mqttConnection = new HiveMqttClient()
        def actionExecutor = new ActionExecutor(mqttConnection)
        def actionManager = new ActionManager(actionExecutor)

        def handler = new MqttClientHandler(args[0], mqttConnection, actionManager)
        handler.start()

        new CommandLineInterface(handler).run()
    }
}
