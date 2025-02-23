package space.left4dev.mqtt

import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttClientSslConfig
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect
import com.hivemq.client.mqtt.mqtt5.message.disconnect.Mqtt5DisconnectReasonCode
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import space.left4dev.config.connect.ConnectConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class HiveMqttClient implements MqttConnection {

    private static final Logger log = LoggerFactory.getLogger(HiveMqttClient)

    private Mqtt5AsyncClient client

    private final Executor executor = Executors.newFixedThreadPool(4)

    @Override
    void connect(ConnectConfig config) {
        def clientBuilder = MqttClient.builder()
                .useMqttVersion5()
                .identifier(config.clientId ?: UUID.randomUUID().toString())
                .serverHost(config.host)
                .serverPort(config.port)
                .automaticReconnect()
                .initialDelay(1, TimeUnit.SECONDS)
                .maxDelay(10, TimeUnit.SECONDS)
                .applyAutomaticReconnect()

        if (config.ssl?.enabled) {
            def sslConfigBuilder = MqttClientSslConfig.builder()

            // KeyStore conf
            if (config.ssl.keystorePath) {
                def keyStore = KeyStore.getInstance("JKS")
                new FileInputStream(config.ssl.keystorePath).withCloseable { input ->
                    keyStore.load(input, config.ssl.keystorePassword?.toCharArray() ?: null)
                }
                def kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
                kmf.init(keyStore, config.ssl.keystorePassword?.toCharArray() ?: null)
                sslConfigBuilder.keyManagerFactory(kmf)
            }

            // TrustStore conf
            if (config.ssl.truststorePath) {
                def trustStore = KeyStore.getInstance("JKS")
                new FileInputStream(config.ssl.truststorePath).withCloseable { input ->
                    trustStore.load(input, config.ssl.truststorePassword?.toCharArray() ?: null)
                }
                def tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                tmf.init(trustStore)
                sslConfigBuilder.trustManagerFactory(tmf)
            }

            clientBuilder.sslConfig(sslConfigBuilder.build())
        }

        client = clientBuilder.buildAsync()

        def connectOptions = Mqtt5Connect.builder()
                .keepAlive(config.keepAlive ?: 60)
                .cleanStart(config.cleanStart ?: true)
                .sessionExpiryInterval(config.sessionExpiryInterval ?: 0)

        // LWT conf
        if (config.will) {
            connectOptions.willPublish(
                    Mqtt5Publish.builder()
                            .topic(config.will.topic)
                            .payload(config.will.payload?.getBytes(StandardCharsets.UTF_8))
                            .qos(MqttQos.fromCode(config.will.qos ?: 1))
                            .retain(config.will.retain ?: false)
                            .build()
            )
        }

        if (config.username) {
            connectOptions.simpleAuth()
                    .username(config.username)
                    .password(config.password?.getBytes(StandardCharsets.UTF_8))
                    .applySimpleAuth()
        }

        try {
            client.connect(connectOptions.build()).get(10, TimeUnit.SECONDS)
            log.info("Connected to MQTT broker at ${config.host}:${config.port}")
        } catch (Exception e) {
            log.error("Failed to connect to MQTT broker: ${e.message}")
            throw e
        }
    }

    @Override
    void disconnect() {
        client.disconnectWith()
                .reasonCode(Mqtt5DisconnectReasonCode.NORMAL_DISCONNECTION)
                .send()

        log.info("Disconnected from MQTT broker")
    }

    @Override
    void subscribe(String topicFilter, int qos, Closure callback) {
        client.subscribeWith()
                .topicFilter(topicFilter)
                .qos(MqttQos.fromCode(qos))
                .callback { Mqtt5Publish publish ->
                    log.info("input topic <=== topicFilter = ${topicFilter}")
                    def payloadStr = new String(publish.payloadAsBytes, StandardCharsets.UTF_8)
                    def msg = [
                            topic  : publish.topic.toString(),
                            payload: payloadStr,
                            qos    : publish.qos,
                            retain : publish.retain
                    ]
                    callback(msg)
                }
                .send()
                .orTimeout(10, TimeUnit.SECONDS)

        log.info("subscribe() topicFilter = ${topicFilter}, qos = ${qos}")
    }

    @Override
    void publish(String topic,
                 String payload,
                 int qos,
                 boolean retain,
                 long expiryInterval) {

        def publish = Mqtt5Publish.builder()
                .topic(topic)
                .payload(payload.getBytes(StandardCharsets.UTF_8))
                .qos(MqttQos.fromCode(qos))
                .retain(retain)
                .messageExpiryInterval(expiryInterval)
                .build()

        client.publish(publish).whenCompleteAsync({ result, throwable ->
            {
                if (throwable == null) {
                    log.info("Successfully published to $topic with payload: $payload, QoS: $qos, retain: $retain")
                } else {
                    log.error("Failed to publish to $topic with payload: $payload, error: ${throwable.message}", throwable)
                }
            }
        }, executor)
    }
}
