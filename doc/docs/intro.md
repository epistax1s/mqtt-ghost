---
sidebar_position: 2
---

# Build from Source

This section explains how to build and run `mqtt-mimic`.

## Prerequisites

- **Java 21+**: The utility requires Java 21 or higher.
- A `*.groovy` configuration file (see `example/client_config.groovy` for an example).

## Steps

### 1. **Install Java 21**

Ensure Java 21 or higher is installed. For convenience, you can use SDKMAN!:

```bash
sdk install java 21.0.2-open
sdk use java 21.0.2-open
```

Verify the version:

```bash
java -version
```

Example output:

```text
openjdk 21.0.2 2024-01-16
OpenJDK Runtime Environment (build 21.0.2+13-58)
OpenJDK 64-Bit Server VM (build 21.0.2+13-58, mixed mode, sharing)
```

### 2. Build the Project

Navigate to the project directory:

```bash
cd mqtt-mimic
```

Clean and build the JAR file using the Gradle Wrapper:

```bash
./gradlew clean
./gradlew shadowJar
```

This creates an executable JAR at build/libs/mqtt-mimic-1.0-SNAPSHOT-all.jar.

### 3. Run the Utility

Launch the utility with your client_config.groovy file:

```bash
java -jar build/libs/mqtt-mimic-1.0-SNAPSHOT-all.jar ./client_config.groovy
```

### Notes

- The project uses the Gradle Wrapper (`./gradlew`), so a separate Gradle installation is not required.
- Check `example/client_config.groovy` for a sample configuration.
- Detailed configuration and usage instructions are available in the separate documentation.
G