# 📝 TODO List - Projet EatNow

## 🔴 PRIORITÉ 1 - Communication Inter-Services + Résilience (CRITIQUE)

### 1.1 Ajouter RestTemplate/WebClient aux dépendances
- [ ] Ajouter `spring-boot-starter-webflux` dans `order-service/build.gradle` (pour WebClient)
  - OU utiliser RestTemplate (déjà disponible avec spring-boot-starter-web)

### 1.2 Configuration des URLs des services K8s
- [ ] Ajouter dans `order-service/src/main/resources/application.properties` :
  ```properties
  # URLs des services via DNS Kubernetes (nom-du-service.namespace.svc.cluster.local)
  # Ou simplement nom-du-service si dans le même namespace
  menu.service.url=http://menu-service:8080/menu
  delivery.service.url=http://delivery-service:8080/delivery
  
  # Configuration Resilience4j
  resilience4j.circuitbreaker.instances.menuService.slidingWindowSize=10
  resilience4j.circuitbreaker.instances.menuService.failureRateThreshold=50
  resilience4j.circuitbreaker.instances.menuService.waitDurationInOpenState=10000
  resilience4j.circuitbreaker.instances.deliveryService.slidingWindowSize=10
  resilience4j.circuitbreaker.instances.deliveryService.failureRateThreshold=50
  
  resilience4j.retry.instances.menuService.maxAttempts=3
  resilience4j.retry.instances.menuService.waitDuration=1000
  resilience4j.retry.instances.deliveryService.maxAttempts=3
  
  resilience4j.timelimiter.instances.menuService.timeoutDuration=2s
  resilience4j.timelimiter.instances.deliveryService.timeoutDuration=2s
  ```

### 1.3 Créer les clients HTTP dans order-service
- [ ] Créer `order-service/src/main/java/.../config/RestClientConfig.java`
  - Bean RestTemplate OU WebClient
- [ ] Créer `order-service/src/main/java/.../client/MenuServiceClient.java`
  - Méthode `validateDishes(List<String> dishIds)` pour vérifier que les plats existent
  - Annotations `@CircuitBreaker(name = "menuService", fallbackMethod = "validateDishesFallback")`
  - Annotations `@Retry(name = "menuService")`
  - Annotations `@TimeLimiter(name = "menuService")`
  - Méthode fallback qui retourne une validation par défaut ou lève une exception claire
- [ ] Créer `order-service/src/main/java/.../client/DeliveryServiceClient.java`
  - Méthode `createDelivery(String orderId, ...)` pour créer une livraison
  - Annotations `@CircuitBreaker(name = "deliveryService", fallbackMethod = "createDeliveryFallback")`
  - Annotations `@Retry(name = "deliveryService")`
  - Annotations `@TimeLimiter(name = "deliveryService")`
  - Méthode fallback qui log l'erreur et retourne null ou une réponse par défaut

### 1.4 Intégrer les appels dans OrderService
- [ ] Modifier `OrderService.createOrder()` pour :
  - Appeler `MenuServiceClient.validateDishes()` pour valider les plats avant de créer la commande
  - Appeler `DeliveryServiceClient.createDelivery()` après création de la commande
  - Gérer les erreurs avec des messages clairs (ex: "Menu service indisponible")

### 1.5 Vérifier les Services K8s
- [ ] Vérifier que les services K8s existent et sont bien nommés :
  - `kubectl get svc menu-service` → doit pointer vers le port 8080
  - `kubectl get svc delivery-service` → doit pointer vers le port 8080
  - `kubectl get svc order-service` → doit pointer vers le port 8080
- [ ] Les services communiquent via le DNS interne K8s : `http://nom-service:port`

---

## 🟠 PRIORITÉ 2 - Monitoring (Prometheus + Grafana)

### 2.1 Déployer Prometheus
- [ ] Créer `k8s/prometheus-configmap.yaml` avec scrape configs pour les 3 services
- [ ] Créer `k8s/prometheus-deployment.yaml`
- [ ] Créer `k8s/prometheus-service.yaml` (type NodePort ou ClusterIP)
- [ ] Ajouter Prometheus dans `k8s/kustomization.yaml`

### 2.2 Déployer Grafana
- [ ] Créer `k8s/grafana-configmap.yaml` (datasource Prometheus)
- [ ] Créer `k8s/grafana-deployment.yaml`
- [ ] Créer `k8s/grafana-service.yaml` (type NodePort)
- [ ] Ajouter Grafana dans `k8s/kustomization.yaml`

### 2.3 Dashboard Grafana
- [ ] Se connecter à Grafana (admin/admin)
- [ ] Créer un dashboard avec panels pour :
  - Nombre de requêtes HTTP par service (métrique: `http_server_requests_seconds_count`)
  - Taux d'erreurs (métrique: `http_server_requests_seconds_count{status=~"5.."}`)
  - Temps de réponse moyen (métrique: `http_server_requests_seconds_sum / http_server_requests_seconds_count`)
  - Métriques Circuit Breaker (si implémenté)
- [ ] Exporter le dashboard en JSON dans `k8s/grafana-dashboard.json`
- [ ] Prendre des captures d'écran

---

## 🟡 PRIORITÉ 3 - Corrections & Améliorations

### 3.1 Corrections de configuration
- [ ] Corriger `order-service/src/main/resources/application.properties` ligne 16 : `service=INFO` → `INFO`
- [ ] **IMPORTANT** : Vérifier les ports des Services K8s dans `k8s/*-service.yaml`
  - Tous doivent exposer le port 8080 (targetPort vers 8081 du container)
  - Les applications écoutent sur 8081 dans leurs pods
  - Les Services K8s exposent le port 8080 pour la communication inter-services
- [ ] Corriger les health checks dans les deployments :
  - Path doit être `/actuator/health` (sans le context-path car on appelle directement le pod)
  - OU ajouter le context-path : `/menu/actuator/health`, `/order/actuator/health`, `/delivery/actuator/health`

### 3.2 Améliorer les health checks
- [ ] Vérifier que `/actuator/health` est accessible sur tous les services
- [ ] Tester les probes liveness et readiness

### 3.3 Build et Push des images Docker
- [ ] Builder l'image order-service : `docker build -t janovp/order-service:latest order-service/`
- [ ] Push sur Docker Hub : `docker push janovp/order-service:latest`
- [ ] Mettre à jour les images menu-service et delivery-service si modifiées

---

## 🟢 PRIORITÉ 4 - Documentation

### 4.1 README principal
- [ ] Créer `DEPLOYMENT.md` ou remplacer le README.md avec :
  - Instructions de build des 3 microservices
  - Instructions de déploiement K8s complet
  - URLs d'accès (services + Swagger + Prometheus + Grafana)
  - Exemples de requêtes API pour tester
  - Captures d'écran du dashboard Grafana

### 4.2 Documentation API
- [ ] Vérifier que Swagger est accessible sur les 3 services
- [ ] Documenter les endpoints principaux

### 4.3 Schéma d'architecture
- [ ] Vérifier que `architecture.html` est à jour
- [ ] Exporter en PNG si nécessaire
- [ ] S'assurer qu'il montre :
  - Les 3 microservices
  - Les flux de communication REST
  - Prometheus + Grafana
  - Circuit Breaker, Retry, Timeout
  - HPA
  - Ingress

---

## ✅ Tests de validation finale

### Test du flux complet
- [ ] 1. Lister les plats : `GET http://eatnow.local/menu/api/menu/dishes`
- [ ] 2. Créer une commande : `POST http://eatnow.local/order/api/orders`
- [ ] 3. Vérifier que la livraison est créée automatiquement : `GET http://eatnow.local/delivery/api/deliveries`
- [ ] 4. Mettre à jour le statut de la commande : `PUT http://eatnow.local/order/api/orders/{id}/status`

### Test de résilience
- [ ] Arrêter menu-service : `kubectl scale deployment menu-service --replicas=0`
- [ ] Créer une commande et vérifier le fallback
- [ ] Vérifier les métriques du circuit breaker dans Grafana
- [ ] Redémarrer menu-service : `kubectl scale deployment menu-service --replicas=2`

### Test de scalabilité
- [ ] Générer de la charge sur un service
- [ ] Vérifier que le HPA scale automatiquement : `kubectl get hpa -w`
- [ ] Vérifier les métriques dans Grafana

### Monitoring
- [ ] Accéder à Prometheus : vérifier que les 3 services sont scrapés
- [ ] Accéder à Grafana : vérifier le dashboard
- [ ] Prendre des captures d'écran pour le README

---

## 📊 Checklist finale avant rendu

- [ ] Les 3 microservices sont déployés et fonctionnels
- [ ] La communication inter-services fonctionne
- [ ] Resilience4j est implémenté (Circuit Breaker, Retry, Timeout)
- [ ] Prometheus collecte les métriques des 3 services
- [ ] Grafana affiche un dashboard avec les métriques clés
- [ ] Swagger est accessible sur les 3 services
- [ ] Le README contient toutes les instructions
- [ ] Le schéma d'architecture est à jour
- [ ] Les captures d'écran du dashboard Grafana sont dans le README
- [ ] Le flux end-to-end fonctionne (menu → order → delivery)

---

## 🎯 Estimation du temps

- **Priorité 1** : 1h30-2h
- **Priorité 2** : 1h-1h30
- **Priorité 3** : 30min
- **Priorité 4** : 30min
- **Tests** : 30min

**Total estimé** : 4h-5h
