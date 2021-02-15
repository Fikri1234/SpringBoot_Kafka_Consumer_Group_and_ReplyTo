# SpringBoot_Kafka_Consumer_Group_and_ReplyTo
Installation Kafka & implement Spring boot Kafka to produce data - consumer group and reply topic to another one

## Prerequisites
1. Java 8
2. Dev Tools 
3. Lombok 
4. Zookeper 
5. Kafka
5. Gradle

## Getting Started

### Architecture
![Alt text](asset/architecture.jpg?raw=true "Architecture")

### Installation & Running Zookeeper
1. Download and extract Zookeper https://zookeeper.apache.org/releases.html
2. Go to Zookeper directory, then open conf directory
3. Rename file zoo_sample.cfg to zoo.cfg
4. Create folder "dataz" inside Zookeeper directory
5. Edit zoo.cfg and change dataDir to "Zookeper directory"/dataz
6. [Screenshot Edit zoo.cfg](asset/kafka_zconf.png)
7. Run Zookeeper by open new command inside Zookeper directory and type by following command `.\bin\zkServer.sh`

### Installation & Running Kafka
1. Download and extract Kafka Binary https://kafka.apache.org/downloads
2. Go to Kafka directory, then open config directory
3. Create 3 Broker Properties by copy paste from file server.properties and rename it by following number server-1.properties, server-2.properties, server-3.properties
4. [Screenshot Edit server-1.Properties](asset/kafka_server1_conf.png)
5. [Screenshot Edit server-2.Properties](asset/kafka_server2_conf.png)
6. [Screenshot Edit server-3.Properties](asset/kafka_server3_conf.png)
7. Run broker 1 by open cmd in Kafka directory and type `.\bin\windows\kafka-server-start.bat .\config\server-1.properties`
8. [Screenshot Start Broker 1](asset/kafka_server3_conf.png)
9. Run broker 2 by open cmd in Kafka directory and type `.\bin\windows\kafka-server-start.bat .\config\server-2.properties`
10. [Screenshot Start Broker 2](asset/kafka_server3_conf.png)
11. Run broker 3 by open cmd in Kafka directory and type `.\bin\windows\kafka-server-start.bat .\config\server-3.properties`
12. [Screenshot Start Broker 3](asset/kafka_server3_conf.png)

### Spring Boot
1. Open a new command prompt in the app directory,and type `./gradlew bootRun`
2. Hit the URL localhost:8181/send-group/
![Alt text](asset/kafka_send_consumer_group_loop.png?raw=true "Consumer Group iterate")
3. Hit the URL localhost:8181/send/
![Alt text](asset/kafka_send_consumer_group.png?raw=true "Consumer Group")
4. Hit the URL localhost:8181/send-reply/
![Alt text](asset/kafka_reply.png?raw=true "Consumer Group Reply To Another Topic")
