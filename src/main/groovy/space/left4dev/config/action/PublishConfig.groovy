package space.left4dev.config.action

class PublishConfig extends ActionBaseConfig {

    String topic
    String payload

    int qos = 0 // with default value
    long expiryInterval = 4_294_967_295 // with default value
    boolean retain = false // with default value

}
