FROM openjdk:19-alpine
VOLUME /tmp
EXPOSE 5007
ARG JAR_FILE=target/notifications-service-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} notifications-service.jar
ENTRYPOINT ["java","-jar","/notifications-service.jar"]