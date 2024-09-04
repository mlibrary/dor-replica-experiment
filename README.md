# dor-replica-experiment

This project includes experimental code that allows for the storing and
replicating of information packages in multiple (OCFL) repositories.
It currently uses the Spring framework and `oclf-java` to manage OCFL-related actions.
Packages, replicas (copies), and repositories are recorded in a MySQL database.

## Installation/Usage

### Prerequisite(s)

- Java 21
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)

### Configuration

#### Repositories setup

A barebones structure for multiple file system repositories can be found
under [/repos](/repos/), but you will need to create a few more directories and
add some content to fully use the application.

First, create `storage` and `workspace` directories for the provided repositories.

```sh
mkdir repos/repo_one/storage
mkdir repos/repo_one/workspace
mkdir repos/repo_two/storage
mkdir repos/repo_two/workspace
```

Then, add a package (directory with any contents) to [repos/deposit](repos/deposit/).

### Usage

After completing the above, the application should be ready to run as a demo.
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
