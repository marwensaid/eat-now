# Étape 1: Build de l'application avec Maven
FROM maven:3.8.5-openjdk-17 AS build

# Copier l'ensemble du projet pour mettre en cache les dépendances
WORKDIR /app
COPY pom.xml .
COPY menu-service/pom.xml ./menu-service/
COPY order-service/pom.xml ./order-service/
COPY delivery-service/pom.xml ./delivery-service/

# Télécharger les dépendances (sans compiler)
RUN mvn dependency:go-offline

# Copier le code source
COPY delivery-service/src ./delivery-service/src

# Builder uniquement le module nécessaire
RUN mvn -pl delivery-service -am clean package

# Étape 2: Création de l'image finale
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=build /app/delivery-service/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
