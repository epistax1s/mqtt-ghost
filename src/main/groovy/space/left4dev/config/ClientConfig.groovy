package space.left4dev.config

import space.left4dev.config.action.ActionBaseConfig
import space.left4dev.config.connect.ConnectConfig
import space.left4dev.config.subscribe.SubscribeConfig
import space.left4dev.utils.TypedClosure

class ClientConfig {

    String clientId
    int keepAlive = 60

    ConnectConfig connect

    Map<String, TypedClosure<ActionBaseConfig>> actions = [:]
    List<SubscribeConfig> messageHandlers = []

}
