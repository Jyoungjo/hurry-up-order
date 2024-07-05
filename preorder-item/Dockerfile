FROM openjdk:21
ARG JAR_FILE=./preorder-item/build/libs/*.jar
COPY ${JAR_FILE} item-service.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "item-service.jar"]