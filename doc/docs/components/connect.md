---
sidebar_position: 1
---

# Connect

## Basic Syntax

The `connect` component is a code block in the DSL where connection parameters are defined. The minimal configuration 
requires only the broker’s address (`host`) and port (`port`). All other parameters are optional and can be used as needed.

```groovy
connect {
    host = "broker.hivemq.com"
    port = 1883
}
```

This example connects an MQTT client to the public HiveMQ broker at broker.hivemq.com via port 1883.

## Full List of Parameters

### 1. `host`

- **Description**: The address of the MQTT broker (domain name or IP address).
- **Type**: String.
- **Required**: Yes.

**Example**:
```groovy
host = "broker.hivemq.com"
```

### 2. `port`

* **Description**: The port through which the client connects to the broker.
* **Type**: Integer.
* **Required**: Yes.

**Example**:
```groovy
port = 1883
```

### 3. `clientId`

* **Description**: A unique identifier for the client. If not specified, a random `UUID` is automatically generated.
* **Type**: String.
* **Required**: No.

**Example**:
```groovy
clientId = "my-mqtt-client-001"
```

### 4. `username`

* **Description**: The username for authenticating with the broker.
* **Type**: String.
* **Required**: No (used only if the broker requires authentication).

**Example**:
```groovy
username = "user123"
```

### 5. `password`

* **Description**: The password for authenticating with the broker.
* **Type**: String.
* **Required**: No (used alongside username).

**Example**:
```groovy
password = "secretpass"
```

### 6. `keepAlive`

* **Description**: The interval (in seconds) at which the client sends "keep-alive" messages to maintain the connection.
* **Type**: Integer.
* **Required**: No.
* **Default**: 60 seconds.

**Example**:
```groovy
keepAlive = 30
```

### 7. `cleanStart`

* **Description**: Specifies whether to start the session with a clean state (true) or restore a previous session (false).
* **Type**: Boolean.
* **Required**: No.
* **Default**: `true`.

**Example**:
```groovy
cleanStart = false
```

### 8. `sessionExpiryInterval`

* **Description**: The time (in seconds) for which the session is retained on the broker after the client disconnects.
* **Type**: Integer.
* **Required**: No.
* **Default**: 0 (session is not retained).

**Example**:
```groovy
sessionExpiryInterval = 3600
```

### 9. `ssl` (SSL Settings Block)

* **Description**: A block for configuring a secure connection via SSL/TLS.
* **Required**: No (used only for secure connections).
* **Subparameters**:
  * `enabled`: Enables SSL usage (default: false).
  * `keystorePath`: Path to the KeyStore file in JKS format.
  * `keystorePassword`: Password for accessing the KeyStore.
  * `truststorePath`: Path to the TrustStore file in JKS format.
  * `truststorePassword`: Password for accessing the TrustStore.

**Example**:
```groovy
ssl {
    enabled = true
    keystorePath = "/path/to/keystore.jks"
    keystorePassword = "keystorePass"
    truststorePath = "/path/to/truststore.jks"
    truststorePassword = "truststorePass"
}
```

### 10. `will` (Last Will and Testament Block)

* **Description**: A block for configuring the "Last Will and Testament" (LWT) message, which the broker sends on behalf of the client upon an unexpected disconnection.
* **Required**: No.
* **Subparameters**:
  * `topic`: The topic to which the LWT message will be sent.
  * `payload`: The content of the message (string).
  * `qos`: Quality of Service level (0, 1, or 2). Default: 1.
  * `retain`: Flag indicating whether the message should be retained (true/false). Default: false.

```groovy
will {
    topic = "status/my-client"
    payload = "Client offline"
    qos = 1
    retain = true
}
```


## Automatic Reconnection

The `connect` component automatically enables a reconnection mechanism if the connection to the broker is lost. 
The reconnection settings are fixed:

* Initial delay before retry: `1 second`.
* Maximum delay between retries: `10 seconds`.

These parameters are not configurable via the DSL but ensure the client’s reliability.

## Usage Examples

### 1. Minimal Configuration

Connecting to a public broker without authentication or SSL:

```groovy
connect {
    host = "broker.hivemq.com"
    port = 1883
}
```

### 2. Connection with Authentication

Connecting to a broker with a username and password:

```groovy
connect {
    host = "mybroker.com"
    port = 1883
    username = "user123"
    password = "secretpass"
}
```

### 3. Connection with SSL

Connecting to a broker via a secure connection using KeyStore and TrustStore:

```groovy
connect {
    host = "securebroker.com"
    port = 8883
    ssl {
        enabled = true
        keystorePath = "/path/to/keystore.jks"
        keystorePassword = "keystorePass"
        truststorePath = "/path/to/truststore.jks"
        truststorePassword = "truststorePass"
    }
}
```

### 4. Connection with Last Will and Testament

Connecting with an LWT setup to report the client’s status upon disconnection:

```groovy
connect {
    host = "broker.example.com"
    port = 1883
    username = "user"
    password = "pass"
    will {
        topic = "clients/status"
        payload = "Client disconnected unexpectedly"
        qos = 2
        retain = true
    }
}
```

### 5. Full Configuration

An example using all major parameters:

```groovy
connect {
    host = "broker.example.com"
    port = 1883
    clientId = "client-001"
    username = "admin"
    password = "admin123"
    keepAlive = 20
    cleanStart = false
    sessionExpiryInterval = 7200
    ssl {
        enabled = true
        keystorePath = "/secure/keystore.jks"
        keystorePassword = "keypass"
        truststorePath = "/secure/truststore.jks"
        truststorePassword = "trustpass"
    }
    will {
        topic = "client/status"
        payload = "Offline"
        qos = 1
        retain = false
    }
}
```

## Notes and Recommendations

1. **Required Parameters**: Ensure host and port are always specified, or the connection will not be established.
2. **SSL**: If the broker requires a secure connection (typically port 8883), enable the ssl block and provide valid paths to 
KeyStore and TrustStore files.
3. **LWT**: Use will if it’s important to notify other devices of the client’s status upon disconnection.
4. **Errors**: If the connection fails (e.g., due to incorrect credentials or an unavailable broker), an error message will 
appear in the logs, and an exception will be thrown.
