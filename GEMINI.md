# Project: KotlinLogin-service

## Project Overview

This is a Kotlin-based authentication service built with the Ktor framework. It leverages JWT (JSON Web Tokens) for authentication, Exposed as an ORM (Object-Relational Mapping) for database interactions, and HikariCP for efficient database connection pooling. The project is designed to connect to a PostgreSQL database.

The service provides:
*   A basic API endpoint for checking service status.
*   A `POST /login` endpoint to issue JWT tokens upon successful (though currently placeholder) credential handling.
*   A secured `GET /user` endpoint that requires a valid JWT to retrieve user information from the token.
*   A `POST /register` endpoint which is currently a placeholder and not yet implemented.

## Building & Running

The project utilizes Gradle for build automation and dependency management.

### General Commands

*   `./gradlew test`: Executes all defined tests within the project.
*   `./gradlew build`: Compiles the project, runs tests, and packages the application.
*   `./gradlew buildFatJar`: Creates an executable "fat JAR" containing the server and all its dependencies, suitable for standalone deployment.
*   `./gradlew run`: Starts the Ktor server directly from Gradle.
*   `./gradlew buildImage`: Builds a Docker image for the application.
*   `./gradlew publishImageToLocalRegistry`: Publishes the built Docker image to the local Docker registry.
*   `./gradlew runDocker`: Runs the application using the locally built Docker image.

### Application Startup

Upon successful startup, the server typically logs:
```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

## Development Conventions

*   **Language:** Kotlin
*   **Web Framework:** Ktor
*   **Build Tool:** Gradle
*   **Database:** PostgreSQL. Connection details (URL, user, password, pool size, etc.) are configured in `src/main/resources/application.yaml`.
*   **ORM:** Exposed for database interactions and schema management.
*   **Connection Pooling:** HikariCP.
*   **Authentication:** JSON Web Tokens (JWT) for secure API access. JWT configuration (domain, audience, realm, secret) is managed via `src/main/resources/application.yaml` and loaded through `JwtConfig.kt`.
*   **Configuration:** Application and environment-specific settings are primarily defined in `src/main/resources/application.yaml`.
*   **Logging:** Uses Logback as configured in `src/main/resources/logback.xml`.
*   **Serialization:** Kotlinx Serialization for JSON content negotiation.
*   **Schema Management:** Database tables (e.g., `UsersTable`) are created and managed programmatically using Exposed's `SchemaUtils.create()` during application startup, based on the definitions in `src/main/kotlin/models/UserTable.kt`.
*   **Password Handling:** Passwords are expected to be hashed before being stored in the database. The `UsersTable` schema includes a `passwordHash` field for this purpose.
*   **Testing:** Unit and integration tests are written using Kotlin's test framework and Ktor's `testApplication` utility, allowing for isolated testing with configurable application settings (e.g., overriding JWT properties for tests).
