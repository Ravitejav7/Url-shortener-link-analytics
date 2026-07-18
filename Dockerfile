FROM maven:3.9.16-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src src

RUN chmod +x mvnw && ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/target/url-shortener-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=10000
EXPOSE 10000

ENTRYPOINT ["sh", "-c", "if [ -z \"$MONGODB_URI\" ]; then echo 'MONGODB_URI is required'; exit 1; fi; java -Dspring.mongodb.uri=\"$MONGODB_URI\" -Dapp.base-url=\"${APP_BASE_URL:-http://localhost:${PORT:-8080}}\" -jar app.jar"]
