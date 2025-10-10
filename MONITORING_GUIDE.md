# 📊 Guide Monitoring - Prometheus + Grafana

## 🎯 Accès aux interfaces

### Prometheus

- **URL** : http://localhost:30090
- **Description** : Interface de requêtage PromQL et exploration des métriques
- **Targets** : http://localhost:30090/targets (voir l'état des services scrapés)

### Grafana

- **URL** : http://localhost:30300
- **Identifiants** :
  - Username: `admin`
  - Password: `admin123`

## 🚀 Premiers pas

### 1. Vérifier que Prometheus collecte les métriques

```bash
# Ouvrir Prometheus
open http://localhost:30090

# Vérifier les targets
open http://localhost:30090/targets
```

Vous devriez voir :

- ✅ `menu-service` (1/1 up)
- ✅ `order-service` (1/1 up)
- ✅ `delivery-service` (1/1 up)

### 2. Tester une requête PromQL dans Prometheus

Dans l'interface Prometheus, essayez ces requêtes :

```promql
# Voir tous les services actifs
up{job=~"menu-service|order-service|delivery-service"}

# Nombre de requêtes HTTP par seconde
rate(http_server_requests_seconds_count[1m])

# Utilisation CPU
process_cpu_usage * 100
```

### 3. Se connecter à Grafana

```bash
# Ouvrir Grafana
open http://localhost:30300

# Login : admin / admin123
```

### 4. Importer le dashboard

1. Cliquer sur **"+" → "Import"** dans le menu de gauche
2. Copier le contenu de `k8s/grafana-dashboard.json`
3. Cliquer sur **"Load"** puis **"Import"**

Ou depuis la ligne de commande :

```bash
# Récupérer l'API key de Grafana (pour automatisation)
kubectl get secret --namespace eat-now grafana -o jsonpath="{.data.admin-password}" | base64 --decode

# Importer le dashboard via API
curl -X POST http://admin:admin123@localhost:30300/api/dashboards/db \
  -H "Content-Type: application/json" \
  -d @k8s/grafana-dashboard.json
```

## 📊 Dashboards inclus

Le dashboard **"Eat Now - Microservices Monitoring"** contient :

### Panel 1 : Requêtes HTTP par seconde

- **PromQL** : `rate(http_server_requests_seconds_count{namespace="eat-now"}[1m])`
- **Description** : Nombre de requêtes HTTP reçues par service

### Panel 2 : Taux d'erreurs HTTP

- **PromQL** : `sum(rate(http_server_requests_seconds_count{namespace="eat-now",status=~"4..|5.."}[1m])) by (service, status)`
- **Description** : Erreurs 4xx et 5xx par service

### Panel 3 : Temps de réponse moyen

- **PromQL** : `rate(http_server_requests_seconds_sum[1m]) / rate(http_server_requests_seconds_count[1m]) * 1000`
- **Description** : Latence moyenne en millisecondes

### Panel 4 : Uptime des services

- **PromQL** : `up{job=~"menu-service|order-service|delivery-service"}`
- **Description** : État de disponibilité (1=UP, 0=DOWN)

### Panel 5 : Utilisation CPU

- **PromQL** : `sum(rate(process_cpu_usage{namespace="eat-now"}[1m])) by (service) * 100`
- **Description** : Pourcentage d'utilisation CPU

### Panel 6 : Utilisation Mémoire

- **PromQL** : `jvm_memory_used_bytes{area="heap"} / 1048576`
- **Description** : Mémoire Heap utilisée en MB

### Panel 7 : Nombre de Pods actifs

- **PromQL** : `count(up{job=~"menu-service|order-service|delivery-service"} == 1)`
- **Description** : Total de pods en état UP

### Panel 8 : Circuit Breaker - États

- **PromQL** : `resilience4j_circuitbreaker_state{namespace="eat-now"}`
- **Description** : État des circuit breakers (0=CLOSED, 1=OPEN, 2=HALF_OPEN)

### Panel 9 : Nombre de retries

- **PromQL** : `rate(resilience4j_retry_calls_total{namespace="eat-now"}[1m])`
- **Description** : Tentatives de retry par seconde

### Panel 10 : Top 10 endpoints

- **PromQL** : `topk(10, sum(rate(http_server_requests_seconds_count[5m])) by (service, uri, method))`
- **Description** : Endpoints les plus sollicités

## 🧪 Générer du trafic pour voir les métriques

### Créer du trafic HTTP

```bash
# Menu service - consulter les plats
for i in {1..100}; do curl -s http://eat-now.local/menu/api/dishes > /dev/null; done

# Order service - créer des commandes
for i in {1..50}; do
  curl -s -X POST http://eat-now.local/order/api/orders \
    -H "Content-Type: application/json" \
    -d "{\"userId\":\"user$i\",\"customerName\":\"Test User\",\"customerPhone\":\"+3360000000$i\",\"deliveryAddress\":\"Test Address $i\",\"items\":[{\"dishId\":1,\"quantity\":2}]}" > /dev/null
done

# Delivery service - créer des livraisons
for i in {1..30}; do
  curl -s -X POST http://eat-now.local/delivery/api/deliveries \
    -H "Content-Type: application/json" \
    -d "{\"orderId\":$i,\"deliveryAddress\":\"Test Address $i\",\"customerName\":\"Test User\",\"customerPhone\":\"+3360000000$i\"}" > /dev/null
done
```

### Générer des erreurs (pour tester les alertes)

```bash
# Générer des 404
for i in {1..50}; do curl -s http://eat-now.local/menu/api/dishes/999999 > /dev/null; done

# Générer des 500 (requête invalide)
for i in {1..20}; do
  curl -s -X POST http://eat-now.local/order/api/orders \
    -H "Content-Type: application/json" \
    -d "{}" > /dev/null
done
```

## 🔍 Requêtes PromQL utiles

Voir le fichier `PROMQL_QUERIES.md` pour la liste complète des requêtes.

### Quick Start - Top 5 requêtes

1. **Services UP/DOWN**

   ```promql
   up{job=~"menu-service|order-service|delivery-service"}
   ```

2. **Requêtes HTTP totales par service**

   ```promql
   sum(rate(http_server_requests_seconds_count{namespace="eat-now"}[1m])) by (service)
   ```

3. **Temps de réponse P95**

   ```promql
   histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{namespace="eat-now"}[1m])) by (le, service))
   ```

4. **Taux d'erreur (%)**

   ```promql
   (sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) / sum(rate(http_server_requests_seconds_count[5m]))) * 100
   ```

5. **Utilisation mémoire (%)**
   ```promql
   (jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100
   ```

## 📈 Créer des alertes personnalisées

### Dans Grafana

1. Aller sur un panel
2. Cliquer sur **"Alert"** → **"Create Alert"**
3. Définir la condition (ex: `WHEN avg() OF query(A, 5m, now) IS ABOVE 0.8`)
4. Configurer les notifications

### Exemple d'alerte : Taux d'erreur élevé

```yaml
- alert: HighErrorRate
  expr: |
    (sum(rate(http_server_requests_seconds_count{namespace="eat-now",status=~"5.."}[5m])) 
    / sum(rate(http_server_requests_seconds_count{namespace="eat-now"}[5m]))) * 100 > 5
  for: 2m
  labels:
    severity: warning
  annotations:
    summary: "Taux d'erreur élevé (> 5%)"
    description: "Le service {{ $labels.service }} a un taux d'erreur de {{ $value }}%"
```

## 🛠️ Commandes utiles

### Vérifier que Prometheus scrape les services

```bash
# Voir les targets Prometheus
kubectl port-forward -n eat-now svc/prometheus-server 9090:80
open http://localhost:9090/targets
```

### Voir les logs Prometheus

```bash
kubectl logs -n eat-now -l app.kubernetes.io/name=prometheus -c prometheus-server --tail=100 -f
```

### Voir les logs Grafana

```bash
kubectl logs -n eat-now -l app.kubernetes.io/name=grafana --tail=100 -f
```

### Redémarrer Prometheus

```bash
kubectl rollout restart deployment/prometheus-server -n eat-now
```

### Redémarrer Grafana

```bash
kubectl rollout restart deployment/grafana -n eat-now
```

## 🐛 Troubleshooting

### Prometheus ne collecte pas les métriques

1. Vérifier que les services exposent `/actuator/prometheus` :

   ```bash
   kubectl run test --rm -it --image=curlimages/curl --restart=Never -n eat-now -- \
     curl http://menu-service:8081/actuator/prometheus
   ```

2. Vérifier les targets dans Prometheus :

   ```bash
   open http://localhost:30090/targets
   ```

3. Vérifier la configuration Prometheus :
   ```bash
   kubectl get cm -n eat-now prometheus-server -o yaml
   ```

### Grafana ne se connecte pas à Prometheus

1. Vérifier la datasource dans Grafana :

   - Aller dans **Configuration → Data Sources**
   - Vérifier l'URL : `http://prometheus-server:80`
   - Cliquer sur **"Save & Test"**

2. Tester la connexion depuis un pod :
   ```bash
   kubectl run test --rm -it --image=curlimages/curl --restart=Never -n eat-now -- \
     curl http://prometheus-server/api/v1/query?query=up
   ```

### Le dashboard ne montre pas de données

1. Vérifier qu'il y a du trafic sur les services
2. Attendre 1-2 minutes pour que les métriques soient collectées
3. Vérifier la période de temps dans Grafana (en haut à droite)
4. Tester une requête PromQL simple dans Prometheus d'abord

## 📚 Ressources

- **PromQL Queries** : `PROMQL_QUERIES.md`
- **Dashboard JSON** : `k8s/grafana-dashboard.json`
- **Prometheus Config** : `k8s/prometheus-scrape-config.yaml`
- **Documentation Prometheus** : https://prometheus.io/docs/
- **Documentation Grafana** : https://grafana.com/docs/
- **Spring Boot Actuator** : https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html
