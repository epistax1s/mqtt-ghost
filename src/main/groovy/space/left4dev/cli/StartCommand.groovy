package space.left4dev.cli

import space.left4dev.handler.MqttClientHandler

class StartCommand implements Command {
    @Override
    void execute(String[] args, MqttClientHandler handler) {
        if (args.length < 2) {
            println "Usage: start <actionName>"
            return
        }

        def actionName = args[1]
        handler.actionExecutor.startAction(actionName, handler.config.actions[actionName])
    }
}
