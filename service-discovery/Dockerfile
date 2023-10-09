FROM maven:latest as maven
VOLUME /tmp
COPY pom.xml /app/
COPY ./service-discovery /app/service-discovery
WORKDIR /app/service-discovery
RUN mvn -pl . -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /service-discovery
ARG JAR_FILE=service-discovery-0.0.1-SNAPSHOT
EXPOSE 5001
COPY --from=maven /app/service-discovery/target/${JAR_FILE}.jar .
ENTRYPOINT ["java","-jar","service-discovery-0.0.1-SNAPSHOT.jar"]