FROM openjdk:19-alpine
VOLUME /tmp
EXPOSE 5002
ARG JAR_FILE=target/users-service-1.0-SNAPSHOT.jar
ADD ${JAR_FILE} users-service/users-service.jar
ENTRYPOINT ["java","-jar","users-service/users-service.jar"]