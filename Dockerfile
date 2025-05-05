FROM openjdk:17-jdk-alpine

COPY target/atom-creator-*.jar atom-creator.jar
ENTRYPOINT ["java", "-jar", "/atom-creator.jar", "--clean", "-f", "/config.yaml", "/feeds"]
