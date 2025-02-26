---
sidebar_position: 4
---

# Action

The `action` component is a named code block in the DSL designed to execute arbitrary logic. Unlike `publish`, which focuses
on sending MQTT messages, `action` is for running general-purpose code. It can be triggered manually, scheduled to run 
periodically, or controlled via activation and deactivation.

## Basic Syntax

An `action` block is defined with a name and a closure containing the logic to execute. Here’s a minimal example:

```groovy
action("sayHello") {
    justDo = {
        println "Hello from action!"
    }
}
```

This creates an action named `"sayHello"` that prints a message when invoked.

## Properties

### 1. `justDo`

* **Description**: The closure containing the main logic to execute when the action runs.
* **Type**: Closure.
* **Required**: Yes.

**Example**:
```groovy
justDo = { 
    println "Doing some work" 
}
```

### 2. `schedule` (Scheduling Block)

* **Description**: A block to configure periodic execution of the action.
* **Required**: No.
* **Subparameters**:
  * `interval`: The interval (in milliseconds) between executions.
    * **Type**: Long.
    * **Required**: Yes (within the block).
    * **Example**: `interval = 5_000`
  * `startImmediately`: Whether the schedule starts immediately on client startup.
    * **Type**: Boolean.
    * **Required**: No.
    * **Default**: false.
    * **Example**: `startImmediately = true`

**Example**:
```groovy
schedule {
    interval = 5_000
    startImmediately = true
}
```

## Key Features

1. **Arbitrary Logic**: `justDo` allows execution of any code, not tied to MQTT publishing.
2. **Scheduling**: Use `schedule` to run the action at regular intervals.
3. **Control**: Supports manual invocation with `doAction`, and schedule control with `activateAction` and `deactivateAction`.
4. **Dynamic**: The configuration is rebuilt each time the action runs, enabling use of dynamic data.

## Controlling Execution

- `doAction("name")`: Immediately runs the action once.
- `activateAction("name")`: Activates the action’s schedule, if defined, starting periodic execution.
- `deactivateAction("name")`: Deactivates the schedule, stopping automatic execution until reactivated or manually triggered.

These functions can be called from anywhere in the DSL – within subscribe, publish, or standalone code.

## Usage Examples

### 1. Simple Action

Running code on demand:

```groovy
action("sayHello") {
    justDo = {
        println "Hello!"
    }
}
doAction("sayHello")
```

### 2. Scheduled Action

Running every 5 seconds:

```groovy
action("tick") {
    justDo = {
        println "Tick: ${new Date()}"
    }
    schedule {
        interval = 5_000
        startImmediately = true
    }
}
```

### 3. Dynamic Action with Control

Controlled via subscription:

```groovy
env.prefix = UUID.randomUUID().toString()

action("logTime") {
    justDo = {
        println "Time: ${new Date()}"
    }
    schedule {
        interval = 10_000
        startImmediately = false
    }
}

subscribe("mqtt-ghost/${env.prefix}/on") { msg ->
    activateAction("logTime")
}

subscribe("mqtt-ghost/${env.prefix}/off") { msg ->
    deactivateAction("logTime")
}
```

### 4. Interaction with Publish

Triggering a publish `action`:

```groovy
action("triggerSend") {
    justDo = {
        doAction("sendHello")
    }
}

publish("sendHello") {
    topic = "greetings"
    payload = "Message from action!"
}

doAction("triggerSend")
```

## Notes

* **Required**: Specify `justDo`, or the action won’t do anything useful.
* **Execution**: Without `schedule` or `doAction`, the action won’t run on its own.
* **Flexibility**: Use it for tasks beyond MQTT, like logging or calculations.
