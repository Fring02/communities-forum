FROM maven:latest as maven
VOLUME /tmp
COPY pom.xml /app/
COPY ./config-server /app/config-server
WORKDIR /app/config-server
RUN mvn -pl . -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /config-server
ARG JAR_FILE=config-server-0.0.1-SNAPSHOT
EXPOSE 5000
COPY --from=maven /app/config-server/target/${JAR_FILE}.jar .
ENTRYPOINT ["java","-jar","config-server-0.0.1-SNAPSHOT.jar"]