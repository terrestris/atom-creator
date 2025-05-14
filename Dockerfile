FROM openjdk:17-jdk-alpine

ENV CONFIG_DIR=/configs
ENV OUTPUT_DIR=/feeds

COPY target/atom-creator-*.jar atom-creator.jar
ENTRYPOINT java -jar /atom-creator.jar --clean $CONFIG_DIR $OUTPUT_DIR
