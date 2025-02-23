package space.left4dev.mqtt

import space.left4dev.config.connect.ConnectConfig

interface MqttConnection {

    void connect(ConnectConfig config)

    void disconnect()

    void subscribe(String topicFilter, int qos, Closure callback)

    void publish(String topic, String payload, int qos, boolean retain, long expiryInterval)

}
