# Requêtes PromQL pour Eat Now Microservices

## 📊 Métriques HTTP

### Nombre de requêtes HTTP par seconde (tous services)

```promql
rate(http_server_requests_seconds_count{namespace="eat-now"}[1m])
```

### Nombre total de requêtes HTTP par service

```promql
sum(rate(http_server_requests_seconds_count{namespace="eat-now"}[1m])) by (service)
```

### Taux d'erreurs HTTP (4xx et 5xx) par service

```promql
sum(rate(http_server_requests_seconds_count{namespace="eat-now",status=~"4..|5.."}[1m])) by (service, status)
```

### Temps de réponse moyen (en millisecondes)

```promql
rate(http_server_requests_seconds_sum{namespace="eat-now"}[1m]) / rate(http_server_requests_seconds_count{namespace="eat-now"}[1m]) * 1000
```

### Temps de réponse p95 (95e percentile)

```promql
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{namespace="eat-now"}[1m])) by (le, service, uri))
```

### Temps de réponse p99 (99e percentile)

```promql
histogram_quantile(0.99, sum(rate(http_server_requests_seconds_bucket{namespace="eat-now"}[1m])) by (le, service, uri))
```

### Top 5 endpoints les plus lents

```promql
topk(5, rate(http_server_requests_seconds_sum{namespace="eat-now"}[5m]) / rate(http_server_requests_seconds_count{namespace="eat-now"}[5m]))
```

### Requêtes HTTP par méthode (GET, POST, PUT, DELETE)

```promql
sum(rate(http_server_requests_seconds_count{namespace="eat-now"}[1m])) by (method)
```

## 🔄 Résilience (Resilience4j)

### État du Circuit Breaker

```promql
resilience4j_circuitbreaker_state{namespace="eat-now"}
```

_Valeurs: 0=CLOSED, 1=OPEN, 2=HALF_OPEN_

### Nombre d'appels réussis du Circuit Breaker

```promql
rate(resilience4j_circuitbreaker_calls_total{namespace="eat-now",kind="successful"}[1m])
```

### Nombre d'appels échoués du Circuit Breaker

```promql
rate(resilience4j_circuitbreaker_calls_total{namespace="eat-now",kind="failed"}[1m])
```

### Taux d'échec du Circuit Breaker (%)

```promql
rate(resilience4j_circuitbreaker_calls_total{namespace="eat-now",kind="failed"}[1m]) / rate(resilience4j_circuitbreaker_calls_total{namespace="eat-now"}[1m]) * 100
```

### Nombre de retries (tentatives)

```promql
rate(resilience4j_retry_calls_total{namespace="eat-now"}[1m])
```

### Nombre de retries réussis vs échoués

```promql
sum(rate(resilience4j_retry_calls_total{namespace="eat-now"}[1m])) by (kind)
```

## 💻 Utilisation des ressources

### Utilisation CPU (%)

```promql
sum(rate(process_cpu_usage{namespace="eat-now"}[1m])) by (service) * 100
```

### Utilisation Mémoire Heap (MB)

```promql
jvm_memory_used_bytes{namespace="eat-now",area="heap"} / 1048576
```

### Utilisation Mémoire Non-Heap (MB)

```promql
jvm_memory_used_bytes{namespace="eat-now",area="nonheap"} / 1048576
```

### Mémoire maximum disponible (MB)

```promql
jvm_memory_max_bytes{namespace="eat-now",area="heap"} / 1048576
```

### Pourcentage d'utilisation de la mémoire Heap

```promql
(jvm_memory_used_bytes{namespace="eat-now",area="heap"} / jvm_memory_max_bytes{namespace="eat-now",area="heap"}) * 100
```

### Threads actifs dans la JVM

```promql
jvm_threads_live_threads{namespace="eat-now"}
```

### Threads démons dans la JVM

```promql
jvm_threads_daemon_threads{namespace="eat-now"}
```

## 🗑️ Garbage Collection

### Temps passé en GC (secondes)

```promql
rate(jvm_gc_pause_seconds_sum{namespace="eat-now"}[1m])
```

### Nombre de GC par seconde

```promql
rate(jvm_gc_pause_seconds_count{namespace="eat-now"}[1m])
```

### Temps moyen de GC (ms)

```promql
rate(jvm_gc_pause_seconds_sum{namespace="eat-now"}[1m]) / rate(jvm_gc_pause_seconds_count{namespace="eat-now"}[1m]) * 1000
```

## 🚀 Disponibilité et Uptime

### Services actifs (1 = UP, 0 = DOWN)

```promql
up{job=~"menu-service|order-service|delivery-service"}
```

### Nombre total de pods actifs

```promql
count(up{job=~"menu-service|order-service|delivery-service"} == 1)
```

### Temps depuis le dernier démarrage (heures)

```promql
(time() - process_start_time_seconds{namespace="eat-now"}) / 3600
```

## 📦 Base de données / Connexions

### Connexions actives (si applicable)

```promql
hikaricp_connections_active{namespace="eat-now"}
```

### Connexions idle

```promql
hikaricp_connections_idle{namespace="eat-now"}
```

### Timeout de connexions

```promql
rate(hikaricp_connections_timeout_total{namespace="eat-now"}[1m])
```

## 🌐 Requêtes inter-services

### Appels de order-service vers menu-service

```promql
rate(http_client_requests_seconds_count{service="order-service",uri=~".*menu.*"}[1m])
```

### Appels de delivery-service vers order-service

```promql
rate(http_client_requests_seconds_count{service="delivery-service",uri=~".*order.*"}[1m])
```

## 📈 Métriques Business

### Nombre de plats consultés par seconde

```promql
rate(http_server_requests_seconds_count{service="menu-service",uri="/api/dishes",method="GET"}[1m])
```

### Nombre de commandes créées par seconde

```promql
rate(http_server_requests_seconds_count{service="order-service",uri="/api/orders",method="POST"}[1m])
```

### Nombre de livraisons créées par seconde

```promql
rate(http_server_requests_seconds_count{service="delivery-service",uri="/api/deliveries",method="POST"}[1m])
```

## 🎯 Alertes suggérées

### Alerte : Taux d'erreur élevé (> 5%)

```promql
(sum(rate(http_server_requests_seconds_count{namespace="eat-now",status=~"5.."}[5m])) / sum(rate(http_server_requests_seconds_count{namespace="eat-now"}[5m]))) * 100 > 5
```

### Alerte : Temps de réponse élevé (> 1 seconde)

```promql
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{namespace="eat-now"}[5m])) by (le, service)) > 1
```

### Alerte : Utilisation CPU élevée (> 80%)

```promql
sum(rate(process_cpu_usage{namespace="eat-now"}[1m])) by (service) * 100 > 80
```

### Alerte : Utilisation mémoire élevée (> 80%)

```promql
(jvm_memory_used_bytes{namespace="eat-now",area="heap"} / jvm_memory_max_bytes{namespace="eat-now",area="heap"}) * 100 > 80
```

### Alerte : Service DOWN

```promql
up{job=~"menu-service|order-service|delivery-service"} == 0
```

### Alerte : Circuit Breaker OPEN

```promql
resilience4j_circuitbreaker_state{namespace="eat-now"} == 1
```

## 🔍 Debugging et Troubleshooting

### Requêtes HTTP les plus lentes (top 10)

```promql
topk(10, sum(rate(http_server_requests_seconds_sum{namespace="eat-now"}[5m])) by (service, uri, method) / sum(rate(http_server_requests_seconds_count{namespace="eat-now"}[5m])) by (service, uri, method))
```

### Services avec le plus d'erreurs

```promql
topk(5, sum(rate(http_server_requests_seconds_count{namespace="eat-now",status=~"5.."}[5m])) by (service))
```

### Évolution du nombre de threads dans le temps

```promql
jvm_threads_live_threads{namespace="eat-now"}
```
