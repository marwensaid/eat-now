# Use OpenJDK 17 as base image
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY build/libs/order-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8083
EXPOSE 8083

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
