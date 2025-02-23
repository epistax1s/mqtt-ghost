package space.left4dev.cli

import space.left4dev.handler.MqttClientHandler

interface Command {
    void execute(String[] args, MqttClientHandler handler)
}
