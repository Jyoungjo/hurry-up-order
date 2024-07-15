FROM openjdk:21
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} payment-service.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "payment-service.jar"]