# Usar una imagen base de Eclipse Temurin OpenJDK 17
FROM eclipse-temurin:17-jdk-alpine

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo pom.xml primero para aprovechar el caché de Maven
COPY pom.xml .

# Copiar el código fuente
COPY src ./src

# Instalar Maven
RUN apk add --no-cache maven

# Compilar la aplicación
RUN mvn clean package -DskipTests

# Exponer el puerto 8080
EXPOSE 8080

# Variables de entorno por defecto (pueden ser sobrescritas al ejecutar el contenedor)
ENV SERVER_PORT=8080
ENV DATABASE_URL=jdbc:mysql://host.docker.internal:3306/support_tickets_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
ENV DATABASE_USERNAME=root
ENV DATABASE_PASSWORD=root
ENV JPA_DDL_AUTO=update
ENV JPA_SHOW_SQL=false
ENV LOG_SQL_LEVEL=INFO
ENV LOG_BINDER_LEVEL=WARN

# Ejecutar la aplicación
CMD ["java", "-jar", "target/support-tickets-api-1.0.0.jar"]
