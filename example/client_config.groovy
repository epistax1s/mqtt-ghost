clientId = "client1"
keepAlive = 120

connect {
    host = "broker.hivemq.com"
    port = 1883
    username = "user"
    password = "pass"
    ssl {
        keystorePath = "/path/to/keystore"
        keystorePassword = "keystorePass"
        truststorePath = "/path/to/truststore"
        truststorePassword = "truststorePass"
    }
}

env.prefix = UUID.randomUUID().toString()

publish("sendHello") {
    before = {
        println("before")
    }
    topic = "mqtt-mimic/${env.prefix}/greetings"
    payload = "Hello, world! Dynamic message = ${UUID.randomUUID().toString()}"
    qos = 1
    retain = false
    schedule {
        interval = 5_000
        startImmediately = true
    }
}

subscribe("mqtt-mimic/${env.prefix}/greetings") { msg ->
    println """
    Receive message!
    Topic : ${msg.topic}
    Payload:
    ${msg.payload}
    """
}

subscribe("mqtt-mimic/${env.prefix}/on") { msg ->
    activateAction("sendHello")
}

subscribe("mqtt-mimic/${env.prefix}/off") { msg ->
    deactivateAction("sendHello")
}
