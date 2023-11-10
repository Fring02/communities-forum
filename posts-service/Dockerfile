FROM maven:3-eclipse-temurin-17-alpine as maven
VOLUME /tmp
COPY pom.xml /app/
COPY ./posts-service /app/posts-service
WORKDIR /app/posts-service
RUN mvn -pl . -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /posts-service
ARG JAR_FILE=posts-service-0.0.1-SNAPSHOT
EXPOSE 5004
COPY --from=maven /app/posts-service/target/${JAR_FILE}.jar .
ENTRYPOINT ["java","-jar","posts-service-0.0.1-SNAPSHOT.jar"]