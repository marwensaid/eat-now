# Menu Service

Microservice de gestion du catalogue des plats pour l'application **Eat Now**.

## 📋 Fonctionnalités

- **Lister tous les plats** : Récupère l'ensemble des plats disponibles dans le catalogue
- **Consulter un plat** : Affiche les détails d'un plat spécifique
- **Ajouter un plat** : Crée un nouveau plat dans le catalogue
- **Modifier un plat** : Met à jour les informations d'un plat existant
- **Supprimer un plat** : Retire un plat du catalogue
- **Filtrer par catégorie** : Liste les plats d'une catégorie (ENTREE, PLAT, DESSERT, BOISSON)
- **Filtrer par disponibilité** : Liste les plats disponibles ou indisponibles

## 🛠️ Technologies

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Web** : API REST
- **Spring Actuator** : Endpoints de supervision
- **Micrometer + Prometheus** : Métriques pour l'observabilité
- **Resilience4j** : Résilience (Circuit Breaker, Retry, Timeout)
- **Swagger/OpenAPI** : Documentation interactive de l'API
- **Lombok** : Réduction du boilerplate
- **Maven** : Gestion des dépendances

## 🚀 Démarrage rapide

### Prérequis

- Java 17
- Maven 3.8+

### Compilation

```bash
cd menu_service
./mvnw clean install
```

### Lancement

```bash
./mvnw spring-boot:run
```

Le service démarre sur le port **8081**.

## 📚 API Documentation

Une fois le service démarré, accédez à la documentation Swagger :

- **Swagger UI** : http://localhost:8081/swagger-ui.html
- **OpenAPI JSON** : http://localhost:8081/api-docs

## 🔌 Endpoints principaux

| Méthode | Endpoint                               | Description                 |
| ------- | -------------------------------------- | --------------------------- |
| GET     | `/api/dishes`                          | Liste tous les plats        |
| GET     | `/api/dishes/{id}`                     | Récupère un plat par ID     |
| GET     | `/api/dishes/category/{category}`      | Filtre par catégorie        |
| GET     | `/api/dishes/available?available=true` | Filtre par disponibilité    |
| POST    | `/api/dishes`                          | Crée un nouveau plat        |
| PUT     | `/api/dishes/{id}`                     | Met à jour un plat          |
| DELETE  | `/api/dishes/{id}`                     | Supprime un plat            |
| GET     | `/api/dishes/count`                    | Compte le nombre de plats   |
| GET     | `/api/dishes/health`                   | Vérifie la santé du service |

## 📊 Monitoring

### Actuator Endpoints

- **Health** : http://localhost:8081/actuator/health
- **Metrics** : http://localhost:8081/actuator/metrics
- **Prometheus** : http://localhost:8081/actuator/prometheus

### Métriques disponibles

- Nombre de requêtes HTTP
- Temps de réponse moyen
- Taux d'erreurs
- État du Circuit Breaker
- Métriques JVM

## 🛡️ Résilience

Le service implémente plusieurs patterns de résilience :

### Circuit Breaker

- **Seuil d'échec** : 50%
- **Fenêtre glissante** : 10 appels
- **État ouvert** : 10 secondes
- **Appels en demi-ouvert** : 3

### Retry

- **Tentatives max** : 3
- **Délai entre tentatives** : 500ms

### Time Limiter

- **Timeout** : 2 secondes

## 💾 Stockage des données

Les données sont stockées **en mémoire** (Map/List) :

- Aucune base de données externe requise
- Données initialisées au démarrage
- Données perdues au redémarrage

### Données par défaut

Le service initialise automatiquement 5 plats :

1. Salade César (ENTREE) - 9.50€
2. Burger Classique (PLAT) - 12.90€
3. Pizza Margherita (PLAT) - 11.50€
4. Tiramisu (DESSERT) - 6.50€
5. Coca-Cola (BOISSON) - 2.50€

## 📝 Exemple d'utilisation

### Créer un plat

```bash
curl -X POST http://localhost:8081/api/dishes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pâtes Carbonara",
    "description": "Pâtes fraîches, lardons, crème, parmesan",
    "price": 13.50,
    "category": "PLAT",
    "available": true,
    "imageUrl": "https://example.com/carbonara.jpg"
  }'
```

### Récupérer tous les plats

```bash
curl http://localhost:8081/api/dishes
```

### Récupérer un plat par ID

```bash
curl http://localhost:8081/api/dishes/1
```

### Filtrer par catégorie

```bash
curl http://localhost:8081/api/dishes/category/PLAT
```

### Mettre à jour un plat

```bash
curl -X PUT http://localhost:8081/api/dishes/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Burger Premium",
    "description": "Burger avec viande premium",
    "price": 15.90,
    "category": "PLAT",
    "available": true
  }'
```

### Supprimer un plat

```bash
curl -X DELETE http://localhost:8081/api/dishes/1
```

## 🐳 Docker

### Build de l'image

```bash
docker build -t menu-service:latest .
```

### Lancement du conteneur

```bash
docker run -p 8081:8081 menu-service:latest
```

## ☸️ Kubernetes

Fichiers de déploiement disponibles dans le dossier `k8s/` :

- `deployment.yaml` : Déploiement du service
- `service.yaml` : Service Kubernetes
- `hpa.yaml` : HorizontalPodAutoscaler
- `servicemonitor.yaml` : ServiceMonitor pour Prometheus

## 🧪 Tests

### Exécuter les tests

```bash
./mvnw test
```

### Tests disponibles

- Tests unitaires des contrôleurs
- Tests unitaires des services
- Tests d'intégration

## 📦 Structure du projet

```
menu_service/
├── src/
│   ├── main/
│   │   ├── java/anthony/com/menu_service/
│   │   │   ├── config/              # Configurations
│   │   │   ├── controller/          # Contrôleurs REST
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── exception/           # Gestion des exceptions
│   │   │   ├── model/               # Modèles de données
│   │   │   ├── repository/          # Repositories (stockage)
│   │   │   ├── service/             # Logique métier
│   │   │   └── MenuServiceApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/                        # Tests unitaires
├── pom.xml
└── README.md
```

## 🔧 Configuration

Les paramètres de configuration se trouvent dans `application.properties` :

- **Port** : `server.port=8081`
- **Actuator** : Exposition des endpoints health, metrics, prometheus
- **Circuit Breaker** : Configuration Resilience4j
- **Retry** : Configuration des tentatives
- **Timeout** : Configuration des délais

## 📄 License

MIT License

## 👥 Auteurs

Eat Now Team
