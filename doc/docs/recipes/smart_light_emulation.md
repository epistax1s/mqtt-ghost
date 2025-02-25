---
sidebar_position: 2
---

# Smart Light Emulation with Feedback

## Goal

Create a virtual smart light that accepts `on`/`off` commands and sends back its current status via MQTT.

**Code**:

```groovy
// Configure connection to the MQTT broker for communication
connect {
    host = "broker.hivemq.com"  // Hostname of the MQTT broker
    port = 1883                 // Default MQTT port
}

// Set up environment variables for light ID and its initial status
env.lightId = "light_001"       // Unique identifier for the light
env.lightStatus = "OFF"         // Light starts in the OFF state

// Subscribe to a command topic to receive ON/OFF commands
subscribe("lights/${env.lightId}/command") { msg ->
    if (msg.payload == "ON") {      // If the command is ON
        env.lightStatus = "ON"      // Update the light's status
        doAction("sendStatus")      // Publish the new status
        println "Light turned ON"   // Log the action for debugging
    } else if (msg.payload == "OFF") {  // If the command is OFF
        env.lightStatus = "OFF"     // Update the light's status
        doAction("sendStatus")      // Publish the new status
        println "Light turned OFF"  // Log the action for debugging
    }
}

// Define the action to publish the current status of the light
publish("sendStatus") {
    topic = "lights/${env.lightId}/status"  // Dynamic topic based on light ID
    payload = { "Status: ${env.lightStatus}" }  // Message with current status
    qos = 1                 // Quality of Service level 1 (at least once delivery)
    retain = true           // Retain the message so new subscribers get the latest status
}
```

## How it works:

1. **Connection**: The light connects to the MQTT broker at `broker.hivemq.com` on port `1883`.
2. **Initialization**: The light is assigned an ID (`light_001`) and starts in the `OFF` state.
3. **Command Handling**: The light listens to the `lights/light_001/command` topic for `ON` or `OFF` commands, updating its 
status accordingly and triggering a status update.
4. **Status Publishing**: The `sendStatus` action publishes the current state to `lights/light_001/status` with the `retain`
flag, ensuring new subscribers receive the latest status.

**Result**: This emulates a smart light that responds to commands and provides feedback about its state.