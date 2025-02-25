---
sidebar_position: 1
---

# Temperature Sensor Emulation

## Goal

Create a virtual temperature sensor that periodically sends random temperature readings between 10–40°C and 
responds to `on`/`off` commands via MQTT.

**Code**:
```groovy
// Configure connection to the MQTT broker (using a public broker for testing)
connect {
    host = "broker.hivemq.com"  // Hostname of the MQTT broker
    port = 1883                 // Default MQTT port
}

// Set up environment variables to store device ID and its initial state
env.deviceId = "sensor_001"     // Unique identifier for the sensor
env.isActive = false            // Sensor starts in an inactive state

// Define an action to periodically publish temperature readings
action("publishTemperature") {
    justDo = {
        if (env.isActive) {     // Only proceed if the sensor is active
            // Generate a random temperature value between 10 and 40°C
            def temperature = new Random().nextInt(30) + 10
            // Trigger the sendTemperature action to publish the value
            doAction("sendTemperature", temperature)
        }
    }
    schedule {
        interval = 10_000       // Execute every 10 seconds
        startImmediately = false // Do not start until explicitly activated
    }
}

// Define the action to publish temperature data to an MQTT topic
publish("sendTemperature") {
    topic = "sensors/${env.deviceId}/temperature"  // Dynamic topic based on device ID
    payload = { temperature -> "Temperature: $temperature°C" }  // Format the message
    qos = 1                 // Quality of Service level 1 (at least once delivery)
}

// Subscribe to a command topic to control the sensor (ON/OFF)
subscribe("sensors/${env.deviceId}/command") { msg ->
    if (msg.payload == "ON") {      // Check if the command is to turn the sensor ON
        env.isActive = true         // Set the sensor to active
        activateAction("publishTemperature")  // Start the scheduled publishing
        println "Sensor activated"  // Log activation for debugging
    } else if (msg.payload == "OFF") {  // Check if the command is to turn the sensor OFF
        env.isActive = false        // Set the sensor to inactive
        deactivateAction("publishTemperature")  // Stop the scheduled publishing
        println "Sensor deactivated"  // Log deactivation for debugging
    }
}
```

## How it works:

1. **Connection**: The sensor connects to an MQTT broker at `broker.hivemq.com` on port `1883`.
2. **Initialization**: The device gets a unique ID (`sensor_001`) and starts in an inactive state (`isActive = false`).
3. **Publishing**: The `publishTemperature` action generates a random temperature and sends it via the `sendTemperature` 
action to the topic `sensors/sensor_001/temperature` every 10 seconds, but only when active.
4. **Control**: The sensor listens to the `sensors/sensor_001/command` topic for `ON` or `OFF` commands to start or stop publishing.

**Result**: This emulates a real temperature sensor that can be controlled remotely and publishes data on a schedule.
