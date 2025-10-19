# 🚀 Guide de Déploiement - EatNow Microservices

## 📋 Table des matières

1. [Vue d'ensemble](#vue-densemble)
2. [Prérequis](#prérequis)
3. [Architecture](#architecture)
4. [Build des microservices](#build-des-microservices)
5. [Déploiement Kubernetes](#déploiement-kubernetes)
6. [Accès aux services](#accès-aux-services)
7. [Tests de validation](#tests-de-validation)
8. [Monitoring et observabilité](#monitoring-et-observabilité)
9. [Résilience et Circuit Breaker](#résilience-et-circuit-breaker)
10. [Troubleshooting](#troubleshooting)

---

## 🎯 Vue d'ensemble

**EatNow** est une application de restauration type Uber Eats composée de 3 microservices Spring Boot déployés sur Kubernetes :

- **menu-service** : Gestion du catalogue des plats
- **order-service** : Gestion des commandes avec validation des plats et création automatique de livraison
- **delivery-service** : Gestion des livraisons

### Fonctionnalités clés

✅ Communication inter-services via REST  
✅ Résilience avec Resilience4j (Circuit Breaker, Retry, Time Limiter)  
✅ Monitoring avec Prometheus + Grafana  
✅ Auto-scaling avec HPA (Horizontal Pod Autoscaler)  
✅ API documentée avec Swagger/OpenAPI  
✅ Métriques Prometheus exposées via Actuator  

---

## 🔧 Prérequis

### Outils requis

- **Docker** : v20.10+
- **Kubernetes** : v1.25+ (Minikube, Kind, ou cluster K8s)
- **kubectl** : v1.25+
- **Java** : JDK 17
- **Gradle** : 8.x (wrapper inclus)
- **Git** : Pour cloner le projet

### Vérification

```bash
docker --version
kubectl version --client
java -version
```

---

## 🏗️ Architecture

### Schéma d'architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         Ingress                              │
│                    (eatnow.local)                            │
└────────────┬────────────┬────────────┬──────────────────────┘
             │            │            │
    ┌────────▼───┐  ┌────▼─────┐  ┌──▼──────────┐
    │  Menu      │  │  Order   │  │  Delivery   │
    │  Service   │◄─┤  Service │─►│  Service    │
    │  :8080     │  │  :8080   │  │  :8080      │
    └────────────┘  └──────────┘  └─────────────┘
         │               │              │
         └───────────────┴──────────────┘
                         │
                    ┌────▼─────┐
                    │Prometheus│
                    │  :9090   │
                    └────┬─────┘
                         │
                    ┌────▼─────┐
                    │ Grafana  │
                    │  :3000   │
                    └──────────┘
```

### Communication inter-services

- **order-service** → **menu-service** : Validation des plats avant création de commande
- **order-service** → **delivery-service** : Création automatique de livraison après commande

### Résilience

- **Circuit Breaker** : Protège contre les défaillances en cascade
- **Retry** : 3 tentatives avec délai de 1s
- **Time Limiter** : Timeout de 2s par appel

---

## 🔨 Build des microservices

### 1. Cloner le projet

```bash
git clone <repository-url>
cd eat-now
```

### 2. Builder les microservices

#### Menu Service

```bash
cd menu-service
./gradlew clean build -x test
docker build -t janovp/menu-service:latest .
docker push janovp/menu-service:latest
cd ..
```

#### Order Service

```bash
cd order-service
./gradlew clean build -x test
docker build -t janovp/order-service:latest .
docker push janovp/order-service:latest
cd ..
```

#### Delivery Service

```bash
cd delivery-service
./gradlew clean build -x test
docker build -t janovp/delivery-service:latest .
docker push janovp/delivery-service:latest
cd ..
```

### 3. Script de build automatique

Un script `build-and-deploy.sh` est fourni pour automatiser le processus :

```bash
chmod +x build-and-deploy.sh
./build-and-deploy.sh
```

---

## ☸️ Déploiement Kubernetes

### 1. Démarrer Minikube (si applicable)

```bash
minikube start --cpus=4 --memory=8192
minikube addons enable ingress
minikube addons enable metrics-server
```

### 2. Déployer tous les services

```bash
cd k8s
kubectl apply -k .
```

Cette commande déploie :
- Les 3 microservices (Deployments + Services + HPA)
- Prometheus (ConfigMap + Deployment + Service)
- Grafana (ConfigMaps + Deployment + Service)
- Ingress pour l'accès externe

### 3. Vérifier le déploiement

```bash
# Vérifier les pods
kubectl get pods

# Vérifier les services
kubectl get svc

# Vérifier les HPA
kubectl get hpa

# Vérifier l'ingress
kubectl get ingress
```

### 4. Attendre que tous les pods soient prêts

```bash
kubectl wait --for=condition=ready pod -l app=menu-service --timeout=300s
kubectl wait --for=condition=ready pod -l app=order-service --timeout=300s
kubectl wait --for=condition=ready pod -l app=delivery-service --timeout=300s
kubectl wait --for=condition=ready pod -l app=prometheus --timeout=300s
kubectl wait --for=condition=ready pod -l app=grafana --timeout=300s
```

### 5. Configurer l'accès Ingress

Ajouter à `/etc/hosts` :

```bash
echo "$(minikube ip) eatnow.local" | sudo tee -a /etc/hosts
```

---

## 🌐 Accès aux services

### Via Ingress (recommandé)

| Service | URL | Description |
|---------|-----|-------------|
| Menu Service | http://eatnow.local/menu/api/menu/dishes | Liste des plats |
| Menu Swagger | http://eatnow.local/menu/swagger-ui.html | Documentation API |
| Order Service | http://eatnow.local/order/api/orders | Gestion des commandes |
| Order Swagger | http://eatnow.local/order/swagger-ui.html | Documentation API |
| Delivery Service | http://eatnow.local/delivery/api/deliveries | Gestion des livraisons |
| Delivery Swagger | http://eatnow.local/delivery/swagger-ui.html | Documentation API |

### Via NodePort (alternative)

```bash
# Prometheus
minikube service prometheus --url
# Accès : http://<minikube-ip>:30090

# Grafana
minikube service grafana --url
# Accès : http://<minikube-ip>:30300
# Login : admin / admin
```

### Monitoring

| Service | URL | Credentials |
|---------|-----|-------------|
| Prometheus | http://$(minikube ip):30090 | - |
| Grafana | http://$(minikube ip):30300 | admin / admin |

---

## ✅ Tests de validation

### 1. Test du flux complet

#### Étape 1 : Lister les plats disponibles

```bash
curl -X GET http://eatnow.local/menu/api/menu/dishes
```

**Réponse attendue** : Liste des plats avec leurs IDs

#### Étape 2 : Créer une commande

```bash
curl -X POST http://eatnow.local/order/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "dishIds": ["1", "2"],
    "totalAmount": 25.50,
    "deliveryAddress": "123 Rue de Paris, 75001 Paris",
    "customerName": "Jean Dupont"
  }'
```

**Réponse attendue** : Commande créée avec `deliveryId` renseigné

#### Étape 3 : Vérifier la livraison créée automatiquement

```bash
curl -X GET http://eatnow.local/delivery/api/deliveries
```

**Réponse attendue** : La livraison correspondant à la commande

#### Étape 4 : Mettre à jour le statut de la commande

```bash
curl -X PUT http://eatnow.local/order/api/orders/{orderId}/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CONFIRMED"
  }'
```

### 2. Test de résilience (Circuit Breaker)

#### Arrêter le menu-service

```bash
kubectl scale deployment menu-service --replicas=0
```

#### Tenter de créer une commande

```bash
curl -X POST http://eatnow.local/order/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user456",
    "dishIds": ["1"],
    "totalAmount": 15.00,
    "deliveryAddress": "456 Avenue des Champs, 75008 Paris",
    "customerName": "Marie Martin"
  }'
```

**Réponse attendue** : Erreur 500 avec message "Menu service is currently unavailable"

#### Vérifier les métriques du Circuit Breaker dans Grafana

Accéder au dashboard "EatNow Microservices Dashboard" et observer le panel "Circuit Breaker State"

#### Redémarrer le menu-service

```bash
kubectl scale deployment menu-service --replicas=2
```

### 3. Test de scalabilité (HPA)

#### Générer de la charge

```bash
# Installer hey (outil de load testing)
# Linux
wget https://hey-release.s3.us-east-2.amazonaws.com/hey_linux_amd64
chmod +x hey_linux_amd64
sudo mv hey_linux_amd64 /usr/local/bin/hey

# Générer 10000 requêtes avec 50 workers
hey -n 10000 -c 50 http://eatnow.local/menu/api/menu/dishes
```

#### Observer le scaling automatique

```bash
kubectl get hpa -w
```

**Résultat attendu** : Le nombre de replicas augmente automatiquement quand le CPU dépasse 50%

#### Vérifier dans Grafana

Observer l'augmentation du taux de requêtes et du temps de réponse dans le dashboard

---

## 📊 Monitoring et observabilité

### Prometheus

**URL** : http://$(minikube ip):30090

#### Métriques disponibles

- `http_server_requests_seconds_count` : Nombre de requêtes HTTP
- `http_server_requests_seconds_sum` : Temps total de traitement
- `resilience4j_circuitbreaker_state` : État du circuit breaker
- `resilience4j_retry_calls_total` : Nombre de retries

#### Requêtes PromQL utiles

```promql
# Taux de requêtes par seconde
rate(http_server_requests_seconds_count{job="order-service"}[1m])

# Temps de réponse moyen
rate(http_server_requests_seconds_sum[1m]) / rate(http_server_requests_seconds_count[1m])

# Taux d'erreur
100 * (sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) / sum(rate(http_server_requests_seconds_count[5m])))
```

### Grafana

**URL** : http://$(minikube ip):30300  
**Login** : admin / admin

#### Dashboard "EatNow Microservices Dashboard"

Le dashboard pré-configuré affiche :

1. **HTTP Requests Rate** : Taux de requêtes par seconde pour chaque service
2. **HTTP Error Rate** : Pourcentage d'erreurs HTTP (5xx)
3. **Average Response Time** : Temps de réponse moyen par endpoint
4. **Circuit Breaker State** : État des circuit breakers (CLOSED, OPEN, HALF_OPEN)
5. **Total Requests per Service** : Nombre total de requêtes par service

#### Accès au dashboard

1. Se connecter à Grafana
2. Aller dans "Dashboards" → "Browse"
3. Sélectionner "EatNow Microservices Dashboard"

### Actuator Endpoints

Chaque service expose des endpoints Actuator :

```bash
# Health check
curl http://eatnow.local/menu/actuator/health

# Métriques Prometheus
curl http://eatnow.local/menu/actuator/prometheus

# Informations sur l'application
curl http://eatnow.local/menu/actuator/info
```

---

## 🛡️ Résilience et Circuit Breaker

### Configuration Resilience4j

La configuration est définie dans `order-service/src/main/resources/application.properties` :

```properties
# Circuit Breaker
resilience4j.circuitbreaker.instances.menuService.slidingWindowSize=10
resilience4j.circuitbreaker.instances.menuService.failureRateThreshold=50
resilience4j.circuitbreaker.instances.menuService.waitDurationInOpenState=10000

# Retry
resilience4j.retry.instances.menuService.maxAttempts=3
resilience4j.retry.instances.menuService.waitDuration=1000

# Time Limiter
resilience4j.timelimiter.instances.menuService.timeoutDuration=2s
```

### Comportement du Circuit Breaker

1. **CLOSED** (fermé) : Toutes les requêtes passent normalement
2. **OPEN** (ouvert) : Après 50% d'échecs sur 10 requêtes, le circuit s'ouvre et les requêtes sont rejetées immédiatement
3. **HALF_OPEN** (semi-ouvert) : Après 10 secondes, le circuit teste si le service est de nouveau disponible

### Méthodes Fallback

En cas d'échec, les méthodes fallback sont appelées :

- **MenuServiceClient** : Retourne une réponse indiquant que le service est indisponible
- **DeliveryServiceClient** : Retourne null et log l'erreur (la livraison peut être créée manuellement plus tard)

---

## 🔍 Troubleshooting

### Problèmes résolus lors du déploiement

#### 1. Erreur "CrashLoopBackOff" - Health check paths incorrects

**Symptôme** : Les pods redémarrent continuellement avec l'erreur `CrashLoopBackOff`

**Cause** : Les health checks utilisaient `/actuator/health` mais les services ont des context paths (`/menu`, `/order`, `/delivery`)

**Solution** : Corriger les paths dans les deployments :
```yaml
livenessProbe:
  httpGet:
    path: /menu/actuator/health  # Au lieu de /actuator/health
    port: 8081
readinessProbe:
  httpGet:
    path: /menu/actuator/health
    port: 8081
```

#### 2. Erreur Java 17 - ProcessorMetrics NullPointerException

**Symptôme** : Les services menu et delivery crashent avec l'erreur :
```
Cannot invoke "jdk.internal.platform.CgroupInfo.getMountPoint()" because "anyController" is null
```

**Cause** : Bug connu de Java 17 avec Micrometer dans les conteneurs Docker (problème de détection des cgroups v2)

**Solution** : Désactiver les métriques ProcessorMetrics et JVM dans `application.properties` :
```properties
management.metrics.binders.processor.enabled=false
management.metrics.binders.jvm.enabled=false
```

#### 3. Communication inter-services - DNS Kubernetes

**Symptôme** : Les services ne peuvent pas communiquer entre eux

**Solution** : Utiliser les noms DNS Kubernetes dans `application.properties` :
```properties
menu.service.url=http://menu-service:8080/menu
delivery.service.url=http://delivery-service:8080/delivery
```

### Les pods ne démarrent pas

```bash
# Vérifier les logs
kubectl logs -l app=menu-service

# Décrire le pod pour voir les événements
kubectl describe pod <pod-name>

# Vérifier les ressources
kubectl top nodes
kubectl top pods
```

### Erreur "ImagePullBackOff"

```bash
# Vérifier que les images sont disponibles
docker images | grep janovp

# Re-push les images
docker push janovp/menu-service:latest
docker push janovp/order-service:latest
docker push janovp/delivery-service:latest
```

### L'Ingress ne fonctionne pas

```bash
# Vérifier que l'addon ingress est activé (Minikube)
minikube addons enable ingress

# Vérifier l'ingress
kubectl get ingress
kubectl describe ingress eatnow-ingress

# Vérifier /etc/hosts
cat /etc/hosts | grep eatnow.local
```

### Prometheus ne scrape pas les services

```bash
# Vérifier la configuration Prometheus
kubectl get configmap prometheus-config -o yaml

# Vérifier les targets dans Prometheus UI
# Aller sur http://<minikube-ip>:30090/targets

# Tester l'endpoint Prometheus d'un service
kubectl exec -it <menu-service-pod> -- curl localhost:8081/menu/actuator/prometheus
```

### Le HPA ne scale pas

```bash
# Vérifier que metrics-server est installé
kubectl get deployment metrics-server -n kube-system

# Minikube : activer metrics-server
minikube addons enable metrics-server

# Vérifier les métriques
kubectl top pods
```

### Circuit Breaker ne s'active pas

```bash
# Vérifier les logs du order-service
kubectl logs -l app=order-service -f

# Vérifier la configuration Resilience4j
kubectl exec -it <order-service-pod> -- cat /workspace/BOOT-INF/classes/application.properties | grep resilience4j
```

---

## 📝 Commandes utiles

### Gestion des pods

```bash
# Lister tous les pods
kubectl get pods

# Voir les logs en temps réel
kubectl logs -f <pod-name>

# Se connecter à un pod
kubectl exec -it <pod-name> -- /bin/sh

# Redémarrer un deployment
kubectl rollout restart deployment/<deployment-name>
```

### Gestion des services

```bash
# Lister les services
kubectl get svc

# Tester un service depuis un pod
kubectl run curl --image=curlimages/curl -i --tty --rm -- sh
curl http://menu-service:8080/menu/api/menu/dishes
```

### Nettoyage

```bash
# Supprimer tous les déploiements
kubectl delete -k k8s/

# Arrêter Minikube
minikube stop

# Supprimer le cluster Minikube
minikube delete
```

---

## 🎓 Résumé des URLs

| Service | Type | URL | Credentials |
|---------|------|-----|-------------|
| Menu API | REST | http://eatnow.local/menu/api/menu/dishes | - |
| Menu Swagger | UI | http://eatnow.local/menu/swagger-ui.html | - |
| Order API | REST | http://eatnow.local/order/api/orders | - |
| Order Swagger | UI | http://eatnow.local/order/swagger-ui.html | - |
| Delivery API | REST | http://eatnow.local/delivery/api/deliveries | - |
| Delivery Swagger | UI | http://eatnow.local/delivery/swagger-ui.html | - |
| Prometheus | Monitoring | http://$(minikube ip):30090 | - |
| Grafana | Dashboard | http://$(minikube ip):30300 | admin/admin |

---

## 🎯 Checklist de déploiement

- [x] Les 3 microservices sont buildés et pushés sur Docker Hub
- [x] Kubernetes est démarré avec ingress et metrics-server
- [x] Tous les pods sont en état "Running"
- [x] Les Services K8s sont créés et exposent le port 8080
- [x] L'Ingress est configuré et accessible via eatnow.local
- [x] Prometheus collecte les métriques des 3 services
- [x] Grafana affiche le dashboard "EatNow Microservices Dashboard"
- [x] Le flux end-to-end fonctionne (menu → order → delivery)
- [x] Le Circuit Breaker s'active en cas de défaillance
- [x] Le HPA scale automatiquement sous charge

---

## 📚 Ressources supplémentaires

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)

---

**Auteur** : EatNow Team  
**Version** : 1.0.0  
**Date** : 2025-10-19
