---
sidebar_position: 2
---

# Publish

The `publish` component is a named code block in the DSL used to configure and manage the publication of messages to MQTT 
topics. Each `publish` block has a unique name (e.g., `"sendHello"`), allowing it to be invoked, scheduled, or controlled 
from other parts of the DSL. The minimal configuration requires only a topic (`topic`) and message content (`payload`). All
other parameters are optional and can be used as needed to customize behavior, such as scheduling, adding logic before 
and after sending, or setting QoS and retention options.

## Basic Syntax

A `publish` block is defined with a name followed by a closure that specifies its properties. Here’s a minimal example:

```groovy
publish("sendHello") {
    topic = "greetings"
    payload = "Hello, world!"
}
```

This creates a `publish` action named `"sendHello"` that sends `"Hello, world!"` to the `greetings` topic when triggered.

## Properties

### 1. `topic`

* **Description**: The MQTT topic to publish the message to.
* **Type**: String.
* **Required**: Yes.

**Example**:
```groovy
topic = "mqtt-mimic/test/greetings"
```
### 2. `payload`

* **Description**: The content of the message to publish. Can include dynamic Groovy expressions for real-time customization.
* **Type**: String.
* **Required**: Yes.

**Example**:
```groovy
payload = "Hello, world! ID: ${UUID.randomUUID()}"
```

### 3. `qos`

* **Description**: The Quality of Service level for the message (0, 1, or 2).
* **Type**: Integer.
* **Required**: No.
* **Default**: 0.

**Example**:
```groovy
qos = 1
```

### 4. `retain`

* **Description**: Indicates whether the broker should retain the message as the last known value for the topic.
* **Type**: Boolean.
* **Required**: No.
* **Default**: false.

**Example**:
```groovy
retain = true
```

### 5. `expiryInterval`

* **Description**: The time (in seconds) after which the message expires on the broker.
* **Type**: Long.
* **Required**: No.
* **Default**: 4,294,967,295 (maximum value).

**Example**:
```groovy
expiryInterval = 3600
```

### 6. `before`

* **Description**: A closure executed before the message is published. Useful for adding custom logic or dynamically modifying the configuration.
* **Type**: Closure.
* **Required**: No.

**Example**:
```groovy
before = { 
    println "Sending message..." 
}
```

### 7. `after`

* **Description**: A closure executed after the message is published. Useful for follow-up actions or logging.
* **Type**: Closure.
* **Required**: No.

**Example**:
```groovy
after = { 
    println "Message sent!" 
}
```

### 8. `schedule` (Scheduling Block)

* **Description**: A block for configuring periodic execution of the `publish` action.
* **Required**: No.
* **Subparameters**:
  * `interval`: The interval (in milliseconds) between executions.
    * **Type**: Long.
    * **Required**: Yes (within the schedule block).
  * `startImmediately`: Whether the schedule starts immediately when the client starts.
    * **Type**: Boolean.
    * **Required**: No.
    * **Default**: false.

**Example**:
```groovy
schedule {
    interval = 5_000  // Every 5 seconds
    startImmediately = true
}
```

## Key Features

### 1. Named Component

Each publish block has a unique name, enabling it to be invoked manually via doAction("sendHello") or its schedule to be 
controlled with activateAction("sendHello") and deactivateAction("sendHello").

### 2. Dynamic Configuration

The configuration is rebuilt each time the action runs, allowing dynamic values in topic and payload (e.g., `${env.prefix}`
or `${UUID.randomUUID()}`). The before and after closures can also modify the configuration or external context on the fly.

### 3. Scheduling

The schedule block enables periodic message publishing. When activated, the action runs at the specified interval. 
If startImmediately is true, it begins as soon as the client starts; otherwise, it requires explicit activation.

## Controlling Publish Execution

The execution of a `publish` action can be controlled using three key functions:

- `doAction("name")`
  - Immediately triggers the named `publish` action, sending the message once based on its current configuration. This works 
  regardless of whether a schedule is defined or active.
  - **Example**: `doAction("sendHello")` sends the message defined in the "sendHello" block right away.
- ` activateAction("name")`
  - Activates the named `publish` action’s schedule, if one is defined. Once activated, the action will run periodically 
  according to the `interval` set in the `schedule` block until deactivated or the client stops.
  - **Example**: `activateAction("sendHello")` starts the scheduled publishing for `"sendHello"`.
- `deactivateAction("name")`
  - Deactivates the named `publish` action’s schedule, stopping periodic execution. The action will no longer publish 
  messages automatically until reactivated with `activateAction` or triggered manually with `doAction`.
  - **Example**: `deactivateAction("sendHello")` pauses the scheduled publishing for `"sendHello"`.

These functions can be called from anywhere in the DSL – within `subscribe`, `action`, or standalone code – giving you full 
control over when and how the `publish` action executes.

## Usage Examples

### 1. Simple Publish

A one-time message publication:

```groovy
publish("sayHi") {
    topic = "hello"
    payload = "Hi there!"
    qos = 1
}

doAcion("sayHi")
```

### 2. Scheduled Publish

Sending a message every 5 seconds:

```groovy
publish("sendHello") {
    topic = "greetings"
    payload = "Hello, world! Msg #${UUID.randomUUID()}"
    qos = 1
    schedule {
        interval = 5_000
        startImmediately = true
    }
}
```

### 3. Dynamic Publish with Control

Publishing with a dynamic topic and control via subscriptions:

```groovy
env.prefix = UUID.randomUUID().toString()

publish("sendHello") {
    before = { println "Before sending..." }
    topic = "mqtt-mimic/${env.prefix}/greetings"
    payload = "Hello! ID: ${UUID.randomUUID()}"
    qos = 1
    retain = false
    schedule {
        interval = 5_000
        startImmediately = false  // Waits for activation
    }
    after = { println "Message sent!" }
}

subscribe("mqtt-mimic/${env.prefix}/on") { msg ->
    // Starts periodic publishing
    activateAction("sendHello")  
}

subscribe("mqtt-mimic/${env.prefix}/off") { msg ->
    // Stops the schedule; no messages are published until reactivated or triggered manually
    deactivateAction("sendHello")
}
```

__Details__: Here, the schedule doesn’t start automatically (`startImmediately = false`). When activateAction(`"sendHello"`) 
is called, publishing begins every 5 seconds. Calling deactivateAction(`"sendHello"`) stops the schedule, halting automatic 
messages. The action remains inactive until `activateAction` is called again or `doAction("sendHello")` triggers a one-time 
publication.

### 4. Manual Trigger from Arbitrary Code

Immediate publishing via `doAction`:

```groovy
publish("manualSend") {
    topic = "test"
    payload = "Test message"
}

action("triggerSend") {
    doAction("manualSend")
}

// Or invoke directly
doAction("manualSend")
```

## Notes and Recommendations

1. **Required Parameters**: Ensure topic and payload are specified, or the publication won’t occur.
2. **Dynamic Values**: Use environment variables (e.g., `env.prefix`) or Groovy expressions in payload and topic to create
unique messages.
3. **Scheduling Control**: Decide whether the action should start immediately (startImmediately) or be controlled manually
with activateAction/deactivateAction. Without a schedule or manual trigger, the action won’t run on its own.
4. **Error Handling**: If the publication fails (e.g., due to an unavailable broker), an error message will appear in the
logs.
