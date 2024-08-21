# dor-replica-experiment

This project includes experimental code that allows for the storing and
replicating of information packages in multiple (OCFL) repositories.
It currently uses the Spring framework and `oclf-java` to manage OCFL-related actions.
Packages, replicas (copies), and repositories are recorded in a MySQL database.

## Installation/Usage

### Prerequisite(s)

- Java 21
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)

### Testing

```sh
# For Mac OS/Linux?
./gradlew test
```

## Resource(s)

- [Gradle](https://gradle.org/)
- [Spring framework](https://spring.io/)
- [ocfl-java](https://github.com/OCFL/ocfl-java)
