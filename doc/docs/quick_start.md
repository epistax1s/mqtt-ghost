---
sidebar_position: 1
---

# Quick Start

This section provides the fastest way to get started with mqtt-mimic by downloading a pre-built JAR file from GitHub 
Releases, skipping the build process.

## Prerequisites

- `Java 21+`: Ensure Java 21 or higher is installed.
- A `*.groovy` configuration file (see `example/client_config.groovy` for a sample).

## Steps

### 1. Download the JAR
Visit the GitHub Releases page for mqtt-mimic and download the latest `mqtt-mimic-X.X.X.jar` file
(e.g., `mqtt-mimic-1.0.jar`). 

Alternatively, use wget from the command line:

```bash
wget https://github.com/your-repo/mqtt-mimic/releases/download/vX.X.X/mqtt-mimic-X.X.X-all.jar
```

### 2. Run the Utility
Place the downloaded JAR and your `client_config.groovy` file in the same directory, then execute:

```bash
java -jar mqtt-mimic-1.0.jar ./client_config.groovy
```

## Notes

- Replace X.X.X with the actual version number from the release (e.g., `1.0-SNAPSHOT` or `1.0.0`).
- Update the wget URL with the correct repository and release tag (e.g., `v1.0.0`).
- For building from source instead.
