# --- Etapa 1: build ---
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Etapa 2: imagen final, liviana ---
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render/Railway inyectan la variable PORT; la app la lee via application.properties
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
