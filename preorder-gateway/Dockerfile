FROM openjdk:21
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} gateway.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "gateway.jar"]