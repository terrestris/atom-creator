FROM openjdk:17-jdk-alpine

ENV CONFIG_PATH=/config.yaml
ENV DATA_DIR=/data
ENV PUBLIC_URL=http://localhost:8080

COPY target/atom-creator-*.jar atom-creator.jar
ENTRYPOINT java -jar /atom-creator.jar --clean -f $CONFIG_PATH $DATA_DIR $PUBLIC_URL
