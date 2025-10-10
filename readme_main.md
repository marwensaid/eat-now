# 🍽️ EatNow - Application de Restauration Microservices

## 📋 Table des Matières
- [Architecture](#architecture)
- [Technologies Utilisées](#technologies-utilisées)
- [Structure du Projet](#structure-du-projet)
- [Prérequis](#prérequis)
- [Installation et Déploiement](#installation-et-déploiement)
- [Tests et Validation](#tests-et-validation)
- [Monitoring](#monitoring)
- [APIs Documentation](#apis-documentation)

---

## 🏗️ Architecture

L'application EatNow est composée de 3 microservices Spring Boot :

### **1. menu-service** (Port 8081)
Gestion du catalogue des plats
- CRUD complet sur les plats
- Filtrage par catégorie
- Stockage en mémoire (ConcurrentHashMap)

### **2. order-service** (Port 8082)
Gestion des commandes clients
- Création de commandes
- Consultation par utilisateur
- Mise à jour du statut
- Communication avec menu-service (WebClient + Resilience4j)

### **3. delivery-service** (Port 8083)
Gestion des livraisons
- Création de livraison pour une commande
- Assignation de livreur
- Suivi en temps réel
- Communication avec order-service (WebClient + Resilience4j)

### Flux de Communication
```
Client → menu-service (consultation plats)
Client → order-service → menu-service (création commande)
Client → delivery-service → order-service (création livraison)
```

---

## 🛠️ Technologies Utilisées

| Catégorie | Technologies |
|-----------|--------------|
| **Langage** | Java 17 |
| **Framework** | Spring Boot 3.2.0 |
| **Build** | Maven 3.9+ |
| **Conteneurisation** | Docker |
| **Orchestration** | Kubernetes |
| **Résilience** | Resilience4j (Circuit Breaker, Retry, Timeout) |
| **Monitoring** | Prometheus + Grafana |
| **Métriques** | Micrometer + Spring Actuator |
| **Documentation API** | SpringDoc OpenAPI (Swagger) |
| **Communication** | WebClient (WebFlux) |

---

## 📁 Structure du Projet

```
eatnow/
├── menu-service/
│   ├── src/main/java/com/eatnow/menu/
│   │   ├── MenuServiceApplication.java
│   │   ├── controller/DishController.java
│   │   ├── model/Dish.java
│   │   └── service/DishService.java
│   ├── src/main/resources/application.yml
│   ├── Dockerfile
│   └── pom.xml
│
├── order-service/
│   ├── src/main/java/com/eatnow/order/
│   │   ├── OrderServiceApplication.java
│   │   ├── controller/OrderController.java
│   │   ├── model/{Order.java, OrderItem.java, OrderStatus.java}
│   │   ├── dto/{CreateOrderRequest.java, OrderItemRequest.java}
│   │   ├── service/OrderService.java
│   │   └── client/MenuClient.java
│   ├── src/main/resources/application.yml
│   ├── Dockerfile
│   └── pom.xml
│
├── delivery-service/
│   ├── src/main/java/com/eatnow/delivery/
│   │   ├── DeliveryServiceApplication.java
│   │   ├── controller/DeliveryController.java
│   │   ├── model/{Delivery.java, DeliveryStatus.java}
│   │   ├── dto/CreateDeliveryRequest.java
│   │   ├── service/DeliveryService.java
│   │   └── client/OrderClient.java
│   ├── src/main/resources/application.yml
│   ├── Dockerfile
│   └── pom.xml
│
├── kubernetes/
│   ├── namespace.yaml
│   ├── menu-service-deployment.yaml
│   ├── order-service-deployment.yaml
│   ├── delivery-service-deployment.yaml
│   ├── prometheus-config.yaml
│   └── grafana-deployment.yaml
│
├── architecture.drawio (ou .png)
└── README.md
```

---

## 📦 Prérequis

- **Java 17** installé
- **Maven 3.9+** installé
- **Docker** installé et démarré
- **Kubernetes** (Minikube, Kind, ou cluster K8s)
- **kubectl** configuré

### Vérification

```bash
java -version    # doit afficher Java 17
mvn -version     # doit afficher Maven 3.9+
docker --version
kubectl version
```

---

## 🚀 Installation et Déploiement

### Étape 1 : Build des Microservices

```bash
# Menu Service
cd menu-service
mvn clean package -DskipTests
docker build -t eatnow/menu-service:1.0.0 .

# Order Service
cd ../order-service
mvn clean package -DskipTests
docker build -t eatnow/order-service:1.0.0 .

# Delivery Service
cd ../delivery-service
mvn clean package -DskipTests
docker build -t eatnow/delivery-service:1.0.0 .
```

### Étape 2 : Déploiement sur Kubernetes

```bash
# Créer le namespace
kubectl apply -f kubernetes/namespace.yaml

# Déployer les microservices
kubectl apply -f kubernetes/menu-service-deployment.yaml
kubectl apply -f kubernetes/order-service-deployment.yaml
kubectl apply -f kubernetes/delivery-service-deployment.yaml

# Déployer Prometheus et Grafana
kubectl apply -f kubernetes/prometheus-config.yaml
kubectl apply -f kubernetes/grafana-deployment.yaml

# Vérifier le déploiement
kubectl get pods -n eatnow
kubectl get svc -n eatnow
kubectl get hpa -n eatnow
```

### Étape 3 : Vérifier l'état des Pods

```bash
kubectl get pods -n eatnow --watch
```

Attendez que tous les pods soient en état `Running` et `Ready 1/1`.

---

## 🧪 Tests et Validation

### URLs d'Accès (NodePort)

Remplacez `<NODE-IP>` par l'IP de votre nœud Kubernetes :

```bash
# Pour Minikube
minikube ip

# Ou pour obtenir l'IP du nœud
kubectl get nodes -o wide
```

| Service | URL | Description |
|---------|-----|-------------|
| **Menu Service** | `http://<NODE-IP>:30081/api/dishes` | API plats |
| **Order Service** | `http://<NODE-IP>:30082/api/orders` | API commandes |
| **Delivery Service** | `http://<NODE-IP>:30083/api/deliveries` | API livraisons |
| **Prometheus** | `http://<NODE-IP>:30090` | Métriques |
| **Grafana** | `http://<NODE-IP>:30030` | Dashboards (admin/admin) |

### Test 1 : Consulter les Plats

```bash
curl http://<NODE-IP>:30081/api/dishes
```

### Test 2 : Créer une Commande

```bash
curl -X POST http://<NODE-IP>:30082/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "deliveryAddress": "15 rue de la Paix, Paris",
    "items": [
      {"dishId": 1, "quantity": 2},
      {"dishId": 3, "quantity": 1}
    ]
  }'
```

### Test 3 : Créer une Livraison

```bash
curl -X POST http://<NODE-IP>:30083/api/deliveries \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "deliveryAddress": "15 rue de la Paix, Paris",
    "estimatedTimeMinutes": 30
  }'
```

### Test 4 : Assigner un Livreur

```bash
curl -X PATCH "http://<NODE-IP>:30083/api/deliveries/1/assign?driverId=driver42&driverName=Jean%20Dupont"
```

### Test 5 : Vérifier la Résilience

```bash
# Arrêter le menu-service
kubectl scale deployment menu-service --replicas=0 -n eatnow

# Essayer de créer une commande (doit utiliser le fallback)
curl -X POST http://<NODE-IP>:30082/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user456",
    "deliveryAddress": "10 avenue des Champs, Paris",
    "items": [{"dishId": 2, "quantity": 1}]
  }'

# Redémarrer le menu-service
kubectl scale deployment menu-service --replicas=2 -n eatnow
```

---

## 📊 Monitoring

### Configuration Prometheus

Prometheus scrape automatiquement les métriques des 3 microservices via les annotations :
- `prometheus.io/scrape: "true"`
- `prometheus.io/port: "808X"`
- `prometheus.io/path: "/actuator/prometheus"`

**Accès Prometheus :** `http://<NODE-IP>:30090`

### Métriques Disponibles

Les microservices exposent des métriques via Spring Actuator :

| Métrique | Description |
|----------|-------------|
| `http_server_requests_seconds` | Temps de réponse des requêtes HTTP |
| `menu_service_requests_total` | Nombre total de requêtes menu-service |
| `order_service_requests_total` | Nombre total de requêtes order-service |
| `delivery_service_requests_total` | Nombre total de requêtes delivery-service |
| `resilience4j_circuitbreaker_state` | État du circuit breaker |
| `jvm_memory_used_bytes` | Utilisation mémoire JVM |
| `system_cpu_usage` | Usage CPU |

### Configuration Grafana

1. **Accéder à Grafana :** `http://<NODE-IP>:30030`
2. **Login :** admin / admin
3. **Ajouter Prometheus comme datasource :**
   - Configuration → Data Sources → Add data source
   - Type : Prometheus
   - URL : `http://prometheus:9090`
   - Save & Test

### Dashboard Grafana Recommandé

**Créer un nouveau dashboard avec les panels suivants :**

#### Panel 1 : Nombre de Requêtes par Service
```promql
sum(rate(http_server_requests_seconds_count[5m])) by (application)
```

#### Panel 2 : Taux d'Erreurs
```promql
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) by (application)
```

#### Panel 3 : Temps de Réponse Moyen
```promql
rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])
```

#### Panel 4 : État des Circuit Breakers
```promql
resilience4j_circuitbreaker_state
```

#### Panel 5 : Usage CPU des Pods
```promql
container_cpu_usage_seconds_total{namespace="eatnow"}
```

#### Panel 6 : Usage Mémoire
```promql
container_memory_usage_bytes{namespace="eatnow"}
```

### Captures d'écran à inclure

📸 Prenez des captures d'écran de :
1. Le dashboard Grafana avec tous les panels
2. L'état des pods Kubernetes (`kubectl get pods -n eatnow`)
3. Les HPA en action (`kubectl get hpa -n eatnow`)
4. Une requête réussie sur Swagger

---

## 📚 APIs Documentation

### Swagger UI

Chaque microservice expose une documentation interactive Swagger :

| Service | Swagger UI |
|---------|------------|
| **Menu Service** | `http://<NODE-IP>:30081/swagger-ui.html` |
| **Order Service** | `http://<NODE-IP>:30082/swagger-ui.html` |
| **Delivery Service** | `http://<NODE-IP>:30083/swagger-ui.html` |

### Endpoints Principaux

#### Menu Service

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/dishes` | Liste tous les plats |
| GET | `/api/dishes/{id}` | Détails d'un plat |
| POST | `/api/dishes` | Créer un plat |
| PUT | `/api/dishes/{id}` | Modifier un plat |
| DELETE | `/api/dishes/{id}` | Supprimer un plat |
| GET | `/api/dishes/category/{category}` | Plats par catégorie |

#### Order Service

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/orders` | Liste toutes les commandes |
| GET | `/api/orders/{id}` | Détails d'une commande |
| GET | `/api/orders/user/{userId}` | Commandes d'un utilisateur |
| POST | `/api/orders` | Créer une commande |
| PATCH | `/api/orders/{id}/status` | Mettre à jour le statut |

#### Delivery Service

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/deliveries` | Liste toutes les livraisons |
| GET | `/api/deliveries/{id}` | Détails d'une livraison |
| GET | `/api/deliveries/order/{orderId}` | Livraison d'une commande |
| POST | `/api/deliveries` | Créer une livraison |
| PATCH | `/api/deliveries/{id}/assign` | Assigner un livreur |
| PATCH | `/api/deliveries/{id}/status` | Mettre à jour le statut |
| PATCH | `/api/deliveries/{id}/location` | Mettre à jour la position |

### Health Checks

Tous les services exposent des endpoints de santé :

```bash
# Liveness probe
curl http://<NODE-IP>:3008X/actuator/health/liveness

# Readiness probe
curl http://<NODE-IP>:3008X/actuator/health/readiness

# Métriques Prometheus
curl http://<NODE-IP>:3008X/actuator/prometheus
```

---

## 🔧 Configuration Résilience

### Circuit Breaker

**Configuration dans `application.yml` :**
- **slidingWindowSize :** 10 requêtes
- **failureRateThreshold :** 50%
- **waitDurationInOpenState :** 10 secondes
- **permittedNumberOfCallsInHalfOpenState :** 3

**Comportement :**
- Si 50% des 10 dernières requêtes échouent → Circuit OPEN
- Attendre 10 secondes → Circuit HALF_OPEN
- Tester avec 3 requêtes → Si succès, retour à CLOSED

### Retry

- **maxAttempts :** 3 tentatives
- **waitDuration :** 500ms
- **exponentialBackoff :** x2 (500ms, 1s, 2s)

### Timeout

- **timeoutDuration :** 2 secondes maximum par requête

### Fallback

En cas d'échec :
- **order-service :** Crée une commande avec plats "inconnus"
- **delivery-service :** Crée une livraison sans validation de commande

---

## ⚖️ Scalabilité (HPA)

Les HorizontalPodAutoscaler sont configurés pour :

- **Min replicas :** 2
- **Max replicas :** 5
- **CPU target :** 70%
- **Memory target :** 80%

**Tester le scaling :**

```bash
# Générer de la charge
kubectl run -it --rm load-generator --image=busybox --restart=Never -- /bin/sh

# Dans le pod
while true; do wget -q -O- http://menu-service.eatnow:8081/api/dishes; done

# Observer le scaling (dans un autre terminal)
kubectl get hpa -n eatnow --watch
```

---

## 🐛 Troubleshooting

### Problème : Pods en CrashLoopBackOff

```bash
# Voir les logs
kubectl logs <pod-name> -n eatnow

# Décrire le pod
kubectl describe pod <pod-name> -n eatnow
```

**Causes courantes :**
- Image Docker non trouvée → Vérifier le nom de l'image
- Probes qui échouent → Augmenter `initialDelaySeconds`
- Erreur Java → Vérifier les logs applicatifs

### Problème : Services ne communiquent pas

```bash
# Tester la connectivité
kubectl run test-pod --image=busybox -n eatnow --rm -it -- /bin/sh
wget -O- http://menu-service:8081/actuator/health
```

### Problème : Prometheus ne scrape pas les métriques

```bash
# Vérifier les targets dans Prometheus
# Aller sur http://<NODE-IP>:30090/targets

# Vérifier les annotations des pods
kubectl get pods -n eatnow -o yaml | grep -A 5 annotations
```

---

## 📈 Améliorations Possibles

- [ ] Ajouter une API Gateway (Spring Cloud Gateway)
- [ ] Implémenter Service Discovery (Consul, Eureka)
- [ ] Ajouter une base de données (PostgreSQL avec Helm)
- [ ] Implémenter l'authentification (OAuth2/JWT)
- [ ] Ajouter des tests d'intégration
- [ ] Configurer CI/CD (Jenkins, GitLab CI)
- [ ] Implémenter le tracing distribué (Jaeger, Zipkin)
- [ ] Ajouter un message broker (RabbitMQ, Kafka)

---

## 👨‍💻 Auteur

**Examen Pratique - Architecture Microservices & Cloud-Native**
- Java 17 + Spring Boot 3
- Kubernetes + Prometheus + Grafana
- Resilience4j + WebClient

---

## 📄 Licence

Ce projet est réalisé dans le cadre d'un examen académique.