# ---------- build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app


COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
# Tests
RUN mvn -B clean package -DskipTests

# ---------- Runtime ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN groupadd --system app && useradd --system --gid app app

COPY --from=build /app/target/*.jar app.jar
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]