FROM openjdk:21
ARG JAR_FILE=./preorder-user/build/libs/*.jar
COPY ${JAR_FILE} user-service.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "user-service.jar"]