FROM gradle:8.10-jdk21-jammy AS base

WORKDIR /app/

COPY build.gradle settings.gradle .
COPY src/ ./src/

EXPOSE 8080

CMD ["gradle", "build"]

# ...