FROM openjdk:19-alpine
VOLUME /tmp
EXPOSE 5004
ARG JAR_FILE=target/posts-service-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} posts-service.jar
ENTRYPOINT ["java","-jar","/posts-service.jar"]