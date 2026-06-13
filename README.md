# MediBook - Backend

Sistema de gestión y reserva de citas médicas. API REST desarrollada con Spring Boot.

## Tecnologías

- Java 17
- Spring Boot 4
- Spring Security + JWT
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- Swagger / OpenAPI
- WebSockets (STOMP)
- JUnit 5 + Mockito
- iText (PDF) y Apache POI (Excel)

## Requisitos previos

- JDK 17+
- Maven
- PostgreSQL 16+

## Configuración

### 1. Base de datos

```sql
CREATE USER medico_user WITH PASSWORD 'medico123';
CREATE DATABASE citas_medicas OWNER medico_user;
GRANT ALL PRIVILEGES ON DATABASE citas_medicas TO medico_user;
```

### 2. Variables de entorno

Configura las siguientes variables de entorno antes de ejecutar:

| Variable | Descripción | Ejemplo |
|---|---|---|
| `DB_USERNAME` | Usuario de PostgreSQL | `medico_user` |
| `DB_PASSWORD` | Contraseña de PostgreSQL | `medico123` |
| `JWT_SECRET` | Clave secreta para firmar JWT (mín. 32 caracteres) | `M3d1B00k$2024#...` |
| `MAIL_USERNAME` | Correo para envío de notificaciones | `tu_correo@gmail.com` |
| `MAIL_PASSWORD` | Contraseña de aplicación de Gmail | `xxxx xxxx xxxx xxxx` |

> Para Gmail, genera una "Contraseña de aplicación" en https://myaccount.google.com/security

### 3. Ejecutar

```bash
mvn spring-boot:run
```

La API estará disponible en `http://localhost:8080`

## Documentación de la API

Swagger UI disponible en: http://localhost:8080/swagger-ui.html

## Pruebas

```bash
mvn test
```

## Docker

```bash
docker build -t medibook-backend .
```

O usa `docker-compose.yml` del repositorio principal para levantar todo el stack (backend + frontend + base de datos).

## Roles del sistema

| Rol | Descripción |
|---|---|
| `ADMIN` | Gestión completa del sistema, estadísticas y auditoría |
| `MEDICO` | Gestión de horarios y agenda propia |
| `PACIENTE` | Reserva y gestión de sus propias citas |

## Estructura del proyecto
src/main/java/com/medibook/

├── config/        # Configuración (CORS, WebSocket, Swagger)

├── controller/     # Controladores REST

├── dto/             # Data Transfer Objects

├── entity/          # Entidades JPA

├── exception/       # Manejo global de excepciones

├── repository/      # Repositorios JPA

├── security/         # JWT y Spring Security

└── service/          # Lógica de negocio

## Funcionalidades principales

- Autenticación JWT con roles (Admin, Médico, Paciente)
- CRUD de pacientes, médicos y especialidades
- Gestión de horarios disponibles por médico
- Reserva, cancelación y reprogramación de citas
- Validación de conflictos de horario y fechas pasadas
- Notificaciones por correo (asíncronas)
- Actualizaciones en tiempo real vía WebSockets
- Registro de auditoría de acciones del sistema
- Exportación de reportes en PDF y Excel
- Documentación interactiva con Swagger