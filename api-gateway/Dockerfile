FROM maven:latest as maven
VOLUME /tmp
COPY pom.xml /app/
COPY ./api-gateway /app/api-gateway
WORKDIR /app/api-gateway
RUN mvn -pl . -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /api-gateway
ARG JAR_FILE=api-gateway-0.0.1-SNAPSHOT
EXPOSE 5006
COPY --from=maven /app/api-gateway/target/${JAR_FILE}.jar .
ENTRYPOINT ["java","-jar","api-gateway-0.0.1-SNAPSHOT.jar"]