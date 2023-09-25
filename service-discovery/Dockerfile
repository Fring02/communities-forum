FROM openjdk:19-alpine
VOLUME /tmp
EXPOSE 5001
ARG JAR_FILE=target/service-discovery-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} service-discovery.jar
ENTRYPOINT ["java","-jar","/service-discovery.jar"]