FROM maven:latest as maven
VOLUME /tmp
COPY pom.xml /app/
COPY ./users-service /app/users-service
WORKDIR /app/users-service
RUN mvn -pl . -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /users-service
ARG JAR_FILE=users-service-1.0-SNAPSHOT
EXPOSE 5002
COPY --from=maven /app/users-service/target/${JAR_FILE}.jar .
ENTRYPOINT ["java","-jar","users-service-1.0-SNAPSHOT.jar"]