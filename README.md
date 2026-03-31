# Mitzie Auth Service

This is a Kotlin-based authentication service built with the Ktor framework. It leverages JWT (JSON Web Tokens) for authentication, Exposed as an ORM (Object-Relational Mapping) for database interactions, and HikariCP for efficient database connection pooling. This service is designed to be used across various projects to handle user registration, login, and authentication.

## Features

The service provides:
*   A basic API endpoint for checking service status.
*   A `POST /login` endpoint to issue JWT tokens upon successful credential handling.
*   A secured `GET /user` endpoint that requires a valid JWT to retrieve user information from the token.
*   A `POST /register` endpoint for user registration, ensuring unique email and username.

## Technologies Used

*   **Web Framework:** Ktor
*   **Authentication:** JSON Web Tokens (JWT)
*   **Database:** PostgreSQL
*   **ORM:** Exposed
*   **Connection Pooling:** HikariCP
*   **Build Tool:** Gradle

## Building & Running

To build or run the project, use one of the following tasks:

| Task                                    | Description                                                          |
|-----------------------------------------|----------------------------------------------------------------------|
| `./gradlew test`                        | Run the tests                                                        |
| `./gradlew build`                       | Build everything                                                     |
| `./gradlew buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `./gradlew buildImage`                  | Build the docker image to use with the fat JAR                       |
| `./gradlew publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `./gradlew run`                         | Run the server                                                       |
| `./gradlew runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see output similar to this:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

## Future Enhancements

- [ ] Add user timezone support
- [ ] Add profile picture support
- [ ] Split access token into access and refresh token
- [ ] Implement password reset functionality
- [ ] Integrate OAuth with at least GitHub
- [ ] Implement user profile management (username, email, password)
- [ ] Implement user soft delete