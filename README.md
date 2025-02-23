# mqtt-mimic
A utility for emulating MQTT clients and smart devices, executing scenarios based on DSL scripts.


```zsh
$-> sdk install java 21.0.2-open
$-> sdk use java 21.0.2-open

$-> java -version

openjdk 21.0.2 2024-01-16
OpenJDK Runtime Environment (build 21.0.2+13-58)
OpenJDK 64-Bit Server VM (build 21.0.2+13-58, mixed mode, sharing)

cd mqtt-mimic

./gradlew clean
./gradlew shadowJar

java -jar build/libs/mqtt-mimic-1.0-SNAPSHOT-all.jar ./client_config.groovy
```
