# 🍔 Springboot-eat-now

Bienvenue sur le projet **Eat Now** ! Cette application est une plateforme de livraison de repas construite sur une architecture microservices avec Spring Boot.

## 🚀 Architecture

L'application suit une architecture microservices, où chaque service est responsable d'une fonctionnalité métier spécifique. Cela permet une meilleure scalabilité, une maintenance simplifiée et un déploiement indépendant des composants.

### Nos Services

*   **📦 Plat Service (`/plat-service`)**:
    *   Responsable de la gestion des plats (repas).
    *   Permet de créer, lire, mettre à jour et supprimer des plats du menu.

*   **📝 Commande Service (`/commande-service`)**:
    *   Gère le processus de commande des clients.
    *   Prend en charge la création de commandes, la validation et le suivi de leur état.

*   **🚚 Livraison Service (`/livraison-service`)**:
    *   Coordonne la livraison des commandes.
    *   Suit la position des livreurs et l'état de la livraison jusqu'au client final.

## 🛠️ Stack Technique

*   **Backend**: Java & [Spring Boot](https://spring.io/projects/spring-boot)
*   **Build**: [Apache Maven](https://maven.apache.org/)
*   **Containerisation**: [Docker](https://www.docker.com/) & [Docker Compose](https://docs.docker.com/compose/)
*   **Orchestration**: [Kubernetes](https://kubernetes.io/)
*   **Monitoring**: [Prometheus](https://prometheus.io/) & [Grafana](https://grafana.com/)

## 📂 Structure du Projet

```
.
├── commande-service/   # Microservice de gestion des commandes
├── livraison-service/  # Microservice de gestion des livraisons
├── plat-service/       # Microservice de gestion des plats
├── deployment/         # Fichiers de déploiement Kubernetes
├── docker/             # Dockerfiles et configuration Docker Compose
├── monitoring/         # Configuration pour Prometheus & Grafana
└── pom.xml             # Fichier Maven parent
```

## ⚡ Démarrage Rapide

Ce projet est conçu pour être lancé facilement en local avec Docker.

### Prérequis

*   [JDK](https://www.oracle.com/java/technologies/downloads/) (Version 17+)
*   [Apache Maven](https://maven.apache.org/download.cgi)
*   [Docker](https://www.docker.com/products/docker-desktop/)

### Lancement

1.  **Compiler tous les modules :**
    ```bash
    mvn clean install
    ```

2.  **Démarrer l'environnement avec Docker Compose :**
    ```bash
    docker-compose up --build
    ```

Une fois démarrés, les services seront accessibles sur les ports qui seront définis dans le fichier `docker-compose.yml`.
