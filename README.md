# dor-replica-experiment

This project includes experimental code that allows for the storing and
replicating of information packages in multiple (OCFL) repositories.
It currently uses the Spring framework and `oclf-java` to manage OCFL-related actions.
Packages, replicas (copies), and repositories are recorded in a MySQL database.

## Installation/Usage

### Prerequisite(s)

- Java 21
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)

### Usage

For demo usage, all configuration and setup should be handled for you.
There will be two configured repositories, `repo_one` and `repo_two`.
The following command will start up a Web application.

```sh
./gradlew bootRun
```

The application has OpenAPI and Swagger UI support. Once the application has started,
visit http://localhost:8080/swagger-ui/index.html to review
and use the available routes.

### Testing

```sh
./gradlew test
```

## Resource(s)

- [Gradle](https://gradle.org/)
- [Spring framework](https://spring.io/)
- [ocfl-java](https://github.com/OCFL/ocfl-java)
