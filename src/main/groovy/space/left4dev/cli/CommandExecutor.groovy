package space.left4dev.cli

import space.left4dev.handler.MqttClientHandler

class CommandExecutor {
    private final Map<String, Command> commands = [
            "start": new StartCommand(),
            "stop": new StopCommand(),
            "exit": new ExitCommand()
    ]

    void execute(String input, MqttClientHandler handler) {
        def parts = input.trim().split("\\s+")
        def commandName = parts[0].toLowerCase()
        def command = commands[commandName]
        if (command) {
            command.execute(parts, handler)
        } else {
            println "Unknown command: $commandName"
        }
    }

    void registerCommand(String name, Command command) {
        commands[name] = command
    }
}