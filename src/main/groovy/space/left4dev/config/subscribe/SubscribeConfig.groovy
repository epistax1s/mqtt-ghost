package space.left4dev.config.subscribe

class SubscribeConfig {

    String topicFilter
    Closure handler

    SubscribeConfig(String topicFilter, Closure handler) {
        this.topicFilter = topicFilter
        this.handler = handler
    }
}
