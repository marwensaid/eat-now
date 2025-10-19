# 🍽️ EatNow - Application de Restauration Microservices

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-1.25+-blue.svg)](https://kubernetes.io/)
[![Prometheus](https://img.shields.io/badge/Prometheus-2.48-red.svg)](https://prometheus.io/)
[![Grafana](https://img.shields.io/badge/Grafana-10.2-orange.svg)](https://grafana.com/)

Application de restauration type Uber Eats composée de 3 microservices Spring Boot déployés sur Kubernetes avec monitoring Prometheus/Grafana et résilience Resilience4j.

## ✅ Statut du Déploiement

**Dernière mise à jour** : 2025-10-19  
**Statut** : 🟢 **OPÉRATIONNEL**

| Service | Pods | Status | Tests |
|---------|------|--------|-------|
| menu-service | 2/2 | ✅ Running | ✅ Passed |
| order-service | 2/2 | ✅ Running | ✅ Passed |
| delivery-service | 2/2 | ✅ Running | ✅ Passed |
| prometheus | 1/1 | ✅ Running | ✅ Collecting |
| grafana | 1/1 | ✅ Running | ✅ Accessible |

**Tests E2E** : ✅ Tous les tests passent (voir `./test-e2e.sh`)  
**Documentation** : [VERIFICATION.md](VERIFICATION.md)

---

## 🚀 Démarrage rapide

```bash
# Build et déploiement automatique
chmod +x build-and-deploy.sh
./build-and-deploy.sh

# Ou déploiement manuel
kubectl apply -k k8s/
```

**📖 Pour le guide complet de déploiement, consultez [DEPLOYMENT.md](DEPLOYMENT.md)**

---

## 📋 Vue d'ensemble

### Architecture

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

### Microservices

| Service | Description | Port | Endpoints |
|---------|-------------|------|-----------|
| **menu-service** | Gestion du catalogue des plats | 8080 | `/menu/api/menu/*` |
| **order-service** | Gestion des commandes | 8080 | `/order/api/orders/*` |
| **delivery-service** | Gestion des livraisons | 8080 | `/delivery/api/deliveries/*` |

### Fonctionnalités clés

✅ **Communication inter-services** : REST avec validation des plats et création automatique de livraison  
✅ **Résilience** : Circuit Breaker, Retry (3x), Timeout (2s) via Resilience4j  
✅ **Monitoring** : Prometheus + Grafana avec dashboard pré-configuré  
✅ **Auto-scaling** : HPA basé sur CPU (50%)  
✅ **Documentation** : Swagger/OpenAPI sur chaque service  
✅ **Observabilité** : Métriques Prometheus via Actuator  

---

## 📚 Documentation

- **[DEPLOYMENT.md](DEPLOYMENT.md)** - Guide complet de déploiement et tests
- **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** - Résumé de l'implémentation
- **[TODO.md](TODO.md)** - Liste des tâches (toutes complétées ✅)

---

## 🔧 Technologies utilisées

### Backend
- **Java 17** - Langage de programmation
- **Spring Boot 3.5.6** - Framework applicatif
- **Gradle 8.x** - Gestion des dépendances
- **Resilience4j** - Patterns de résilience
- **Micrometer** - Métriques applicatives
- **SpringDoc OpenAPI** - Documentation API

### Infrastructure
- **Docker** - Containerisation
- **Kubernetes** - Orchestration
- **Prometheus** - Collecte de métriques
- **Grafana** - Visualisation
- **Ingress NGINX** - Routage HTTP

---

## 🌐 Accès aux services

### Via Ingress (après configuration de /etc/hosts)

```bash
echo "$(minikube ip) eatnow.local" | sudo tee -a /etc/hosts
```

| Service | URL |
|---------|-----|
| Menu API | http://eatnow.local/menu/api/menu/dishes |
| Menu Swagger | http://eatnow.local/menu/swagger-ui.html |
| Order API | http://eatnow.local/order/api/orders |
| Order Swagger | http://eatnow.local/order/swagger-ui.html |
| Delivery API | http://eatnow.local/delivery/api/deliveries |
| Delivery Swagger | http://eatnow.local/delivery/swagger-ui.html |

### Monitoring

| Service | URL | Credentials |
|---------|-----|-------------|
| Prometheus | http://$(minikube ip):30090 | - |
| Grafana | http://$(minikube ip):30300 | admin/admin |

---

## ✅ Tests rapides

### 1. Créer une commande

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

### 2. Vérifier la livraison créée automatiquement

```bash
curl http://eatnow.local/delivery/api/deliveries
```

### 3. Tester le Circuit Breaker

```bash
# Arrêter le menu-service
kubectl scale deployment menu-service --replicas=0

# Tenter de créer une commande (fallback activé)
curl -X POST http://eatnow.local/order/api/orders ...

# Redémarrer
kubectl scale deployment menu-service --replicas=2
```

---

## 📊 Monitoring

Le dashboard Grafana "EatNow Microservices Dashboard" affiche :

- **HTTP Requests Rate** : Taux de requêtes/s
- **HTTP Error Rate** : Pourcentage d'erreurs 5xx
- **Average Response Time** : Temps de réponse moyen
- **Circuit Breaker State** : État des circuit breakers
- **Total Requests** : Par service

---

## 🛠️ Développement

### Build local

```bash
cd menu-service
./gradlew clean build
```

### Tests

```bash
./gradlew test
```

### Logs

```bash
kubectl logs -f -l app=order-service
```

---

## 📝 Licence

MIT License

---

## 👥 Auteurs

EatNow Team - Projet d'examen Architecture Microservices & Cloud-Native

---

**📖 Pour plus de détails, consultez [DEPLOYMENT.md](DEPLOYMENT.md)**

---

## 🎓 Contexte académique - Examen Pratique

⸻

🎯 Objectif général

Vous devez concevoir, développer et déployer une application simplifiée de restauration (type Uber Eats) composée de 3 microservices Spring Boot.

Chaque microservice sera :
-	autonome (pas de base de données externe),
-	déployé sur Kubernetes,
-	instrumenté pour la supervision (Prometheus + Grafana),
-	résilient (circuit breaker, retry, timeout),
-	documenté (Swagger / OpenAPI),
-	scalable (HPA ou paramétrage du déploiement).

Vous avez 4 heures pour :
1.	Développer les 3 microservices,
2.	Les dockeriser et les déployer sur Kubernetes (avec Helm ou manifests YAML),
3.	Mettre en place le monitoring,
4.	Réaliser un schéma d’architecture global (Draw.io),
5.	Préparer un README clair pour le déploiement et le test.

⸻

🧩 1. Contexte fonctionnel

L’application EatNow permet à un utilisateur :
-	de consulter des plats disponibles,
-	de passer une commande,
-	de suivre la livraison de sa commande.

L’ensemble repose sur 3 microservices qui communiquent entre eux :

|   **Microservice**   |   **Rôle principal**   |   **Exemples de fonctionnalités **   |
| --- | --- | --- |
|   **menu-service**   |   Gestion du catalogue des plats   |   \- Lister tous les plats- Consulter le détail d’un plat- Ajouter un plat- Modifier ou supprimer un plat   |
|   **order-service**   |   Gestion des commandes clients   |   \- Créer une commande (références plats)- Consulter une commande- Lister les commandes d’un utilisateur- Mettre à jour le statut (CREATED → DELIVERED)   |
|   **delivery-service**   |   Gestion des livraisons   |   \- Créer une livraison associée à une commande- Assigner un livreur- Modifier le statut de livraison- Consulter la livraison   |

Les données peuvent être stockées en mémoire (List ou Map).
Aucune base de données externe n’est requise.

⸻

⚙️ 2. Exigences techniques

🧱 Développement  
-	Langage : Java 17
-	Framework : Spring Boot 3
-	Gestion de dépendances : Maven ou Gradle
-	Communication inter-services : appels HTTP REST (WebClient ou RestTemplate)
-	Résilience : Resilience4j (Circuit Breaker, Retry, Timeout)
-	Observabilité : Micrometer + Actuator (endpoint /actuator/prometheus)
-	Documentation : Swagger/OpenAPI via springdoc-openapi
-	Stockage : collections en mémoire (aucune DB externe)

⸻

☸️ Déploiement Kubernetes

Chaque microservice doit :
-	être containerisé avec Docker,
-	avoir un Deployment et un Service exposé (type NodePort ou LoadBalancer),
-	comporter des probes (liveness et readiness),
-	inclure un HorizontalPodAutoscaler (HPA) basé sur l’utilisation CPU,
-	être accessible via un point d’entrée unique (Ingress ou NodePort).

Optionnel : utilisation d’un Helm chart pour le déploiement global.

⸻

📈 Observabilité & Monitoring

Vous devez déployer :
-	Prometheus pour la collecte des métriques des 3 microservices,
-	Grafana pour la visualisation.

⚠️ Le code et les manifests de Prometheus & Grafana vous sont fournis dans le sujet (ou disponibles sur le dépôt de cours).
Votre tâche est de :
-	configurer les ServiceMonitor ou les cibles dans Prometheus,
-	créer un dashboard Grafana simple affichant :
-	le nombre de requêtes HTTP reçues par service,
-	le taux d’erreurs,
-	le temps de réponse moyen.

⸻

🧩 Résilience

Implémentez dans vos appels inter-services :
-	un circuit breaker,
-	un retry (avec 2 ou 3 tentatives),
-	un timeout (2s max).

Prévoyez un fallback (ex. message d’erreur simple si le service appelé est indisponible).

⸻

📜 Documentation

Chaque microservice doit exposer son interface via Swagger :
-	accessible sur /swagger-ui.html ou /api-docs.

⸻

🧠 3. Partie théorique

Vous devez dessiner l’architecture complète de l’application sur Draw.io (ou équivalent).

Votre schéma doit montrer :
-	les 3 microservices avec leurs interactions (REST),
-	les flux de communication (internes / externes),
-	les composants de monitoring (Prometheus, Grafana),
-	les mécanismes de résilience,
-	la scalabilité (HPA),
-	les points d’exposition (Ingress, NodePort, etc.).

🧩 Format attendu :
-	Fichier .drawio ou export .png dans votre dépôt,
-	Schéma lisible et cohérent avec votre implémentation.

⸻

📦 4. Livrables attendus

À la fin des 4 heures, vous devez avoir :
1.	3 dossiers de microservices : menu-service, order-service, delivery-service
2.	Un Dockerfile par service
3.	Les manifests Kubernetes (ou un chart Helm)
4.	Le fichier d’architecture Draw.io
5.	Le README.md avec :
	-	instructions de build et déploiement,
	-	URLs d’accès aux services,
	-	liens vers la documentation API,
	-	captures d’écran du dashboard Grafana.

⸻

🧭 5. Conseils
Concentrez-vous sur la simplicité fonctionnelle et la cohérence technique.
-	Vous pouvez utiliser des listes statiques (List, Map) pour simuler les données.
-	Utilisez les Actuator endpoints pour exposer les métriques.
-	Si un microservice ne fonctionne pas, documentez ce que vous avez tenté.

⸻
