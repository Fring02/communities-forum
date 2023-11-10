FROM maven:3-eclipse-temurin-17-alpine as maven
VOLUME /tmp
COPY pom.xml /app/
COPY ./notifications-service /app/notifications-service
WORKDIR /app/notifications-service
RUN mvn -pl . -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /notifications-service
ARG JAR_FILE=notifications-service-0.0.1-SNAPSHOT
EXPOSE 5007
COPY --from=maven /app/notifications-service/target/${JAR_FILE}.jar .
ENTRYPOINT ["java","-jar","notifications-service-0.0.1-SNAPSHOT.jar"]