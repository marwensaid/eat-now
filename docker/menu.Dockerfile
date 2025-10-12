FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY menu .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
