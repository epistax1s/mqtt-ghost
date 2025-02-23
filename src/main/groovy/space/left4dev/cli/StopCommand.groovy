package space.left4dev.cli

import space.left4dev.handler.MqttClientHandler

class StopCommand implements Command {
    @Override
    void execute(String[] args, MqttClientHandler handler) {
        if (args.length < 2) {
            println "Usage: stop <actionName>"
            return
        }
        handler.actionExecutor.stopAction(args[1])
    }
}
