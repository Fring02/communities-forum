FROM maven:3-eclipse-temurin-17-alpine as maven
VOLUME /tmp
COPY pom.xml /app/
COPY ./communities-service /app/communities-service
WORKDIR /app/communities-service
RUN mvn -pl . -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /communities-service
ARG JAR_FILE=communities-service-0.0.1-SNAPSHOT
EXPOSE 5005
COPY --from=maven /app/communities-service/target/${JAR_FILE}.jar .
ENTRYPOINT ["java","-jar","communities-service-0.0.1-SNAPSHOT.jar"]