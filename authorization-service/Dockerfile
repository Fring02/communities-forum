FROM openjdk:19-alpine
VOLUME /tmp
EXPOSE 5003
ARG JAR_FILE=target/authorization-service-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} authorization-service.jar
ENTRYPOINT ["java","-jar","/authorization-service.jar"]