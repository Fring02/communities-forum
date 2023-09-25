FROM openjdk:19-alpine
VOLUME /tmp
EXPOSE 5000
ARG JAR_FILE=target/config-server-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} config-server/config-server.jar
ENTRYPOINT ["java","-jar","config-server/config-server.jar"]