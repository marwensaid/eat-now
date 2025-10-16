# Use une image JDK robuste pour Spring Boot/Micrometer
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY build/libs/delivery-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8082
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
