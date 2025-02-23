package space.left4dev.dsl

import space.left4dev.config.ClientConfig
import space.left4dev.config.connect.ConnectConfig
import space.left4dev.handler.ActionManager

class DslBuilder {

    static ClientConfig build(Closure closure, ActionManager actionManager) {
        def config = new ClientConfig()
        def context = new DslContext(config, actionManager)

        closure.delegate = context
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()

        config.connect = config.connect ?: new ConnectConfig()
        return config
    }

}
