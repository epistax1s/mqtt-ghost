package space.left4dev.cli

import space.left4dev.handler.MqttClientHandler

class CommandLineInterface {
    private final CommandExecutor executor
    private final MqttClientHandler handler

    CommandLineInterface(MqttClientHandler handler) {
        this.handler = handler
        this.executor = new CommandExecutor()
    }

    void run() {
        Thread.start {
            def reader = new BufferedReader(new InputStreamReader(System.in))
            while (true) {
                print "> "
                def line = reader.readLine()?.trim()
                if (line) executor.execute(line, handler)
            }
        }
    }
}