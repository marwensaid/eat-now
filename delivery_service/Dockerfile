# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copier les fichiers Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Télécharger les dépendances
RUN ./mvnw dependency:go-offline -B

# Copier le code source
COPY src src

# Construire l'application
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port
EXPOSE 8083

# Variables d'environnement par défaut
ENV ORDER_SERVICE_URL=http://order-service:8082
ENV JAVA_OPTS=""

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
