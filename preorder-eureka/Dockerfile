FROM openjdk:21
ARG JAR_FILE=./preorder-eureka/build/libs/*.jar
COPY ${JAR_FILE} eureka-server.jar
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]