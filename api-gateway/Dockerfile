FROM openjdk:19-alpine
VOLUME /tmp
EXPOSE 5006
ARG JAR_FILE=target/api-gateway-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} api-gateway.jar
ENTRYPOINT ["java","-jar","/api-gateway.jar"]