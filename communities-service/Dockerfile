FROM openjdk:19-alpine
VOLUME /tmp
EXPOSE 5005
ARG JAR_FILE=target/communities-service-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} communities-service.jar
ENTRYPOINT ["java","-jar","/communities-service.jar"]