# API RESTful para Gestión de Tickets de Soporte Técnico

## Descripción

Microservicio para gestionar incidentes de soporte técnico (SupportTicket) utilizando Java 17 y Spring Boot 3, con filtros avanzados mediante query parameters y despliegue con Docker.

## Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **MySQL 8**
- **Maven**
- **Docker**
- **Bean Validation**

## Arquitectura

El proyecto sigue una arquitectura en capas:
- **Controller**: Manejo de endpoints HTTP
- **Service**: Lógica de negocio y validaciones
- **Repository**: Acceso a datos con Spring Data JPA
- **Model**: Entidades y enums
- **DTO**: Objetos de transferencia de datos
- **Exception**: Manejo centralizado de errores

## Instalación y Configuración

### Prerrequisitos

- Java 17 o superior
- Maven 3.6+
- MySQL 8.0+
- Docker (opcional para despliegue)

### Configuración de la Base de Datos

1. Crear la base de datos en MySQL:
```sql
CREATE DATABASE support_tickets_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Actualizar las credenciales en `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/support_tickets_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

### Ejecución Local

```bash
# Compilar y ejecutar
mvn clean spring-boot:run

# O compilar y ejecutar el JAR
mvn clean package
java -jar target/support-tickets-api-1.0.0.jar
```

La aplicación estará disponible en: `http://localhost:8080`

## Endpoints de la API

### 1. Crear Ticket

**POST** `/api/v1/support-tickets`

Crea un nuevo ticket de soporte técnico.

#### Request Body:
```json
{
  "requesterName": "Juan Pérez",
  "status": "OPEN",
  "priority": "HIGH",
  "category": "NETWORK",
  "estimatedCost": 150.50,
  "currency": "USD",
  "dueDate": "2025-12-31"
}
```

#### Response:
```json
{
  "id": 1,
  "ticketNumber": "ST-2025-000001",
  "requesterName": "Juan Pérez",
  "status": "OPEN",
  "priority": "HIGH",
  "category": "NETWORK",
  "estimatedCost": 150.50,
  "currency": "USD",
  "createdAt": "2025-12-12T19:30:00",
  "dueDate": "2025-12-31"
}
```

### 2. Listar Tickets con Filtros

**GET** `/api/v1/support-tickets`

Lista tickets con filtros opcionales y paginación.

#### Query Parameters:

- `q`: Búsqueda de texto en ticketNumber y requesterName (case-insensitive)
- `status`: Estado del ticket (OPEN, IN_PROGRESS, RESOLVED, CLOSED, CANCELLED)
- `currency`: Moneda (USD, EUR)
- `minCost`: Costo mínimo estimado
- `maxCost`: Costo máximo estimado
- `from`: Fecha de creación desde (formato ISO-8601: yyyy-MM-dd'T'HH:mm:ss)
- `to`: Fecha de creación hasta (formato ISO-8601: yyyy-MM-dd'T'HH:mm:ss)
- `page`: Número de página (default: 0)
- `size`: Tamaño de página (default: 20, max: 100)
- `sort`: Ordenamiento (default: createdAt,desc)

#### Ejemplos de Uso:

```bash
# Búsqueda por texto
GET /api/v1/support-tickets?q=pérez

# Filtrar por estado
GET /api/v1/support-tickets?status=OPEN

# Filtrar por moneda y rango de costos
GET /api/v1/support-tickets?currency=USD&minCost=50&maxCost=300

# Filtrar por rango de fechas
GET /api/v1/support-tickets?from=2025-01-01T00:00:00&to=2025-12-31T23:59:59

# Combinar múltiples filtros
GET /api/v1/support-tickets?q=john&status=OPEN&currency=USD&minCost=100&page=0&size=10&sort=createdAt,desc
```

#### Response:
```json
{
  "content": [
    {
      "id": 1,
      "ticketNumber": "ST-2025-000001",
      "requesterName": "Juan Pérez",
      "status": "OPEN",
      "priority": "HIGH",
      "category": "NETWORK",
      "estimatedCost": 150.50,
      "currency": "USD",
      "createdAt": "2025-12-12T19:30:00",
      "dueDate": "2025-12-31"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1
}
```

## Validaciones y Manejo de Errores

### Errores de Validación (400 Bad Request)

```json
{
  "timestamp": "2025-12-12T19:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Error de validación en los datos enviados",
  "path": "/api/v1/support-tickets",
  "validationErrors": {
    "requesterName": "El nombre del solicitante es obligatorio",
    "estimatedCost": "El costo estimado debe ser positivo"
  }
}
```

### Errores de Filtros (400 Bad Request)

```json
{
  "timestamp": "2025-12-12T19:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Estado inválido. Valores permitidos: OPEN, IN_PROGRESS, RESOLVED, CLOSED, CANCELLED",
  "path": "/api/v1/support-tickets"
}
```

## Despliegue con Docker

### Construir la Imagen

```bash
docker build -t support-tickets-api .
```

### Ejecutar el Contenedor

```bash
docker run -d \
  --name support-tickets \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/support_tickets_db \
  -e SPRING_DATASOURCE_USERNAME=tu_usuario \
  -e SPRING_DATASOURCE_PASSWORD=tu_contraseña \
  support-tickets-api
```

### Publicar en Docker Hub

```bash
# Taggear la imagen
docker tag support-tickets-api tu-usuario/support-tickets-api:1.0.0

# Subir a Docker Hub
docker push tu-usuario/support-tickets-api:1.0.0
```

## Campos de la Entidad SupportTicket

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long (PK) | Identificador único autogenerado |
| ticketNumber | String (único) | Número de ticket (ej: ST-2025-000145) |
| requesterName | String | Nombre del solicitante |
| status | Enum | OPEN, IN_PROGRESS, RESOLVED, CLOSED, CANCELLED |
| priority | Enum | LOW, MEDIUM, HIGH, CRITICAL |
| category | String | Categoría (NETWORK, HARDWARE, SOFTWARE) |
| estimatedCost | BigDecimal | Costo estimado del incidente |
| currency | Enum | USD, EUR |
| createdAt | LocalDateTime | Fecha/hora de creación (auto-generada) |
| dueDate | LocalDate | Fecha máxima de atención |

## Reglas de Negocio

1. **Generación de Tickets**: El número de ticket se genera automáticamente con formato ST-2025-XXXXXX
2. **Filtros Combinados**: Todos los filtros son opcionales y se combinan con lógica AND
3. **Validaciones**: 
   - Los costos deben ser positivos
   - Las fechas `from` debe ser ≤ `to`
   - `minCost` debe ser ≤ `maxCost`
4. **Ordenamiento**: Por defecto se ordena por `createdAt` descendente
5. **Paginación**: Máximo 100 registros por página

## Testing

### Ejecutar Tests Unitarios

```bash
mvn test
```

### Ejecutar Tests con Cobertura

```bash
mvn clean test jacoco:report
```

## Monitoreo y Logging

La aplicación incluye:
- Logging SQL en modo DEBUG
- Formato de fechas ISO-8601
- Manejo centralizado de excepciones
- Respuestas de error estructuradas

## Contribución

1. Fork del proyecto
2. Crear feature branch
3. Commit de cambios
4. Push al branch
5. Pull Request

## Licencia

Este proyecto está licenciado bajo la MIT License.
