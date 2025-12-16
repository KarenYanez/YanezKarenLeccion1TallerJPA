# Etapa 1: build
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

# Variables de entorno para conectar con MySQL
ENV DATABASE_URL=jdbc:mysql://mysql_tickets:3306/ticketsdb2025?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
ENV DB_USER=AppRoot
ENV DB_PASSWORD=abcd
ENV DB_PORT=3306
ENV DB_NAME=ticketsdb2025

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
