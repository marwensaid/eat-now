# Order Service - Eat Now

Microservice de gestion des commandes pour l'application Eat Now.

## 📋 Description

Le `order_service` permet de créer et gérer les commandes des clients. Il communique avec le `menu_service` pour valider les plats et récupérer leurs informations (nom, prix).

### Fonctionnalités

- ✅ Créer une commande avec validation des plats via `menu_service`
- ✅ Consulter une commande par ID
- ✅ Lister toutes les commandes d'un utilisateur
- ✅ Mettre à jour le statut d'une commande avec validation du workflow
- ✅ Circuit breaker et retry pour les appels vers `menu_service`
- ✅ Documentation Swagger/OpenAPI
- ✅ Métriques Prometheus via Actuator

## 🚀 Démarrage rapide

### Prérequis

- Java 17+
- Maven 3.6+
- Le `menu_service` doit être démarré sur le port 8081

### Lancer le service

```bash
# Compiler le projet
./mvnw clean package -DskipTests

# Démarrer le service
./mvnw spring-boot:run
```

Le service démarre sur **http://localhost:8082**

### Vérifier que le service est opérationnel

```bash
curl http://localhost:8082/api/orders/health
# Réponse: "Order Service is UP"
```

## 📚 API Endpoints

### Documentation Swagger

- **Swagger UI** : http://localhost:8082/swagger-ui/index.html
- **OpenAPI JSON** : http://localhost:8082/v3/api-docs

### 1. Créer une commande

```bash
POST /api/orders
Content-Type: application/json

{
  "userId": "user123",
  "items": [
    {
      "dishId": 1,
      "quantity": 2
    },
    {
      "dishId": 2,
      "quantity": 1
    }
  ],
  "deliveryAddress": "123 Rue de la Paix, 75001 Paris",
  "customerName": "Jean Dupont",
  "customerPhone": "+33612345678",
  "notes": "Sonnez SVP"
}
```

**Exemple avec curl :**

```bash
echo '{
  "userId": "user123",
  "items": [
    {"dishId": 1, "quantity": 2},
    {"dishId": 2, "quantity": 1}
  ],
  "deliveryAddress": "123 Rue de la Paix, 75001 Paris",
  "customerName": "Jean Dupont",
  "customerPhone": "+33612345678",
  "notes": "Sonnez SVP"
}' | curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d @-
```

**Réponse (201 Created) :**

```json
{
  "id": 1,
  "userId": "user123",
  "items": [
    {
      "dishId": 1,
      "dishName": "Salade César",
      "dishPrice": 9.5,
      "quantity": 2,
      "totalPrice": 19.0
    },
    {
      "dishId": 2,
      "dishName": "Burger Classique",
      "dishPrice": 12.9,
      "quantity": 1,
      "totalPrice": 12.9
    }
  ],
  "totalAmount": 31.9,
  "status": "CREATED",
  "deliveryAddress": "123 Rue de la Paix, 75001 Paris",
  "customerName": "Jean Dupont",
  "customerPhone": "+33612345678",
  "notes": "Sonnez SVP",
  "createdAt": "2025-10-10T11:16:54.480128",
  "updatedAt": "2025-10-10T11:16:54.480136"
}
```

### 2. Consulter une commande

```bash
GET /api/orders/{id}
```

**Exemple :**

```bash
curl http://localhost:8082/api/orders/1
```

### 3. Lister les commandes d'un utilisateur

```bash
GET /api/orders/user/{userId}
```

**Exemple :**

```bash
curl http://localhost:8082/api/orders/user/user123
```

### 4. Mettre à jour le statut d'une commande

```bash
PATCH /api/orders/{id}/status
Content-Type: application/json

{
  "status": "CONFIRMED"
}
```

**Exemple :**

```bash
curl -X PATCH http://localhost:8082/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "CONFIRMED"}'
```

## 🔄 Workflow des statuts

Le service valide les transitions de statut selon le workflow suivant :

```
CREATED → CONFIRMED → PREPARING → READY → IN_DELIVERY → DELIVERED
                                      ↓
                                  CANCELLED (possible depuis n'importe quel statut sauf DELIVERED)
```

### Statuts disponibles

| Statut        | Description                          |
| ------------- | ------------------------------------ |
| `CREATED`     | Commande créée (statut initial)      |
| `CONFIRMED`   | Commande confirmée par le restaurant |
| `PREPARING`   | Commande en préparation              |
| `READY`       | Commande prête pour livraison        |
| `IN_DELIVERY` | Commande en cours de livraison       |
| `DELIVERED`   | Commande livrée (statut final)       |
| `CANCELLED`   | Commande annulée                     |

### Exemples de transitions

```bash
# Workflow complet
curl -X PATCH http://localhost:8082/api/orders/1/status -H "Content-Type: application/json" -d '{"status":"CONFIRMED"}'
curl -X PATCH http://localhost:8082/api/orders/1/status -H "Content-Type: application/json" -d '{"status":"PREPARING"}'
curl -X PATCH http://localhost:8082/api/orders/1/status -H "Content-Type: application/json" -d '{"status":"READY"}'
curl -X PATCH http://localhost:8082/api/orders/1/status -H "Content-Type: application/json" -d '{"status":"IN_DELIVERY"}'
curl -X PATCH http://localhost:8082/api/orders/1/status -H "Content-Type: application/json" -d '{"status":"DELIVERED"}'
```

**Erreur de validation :** Une commande livrée ne peut plus changer de statut :

```json
{
  "status": 400,
  "message": "Impossible de modifier le statut d'une commande Livrée",
  "timestamp": "2025-10-10T11:21:33.374364",
  "path": "/api/orders/1/status"
}
```

## 🔗 Communication inter-services

Le `order_service` communique avec le `menu_service` via **WebClient** (Spring WebFlux) pour :

1. **Valider les plats** : vérifie que les `dishId` existent et sont disponibles
2. **Récupérer les informations** : nom et prix des plats pour calculer le montant total

### Configuration

```properties
# URL du menu_service
menu.service.url=http://localhost:8081
```

### Résilience (Resilience4j)

Tous les appels vers le `menu_service` sont protégés par :

- **Circuit Breaker** : s'ouvre après 50% d'échecs sur 10 appels
- **Retry** : 3 tentatives avec délai exponentiel (500ms → 1000ms → 2000ms)
- **Time Limiter** : timeout de 5 secondes

**Fallback** : En cas d'échec, une réponse par défaut est retournée avec :

- Prix : 0.0
- Nom : "Plat indisponible"
- Disponibilité : false

## 📊 Observabilité

### Actuator Endpoints

```bash
# Health check
curl http://localhost:8082/actuator/health

# Métriques Prometheus
curl http://localhost:8082/actuator/prometheus

# Informations sur l'application
curl http://localhost:8082/actuator/info
```

### Métriques disponibles

- Compteurs de requêtes HTTP par endpoint
- Temps de réponse (latence)
- Taux d'erreurs
- Statut du Circuit Breaker
- Nombre de retries

## 🏗️ Architecture

### Structure du projet

```
order_service/
├── src/main/java/anthony1/com/order_service/
│   ├── OrderServiceApplication.java        # Classe principale
│   ├── client/
│   │   └── MenuServiceClient.java          # Client REST vers menu_service
│   ├── config/
│   │   ├── OpenApiConfig.java              # Configuration Swagger
│   │   └── WebClientConfig.java            # Configuration WebClient
│   ├── controller/
│   │   └── OrderController.java            # Endpoints REST
│   ├── dto/
│   │   ├── DishDTO.java                    # DTO pour les plats du menu_service
│   │   ├── ErrorResponse.java              # DTO pour les erreurs
│   │   ├── OrderItemRequest.java           # DTO pour les items de commande (requête)
│   │   ├── OrderItemResponse.java          # DTO pour les items de commande (réponse)
│   │   ├── OrderRequest.java               # DTO pour créer une commande
│   │   ├── OrderResponse.java              # DTO pour retourner une commande
│   │   └── OrderStatusUpdateRequest.java   # DTO pour mettre à jour le statut
│   ├── exception/
│   │   ├── DishNotFoundException.java      # Exception si plat introuvable
│   │   ├── GlobalExceptionHandler.java     # Gestion globale des erreurs
│   │   ├── InvalidOrderStatusException.java # Exception si transition invalide
│   │   └── OrderNotFoundException.java     # Exception si commande introuvable
│   ├── model/
│   │   ├── Order.java                      # Entité Commande
│   │   ├── OrderItem.java                  # Entité Item de commande
│   │   └── OrderStatus.java                # Enum des statuts
│   ├── repository/
│   │   └── OrderRepository.java            # Stockage en mémoire
│   └── service/
│       └── OrderService.java               # Logique métier
└── src/main/resources/
    └── application.properties              # Configuration
```

### Modèle de données

#### Order (Commande)

| Champ           | Type            | Description                  |
| --------------- | --------------- | ---------------------------- |
| id              | Long            | Identifiant unique           |
| userId          | String          | Identifiant de l'utilisateur |
| items           | List<OrderItem> | Liste des plats commandés    |
| totalAmount     | Double          | Montant total (calculé)      |
| status          | OrderStatus     | Statut de la commande        |
| deliveryAddress | String          | Adresse de livraison         |
| customerName    | String          | Nom du client                |
| customerPhone   | String          | Téléphone du client          |
| notes           | String          | Notes optionnelles           |
| createdAt       | LocalDateTime   | Date de création             |
| updatedAt       | LocalDateTime   | Date de dernière mise à jour |

#### OrderItem (Item de commande)

| Champ      | Type    | Description                                  |
| ---------- | ------- | -------------------------------------------- |
| dishId     | Long    | ID du plat (référence vers menu_service)     |
| dishName   | String  | Nom du plat (récupéré depuis menu_service)   |
| dishPrice  | Double  | Prix unitaire (récupéré depuis menu_service) |
| quantity   | Integer | Quantité commandée                           |
| totalPrice | Double  | Prix total (dishPrice × quantity)            |

## 🧪 Tests

### Tests manuels

Tous les endpoints ont été testés avec succès :

✅ Création de commande avec communication vers `menu_service`  
✅ Récupération d'une commande par ID  
✅ Listing des commandes d'un utilisateur  
✅ Mise à jour du statut avec validation du workflow  
✅ Gestion des erreurs (plat introuvable, transition invalide)

### Scénario de test complet

```bash
# 1. Créer une commande
ORDER_ID=$(echo '{"userId":"user123","items":[{"dishId":1,"quantity":2}],"deliveryAddress":"123 Rue de Paris","customerName":"Jean Dupont","customerPhone":"+33612345678"}' | \
  curl -X POST http://localhost:8082/api/orders -H "Content-Type: application/json" -d @- -s | jq -r .id)

# 2. Consulter la commande
curl http://localhost:8082/api/orders/$ORDER_ID

# 3. Workflow de statuts
curl -X PATCH http://localhost:8082/api/orders/$ORDER_ID/status -H "Content-Type: application/json" -d '{"status":"CONFIRMED"}' -s | jq .status
curl -X PATCH http://localhost:8082/api/orders/$ORDER_ID/status -H "Content-Type: application/json" -d '{"status":"PREPARING"}' -s | jq .status
curl -X PATCH http://localhost:8082/api/orders/$ORDER_ID/status -H "Content-Type: application/json" -d '{"status":"READY"}' -s | jq .status
curl -X PATCH http://localhost:8082/api/orders/$ORDER_ID/status -H "Content-Type: application/json" -d '{"status":"IN_DELIVERY"}' -s | jq .status
curl -X PATCH http://localhost:8082/api/orders/$ORDER_ID/status -H "Content-Type: application/json" -d '{"status":"DELIVERED"}' -s | jq .status

# 4. Lister les commandes de l'utilisateur
curl http://localhost:8082/api/orders/user/user123
```

## 🐳 Docker

### Créer l'image

```bash
docker build -t order-service:latest .
```

### Lancer le conteneur

```bash
docker run -d \
  -p 8082:8082 \
  -e MENU_SERVICE_URL=http://menu-service:8081 \
  --name order-service \
  order-service:latest
```

## 🔧 Configuration

### application.properties

```properties
# Port du service
server.port=8082

# Nom de l'application
spring.application.name=order-service

# URL du menu_service
menu.service.url=http://localhost:8081

# Actuator
management.endpoints.web.exposure.include=health,info,prometheus,metrics
management.endpoint.health.show-details=always

# Swagger
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Resilience4j - Circuit Breaker
resilience4j.circuitbreaker.instances.menuService.sliding-window-size=10
resilience4j.circuitbreaker.instances.menuService.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.menuService.wait-duration-in-open-state=10000
resilience4j.circuitbreaker.instances.menuService.permitted-number-of-calls-in-half-open-state=5

# Resilience4j - Retry
resilience4j.retry.instances.menuService.max-attempts=3
resilience4j.retry.instances.menuService.wait-duration=500ms
resilience4j.retry.instances.menuService.exponential-backoff-multiplier=2

# Resilience4j - Time Limiter
resilience4j.timelimiter.instances.menuService.timeout-duration=5s
```

## 🔗 Liens utiles

- **Swagger UI** : http://localhost:8082/swagger-ui/index.html
- **Health Check** : http://localhost:8082/actuator/health
- **Métriques Prometheus** : http://localhost:8082/actuator/prometheus
- **Menu Service** : http://localhost:8081

## 📝 Notes techniques

- **Stockage** : Les commandes sont stockées en mémoire (ConcurrentHashMap). Les données sont perdues au redémarrage.
- **Communication** : WebClient réactif (Mono/Flux) pour les appels HTTP vers `menu_service`.
- **Validation** : Jakarta Bean Validation sur les DTOs.
- **Gestion des erreurs** : GlobalExceptionHandler avec réponses standardisées.
- **Thread-safe** : Utilisation de AtomicLong pour les IDs et ConcurrentHashMap pour le stockage.

## 🚀 Prochaines étapes

- [ ] Implémenter le `delivery_service`
- [ ] Ajouter une base de données PostgreSQL
- [ ] Créer les manifests Kubernetes (Deployment, Service, Ingress, HPA)
- [ ] Configurer Prometheus et Grafana
- [ ] Créer un Helm Chart
- [ ] Ajouter des tests unitaires et d'intégration
