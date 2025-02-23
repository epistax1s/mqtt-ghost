package space.left4dev.cli

import space.left4dev.handler.MqttClientHandler

class ExitCommand implements Command {
    @Override
    void execute(String[] args, MqttClientHandler handler) {
        println "Exiting..."
        System.exit(0)
    }
}
