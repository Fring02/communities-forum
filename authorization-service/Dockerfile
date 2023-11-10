FROM maven:3-eclipse-temurin-17-alpine as maven
VOLUME /tmp
COPY pom.xml /app/
COPY ./authorization-service /app/authorization-service
WORKDIR /app/authorization-service
RUN mvn -pl . -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /authorization-service
ARG JAR_FILE=authorization-service-0.0.1-SNAPSHOT
EXPOSE 5003
COPY --from=maven /app/authorization-service/target/${JAR_FILE}.jar .
ENTRYPOINT ["java","-jar","authorization-service-0.0.1-SNAPSHOT.jar"]