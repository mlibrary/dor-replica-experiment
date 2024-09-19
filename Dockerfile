FROM gradle:8.10-jdk21-jammy AS base

WORKDIR /app/

COPY build.gradle settings.gradle .
COPY src/ ./src/

EXPOSE 8080

CMD ["gradle", "build"]

FROM gradle:8.10-jdk21-jammy as build

WORKDIR /app/

COPY --from=base /app .

RUN gradle bootJar

FROM bitnami/java:21 AS deployment

ARG JAR_NAME_ARG

ENV JAR_NAME=${JAR_NAME_ARG}

RUN echo $JAR_NAME

WORKDIR /app/

COPY --from=build /app/build/libs/${JAR_NAME_ARG} .

EXPOSE 8080

ENTRYPOINT java -jar ${JAR_NAME}
