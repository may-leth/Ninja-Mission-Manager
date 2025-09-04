# ---- Stage 1: Build ----
FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /src

COPY pom.xml .

RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src

RUN mvn -q -DskipTests package

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /src/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]