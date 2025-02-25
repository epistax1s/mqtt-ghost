# MQTT Mimic

Documentation is available at [https://epistax1s.github.io/mqtt-mimic](https://epistax1s.github.io/mqtt-mimic).

A utility for emulating MQTT clients and smart devices, executing scenarios based on DSL scripts.

## Quick Start

This section provides the fastest way to get started with mqtt-mimic by downloading a pre-built JAR file from GitHub
Releases, skipping the build process.

### Prerequisites

- `Java 21+`: Ensure Java 21 or higher is installed.
- A `*.groovy` configuration file (see `example/client_config.groovy` for a sample).

### Steps

#### 1. Download the JAR
Visit the GitHub Releases page for mqtt-mimic and download the latest `mqtt-mimic-X.X.X.jar` file
(e.g., `mqtt-mimic-1.0.jar`).

Alternatively, use wget from the command line:

```bash
wget https://github.com/your-repo/mqtt-mimic/releases/download/vX.X.X/mqtt-mimic-X.X.X-all.jar
```

#### 2. Run the Utility
Place the downloaded JAR and your `client_config.groovy` file in the same directory, then execute:

```bash
java -jar mqtt-mimic-1.0.jar ./client_config.groovy
```

Enjoy emulating smart devices over MQTT with MQTT Mimic 😊!


## Or ... build From Source

### Prerequisites

- **Java 21+**: The utility requires Java 21 or higher.
- A `*.groovy` configuration file (see `example/client_config.groovy` for an example).

### Steps

#### 1. **Install Java 21**

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

#### 2. Clone the Project

Clone the mqtt-mimic repository from GitHub:

```bash
git clone git@github.com:epistax1s/mqtt-mimic.git
```

#### 3. Build the Project

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

#### 4. Run the Utility

Launch the utility with your `client_config.groovy` file:

```bash
java -jar build/libs/mqtt-mimic-1.0.jar ./client_config.groovy
```

### Notes

- The project uses the Gradle Wrapper (gradlew), so a separate Gradle installation is not required.
- Check `example/client_config.groovy` for a sample configuration.
- Detailed configuration and usage instructions are available in the 
[separate documentation](https://epistax1s.github.io/mqtt-mimic).