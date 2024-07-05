FROM openjdk:21
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} order-service.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "order-service.jar"]