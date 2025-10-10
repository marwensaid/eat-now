# Delivery Service - Eat Now

Microservice de gestion des livraisons pour l'application Eat Now.

## 📋 Description

Le `delivery_service` permet de gérer le processus de livraison des commandes. Il communique avec l'`order_service` pour valider les commandes et assure le suivi complet de la livraison depuis l'assignation du livreur jusqu'à la livraison finale.

### Fonctionnalités

- ✅ Créer une livraison associée à une commande avec validation via `order_service`
- ✅ Assigner un livreur à une livraison
- ✅ Suivre le statut de livraison avec workflow validé
- ✅ Mettre à jour la localisation en temps réel
- ✅ Consulter une livraison par ID ou par ID de commande
- ✅ Lister les livraisons par statut ou par livreur
- ✅ Circuit breaker et retry pour les appels vers `order_service`
- ✅ Documentation Swagger/OpenAPI
- ✅ Métriques Prometheus via Actuator

## 🚀 Démarrage rapide

### Prérequis

- Java 17+
- Maven 3.6+
- L'`order_service` doit être démarré sur le port 8082

### Lancer le service

```bash
# Compiler le projet
./mvnw clean package -DskipTests

# Démarrer le service
./mvnw spring-boot:run
```

Le service démarre sur **http://localhost:8083**

### Vérifier que le service est opérationnel

```bash
curl http://localhost:8083/api/deliveries/health
# Réponse: "Delivery Service is UP"
```

## 📚 API Endpoints

### Documentation Swagger

- **Swagger UI** : http://localhost:8083/swagger-ui/index.html
- **OpenAPI JSON** : http://localhost:8083/v3/api-docs

### 1. Créer une livraison

```bash
POST /api/deliveries
Content-Type: application/json

{
  "orderId": 1,
  "deliveryAddress": "123 Rue de la Paix, 75001 Paris",
  "customerName": "Jean Dupont",
  "customerPhone": "+33612345678",
  "estimatedDeliveryTime": 30,
  "notes": "Sonnez deux fois"
}
```

**Exemple avec curl :**

```bash
echo '{
  "orderId": 1,
  "deliveryAddress": "123 Rue de la Paix, 75001 Paris",
  "customerName": "Jean Dupont",
  "customerPhone": "+33612345678",
  "estimatedDeliveryTime": 30,
  "notes": "Sonnez deux fois"
}' | curl -X POST http://localhost:8083/api/deliveries \
  -H "Content-Type: application/json" \
  -d @-
```

**Réponse (201 Created) :**

```json
{
  "id": 1,
  "orderId": 1,
  "status": "PENDING",
  "deliveryAddress": "123 Rue de la Paix, 75001 Paris",
  "deliveryPersonId": null,
  "deliveryPersonName": null,
  "deliveryPersonPhone": null,
  "customerName": "Jean Dupont",
  "customerPhone": "+33612345678",
  "currentLocation": null,
  "estimatedDeliveryTime": 30,
  "pickupTime": null,
  "deliveredAt": null,
  "notes": "Sonnez deux fois",
  "createdAt": "2025-10-10T13:13:50.701564",
  "updatedAt": "2025-10-10T13:13:50.701576"
}
```

### 2. Assigner un livreur

```bash
PATCH /api/deliveries/{id}/assign
Content-Type: application/json

{
  "deliveryPersonId": 101,
  "deliveryPersonName": "Marc Livreur",
  "deliveryPersonPhone": "+33698765432"
}
```

**Exemple :**

```bash
curl -X PATCH http://localhost:8083/api/deliveries/1/assign \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPersonId": 101,
    "deliveryPersonName": "Marc Livreur",
    "deliveryPersonPhone": "+33698765432"
  }'
```

**Réponse :**

```json
{
  "id": 1,
  "orderId": 1,
  "status": "ASSIGNED",
  "deliveryPersonId": 101,
  "deliveryPersonName": "Marc Livreur",
  "deliveryPersonPhone": "+33698765432",
  ...
}
```

### 3. Mettre à jour le statut de livraison

```bash
PATCH /api/deliveries/{id}/status
Content-Type: application/json

{
  "status": "PICKED_UP",
  "currentLocation": "Restaurant"
}
```

**Exemple :**

```bash
curl -X PATCH http://localhost:8083/api/deliveries/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "PICKED_UP", "currentLocation": "Restaurant"}'
```

### 4. Consulter une livraison

```bash
# Par ID de livraison
GET /api/deliveries/{id}

# Par ID de commande
GET /api/deliveries/order/{orderId}
```

**Exemples :**

```bash
# Par ID de livraison
curl http://localhost:8083/api/deliveries/1

# Par ID de commande
curl http://localhost:8083/api/deliveries/order/1
```

### 5. Lister les livraisons

```bash
# Toutes les livraisons
GET /api/deliveries

# Par statut
GET /api/deliveries/status/{status}

# Par livreur
GET /api/deliveries/person/{deliveryPersonId}
```

**Exemples :**

```bash
# Toutes les livraisons
curl http://localhost:8083/api/deliveries

# Livraisons avec statut DELIVERED
curl http://localhost:8083/api/deliveries/status/DELIVERED

# Livraisons du livreur 101
curl http://localhost:8083/api/deliveries/person/101
```

## 🔄 Workflow des statuts

Le service valide les transitions de statut selon le workflow suivant :

```
PENDING → ASSIGNED → PICKED_UP → IN_TRANSIT → DELIVERED
            ↓            ↓            ↓
        CANCELLED    CANCELLED    FAILED
```

### Statuts disponibles

| Statut       | Description                       | Transitions autorisées        |
| ------------ | --------------------------------- | ----------------------------- |
| `PENDING`    | En attente d'assignation          | ASSIGNED, CANCELLED           |
| `ASSIGNED`   | Livreur assigné                   | PICKED_UP, CANCELLED          |
| `PICKED_UP`  | Commande récupérée par le livreur | IN_TRANSIT, CANCELLED, FAILED |
| `IN_TRANSIT` | En cours de livraison             | DELIVERED, FAILED             |
| `DELIVERED`  | Livrée (statut final)             | Aucune (final)                |
| `CANCELLED`  | Annulée                           | PENDING (réactivation)        |
| `FAILED`     | Échec de livraison                | PENDING (réactivation)        |

### Exemples de workflow complet

```bash
# 1. Créer une livraison (statut PENDING)
DELIVERY_ID=$(echo '{"orderId":1,"deliveryAddress":"123 Rue de Paris","customerName":"Jean Dupont","customerPhone":"+33612345678","estimatedDeliveryTime":30}' | \
  curl -X POST http://localhost:8083/api/deliveries -H "Content-Type: application/json" -d @- -s | jq -r .id)

# 2. Assigner un livreur (PENDING → ASSIGNED)
curl -X PATCH http://localhost:8083/api/deliveries/$DELIVERY_ID/assign \
  -H "Content-Type: application/json" \
  -d '{"deliveryPersonId":101,"deliveryPersonName":"Marc Livreur","deliveryPersonPhone":"+33698765432"}'

# 3. Commande récupérée (ASSIGNED → PICKED_UP)
curl -X PATCH http://localhost:8083/api/deliveries/$DELIVERY_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status":"PICKED_UP","currentLocation":"Restaurant"}'

# 4. En cours de livraison (PICKED_UP → IN_TRANSIT)
curl -X PATCH http://localhost:8083/api/deliveries/$DELIVERY_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status":"IN_TRANSIT","currentLocation":"En route vers le client"}'

# 5. Livraison effectuée (IN_TRANSIT → DELIVERED)
curl -X PATCH http://localhost:8083/api/deliveries/$DELIVERY_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status":"DELIVERED","currentLocation":"Livré au client"}'
```

### Validation des erreurs

**Erreur : Tentative de modification d'une livraison déjà livrée**

```json
{
  "status": 400,
  "message": "Impossible de modifier le statut d'une livraison déjà livrée",
  "timestamp": "2025-10-10T13:15:06.172366",
  "path": "/api/deliveries/1/status"
}
```

**Erreur : Tentative de créer deux livraisons pour la même commande**

```json
{
  "status": 400,
  "message": "Une livraison existe déjà pour la commande 1"
}
```

## 🔗 Communication inter-services

Le `delivery_service` communique avec l'`order_service` via **WebClient** (Spring WebFlux) pour :

1. **Valider les commandes** : vérifie que l'`orderId` existe avant de créer une livraison
2. **Récupérer les informations** : adresse, client, téléphone si non fournis

### Configuration

```properties
# URL de l'order_service
order.service.url=http://localhost:8082
```

### Résilience (Resilience4j)

Tous les appels vers l'`order_service` sont protégés par :

- **Circuit Breaker** : s'ouvre après 50% d'échecs sur 10 appels
- **Retry** : 3 tentatives avec délai exponentiel (500ms → 1000ms → 2000ms)
- **Time Limiter** : timeout de 5 secondes

**Fallback** : En cas d'échec, une réponse par défaut est retournée avec des informations minimales.

## 📊 Observabilité

### Actuator Endpoints

```bash
# Health check
curl http://localhost:8083/actuator/health

# Métriques Prometheus
curl http://localhost:8083/actuator/prometheus

# Informations sur l'application
curl http://localhost:8083/actuator/info
```

### Métriques disponibles

- Compteurs de requêtes HTTP par endpoint
- Temps de réponse (latence)
- Taux d'erreurs
- Statut du Circuit Breaker
- Nombre de retries
- Distribution des statuts de livraison

## 🏗️ Architecture

### Structure du projet

```
delivery_service/
├── src/main/java/anthony2/com/delivery_service/
│   ├── DeliveryServiceApplication.java     # Classe principale
│   ├── client/
│   │   └── OrderServiceClient.java         # Client REST vers order_service
│   ├── config/
│   │   ├── OpenApiConfig.java              # Configuration Swagger
│   │   └── WebClientConfig.java            # Configuration WebClient
│   ├── controller/
│   │   └── DeliveryController.java         # Endpoints REST
│   ├── dto/
│   │   ├── AssignDeliveryPersonRequest.java    # DTO pour assigner un livreur
│   │   ├── DeliveryRequest.java                # DTO pour créer une livraison
│   │   ├── DeliveryResponse.java               # DTO pour retourner une livraison
│   │   ├── DeliveryStatusUpdateRequest.java    # DTO pour mettre à jour le statut
│   │   ├── ErrorResponse.java                  # DTO pour les erreurs
│   │   └── OrderDTO.java                       # DTO pour les commandes du order_service
│   ├── exception/
│   │   ├── DeliveryNotFoundException.java      # Exception si livraison introuvable
│   │   ├── GlobalExceptionHandler.java         # Gestion globale des erreurs
│   │   ├── InvalidDeliveryStatusException.java # Exception si transition invalide
│   │   └── OrderNotFoundException.java         # Exception si commande introuvable
│   ├── model/
│   │   ├── Delivery.java                       # Entité Livraison
│   │   └── DeliveryStatus.java                 # Enum des statuts
│   ├── repository/
│   │   └── DeliveryRepository.java             # Stockage en mémoire
│   └── service/
│       └── DeliveryService.java                # Logique métier
└── src/main/resources/
    └── application.properties                  # Configuration
```

### Modèle de données

#### Delivery (Livraison)

| Champ                 | Type           | Description                                |
| --------------------- | -------------- | ------------------------------------------ |
| id                    | Long           | Identifiant unique                         |
| orderId               | Long           | Référence vers la commande (order_service) |
| status                | DeliveryStatus | Statut de la livraison                     |
| deliveryAddress       | String         | Adresse de livraison                       |
| deliveryPersonId      | Long           | ID du livreur                              |
| deliveryPersonName    | String         | Nom du livreur                             |
| deliveryPersonPhone   | String         | Téléphone du livreur                       |
| customerName          | String         | Nom du client                              |
| customerPhone         | String         | Téléphone du client                        |
| currentLocation       | String         | Localisation actuelle                      |
| estimatedDeliveryTime | Integer        | Temps estimé (en minutes)                  |
| pickupTime            | LocalDateTime  | Heure de récupération                      |
| deliveredAt           | LocalDateTime  | Heure de livraison                         |
| notes                 | String         | Notes optionnelles                         |
| createdAt             | LocalDateTime  | Date de création                           |
| updatedAt             | LocalDateTime  | Date de dernière mise à jour               |

## 🧪 Tests

### Tests manuels

Tous les endpoints ont été testés avec succès :

✅ Création de livraison avec validation de la commande via `order_service`  
✅ Assignation d'un livreur avec passage au statut ASSIGNED  
✅ Mise à jour du statut avec workflow validé (PENDING → ASSIGNED → PICKED_UP → IN_TRANSIT → DELIVERED)  
✅ Consultation par ID de livraison  
✅ Consultation par ID de commande  
✅ Listing par statut  
✅ Listing par livreur  
✅ Validation des erreurs (livraison déjà livrée, commande déjà livrée)  
✅ Timestamps automatiques (pickupTime, deliveredAt)

### Scénario de test complet

```bash
# 1. Créer une livraison
DELIVERY_ID=$(echo '{"orderId":1,"deliveryAddress":"123 Rue de Paris","customerName":"Jean Dupont","customerPhone":"+33612345678","estimatedDeliveryTime":30}' | \
  curl -X POST http://localhost:8083/api/deliveries -H "Content-Type: application/json" -d @- -s | jq -r .id)

# 2. Consulter la livraison
curl http://localhost:8083/api/deliveries/$DELIVERY_ID

# 3. Assigner un livreur
curl -X PATCH http://localhost:8083/api/deliveries/$DELIVERY_ID/assign \
  -H "Content-Type: application/json" \
  -d '{"deliveryPersonId":101,"deliveryPersonName":"Marc Livreur","deliveryPersonPhone":"+33698765432"}' -s | jq .status

# 4. Workflow de statuts avec localisation
curl -X PATCH http://localhost:8083/api/deliveries/$DELIVERY_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status":"PICKED_UP","currentLocation":"Restaurant"}' -s | jq '{status, currentLocation, pickupTime}'

curl -X PATCH http://localhost:8083/api/deliveries/$DELIVERY_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status":"IN_TRANSIT","currentLocation":"Rue de Rivoli"}' -s | jq '{status, currentLocation}'

curl -X PATCH http://localhost:8083/api/deliveries/$DELIVERY_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status":"DELIVERED","currentLocation":"Livré au client"}' -s | jq '{status, currentLocation, deliveredAt}'

# 5. Lister les livraisons du livreur
curl http://localhost:8083/api/deliveries/person/101 -s | jq 'length'

# 6. Lister les livraisons livrées
curl http://localhost:8083/api/deliveries/status/DELIVERED -s | jq 'map({id, orderId, deliveryPersonName})'
```

## 🐳 Docker

### Créer l'image

```bash
docker build -t delivery-service:latest .
```

### Lancer le conteneur

```bash
docker run -d \
  -p 8083:8083 \
  -e ORDER_SERVICE_URL=http://order-service:8082 \
  --name delivery-service \
  delivery-service:latest
```

## 🔧 Configuration

### application.properties

```properties
# Port du service
server.port=8083

# Nom de l'application
spring.application.name=delivery-service

# URL de l'order_service
order.service.url=http://localhost:8082

# Actuator
management.endpoints.web.exposure.include=health,info,prometheus,metrics
management.endpoint.health.show-details=always

# Swagger
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Resilience4j - Circuit Breaker
resilience4j.circuitbreaker.instances.orderService.sliding-window-size=10
resilience4j.circuitbreaker.instances.orderService.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.orderService.wait-duration-in-open-state=10000

# Resilience4j - Retry
resilience4j.retry.instances.orderService.max-attempts=3
resilience4j.retry.instances.orderService.wait-duration=500ms
resilience4j.retry.instances.orderService.exponential-backoff-multiplier=2

# Resilience4j - Time Limiter
resilience4j.timelimiter.instances.orderService.timeout-duration=5s
```

## 🔗 Liens utiles

- **Swagger UI** : http://localhost:8083/swagger-ui/index.html
- **Health Check** : http://localhost:8083/actuator/health
- **Métriques Prometheus** : http://localhost:8083/actuator/prometheus
- **Order Service** : http://localhost:8082
- **Menu Service** : http://localhost:8081

## 📝 Notes techniques

- **Stockage** : Les livraisons sont stockées en mémoire (ConcurrentHashMap). Les données sont perdues au redémarrage.
- **Communication** : WebClient réactif (Mono/Flux) pour les appels HTTP vers `order_service`.
- **Validation** : Jakarta Bean Validation sur les DTOs + validation métier des transitions de statut.
- **Gestion des erreurs** : GlobalExceptionHandler avec réponses standardisées.
- **Thread-safe** : Utilisation de AtomicLong pour les IDs et ConcurrentHashMap pour le stockage.
- **Timestamps automatiques** : `pickupTime` renseigné au passage à PICKED_UP, `deliveredAt` au passage à DELIVERED.

## 🚀 Prochaines étapes

- [ ] Ajouter une base de données PostgreSQL
- [ ] Implémenter la notification en temps réel (WebSocket)
- [ ] Ajouter la géolocalisation GPS
- [ ] Créer les manifests Kubernetes (Deployment, Service, Ingress, HPA)
- [ ] Configurer Prometheus et Grafana
- [ ] Créer un Helm Chart global pour les 3 services
- [ ] Ajouter des tests unitaires et d'intégration
- [ ] Intégrer un système de rating livreur/client
