---
sidebar_position: 3
---

# Subscribe

The subscribe component is used to subscribe to MQTT topics and process incoming messages. It takes a topic 
filter and a closure that runs for each message, enabling subscription and handling with details about the topic and content.

## Basic Syntax

The `subscribe` block specifies a topic filter and a handler:

```groovy
subscribe("greetings") { msg ->
    println "Received: ${msg.payload}"
}
```

Subscribes to the `greetings` topic and prints the message content.

## Properties

### 1. `topicFilter`

* **Description**: The MQTT topic filter (supports `+` and `#` or dynamic Groovy expressions).
* **Type**: String.
* **Required**: Yes.
* **Example**: `"mqtt-ghost/${env.prefix}/greetings"`

### 2. `handler`

* **Description**: A closure to process messages, receiving an msg object.
* **Type**: Closure.
* **Required**: Yes.
* **Example**: `{ msg -> println msg.payload }`

### The msg Object

The `msg` object is passed to the handler and contains:

* **topic**: Topic name (string).
* **payload**: Message content (string, UTF-8).
* **qos**: QoS level (0, 1, or 2).
* **retain**: Retained message flag (`true`/`false`).

**Example**:
```groovy
subscribe("test/#") { msg ->
    println "${msg.topic}: ${msg.payload}"
}
```

## Key Features

1. **Filters**: Supports `+` (single level) and `#` (all sublevels), e.g., `sensors/#`.
2. **Dynamic**: Filters can use Groovy expressions, like `${env.prefix}`.
3. **Processing**: The handler gets `msg` for custom logic.
4. **Interaction**: Call `doAction`, `activateAction`, or `deactivateAction` from the handler.

## Usage Examples

### 1. Simple Subscription

```groovy
subscribe("greetings") { msg ->
    println msg.payload
}
```

### 2. Dynamic Filter

```groovy
env.prefix = UUID.randomUUID().toString()
subscribe("mqtt-ghost/${env.prefix}/greetings") { msg ->
    println "Topic: ${msg.topic}, Data: ${msg.payload}"
}
```

### 3. Wildcard Subscription

```groovy
subscribe("sensors/#") { msg ->
    println "${msg.topic}: ${msg.payload}"
}
```

### 4. Controlling Actions

```groovy
env.prefix = UUID.randomUUID().toString()
publish("sendHello") {
    topic = "mqtt-ghost/${env.prefix}/greetings"
    payload = "Hello!"
    schedule { interval = 5_000; startImmediately = false }
}
subscribe("mqtt-ghost/${env.prefix}/on") { msg ->
    activateAction("sendHello")
}
```

## Notes

1. **Required**: Both `topicFilter` and `handler` must be provided.
2. **Filters**: Use `+` and `#` carefully; check `msg.topic` if needed.
3. **Timeout**: Subscription has a 10-second timeout; check logs for errors.

