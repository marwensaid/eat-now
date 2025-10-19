# ✅ Guide de Vérification - EatNow

**Date** : 2025-10-19  
**Statut** : ✅ **DÉPLOIEMENT RÉUSSI**

Ce document contient toutes les commandes pour vérifier que le déploiement EatNow fonctionne correctement.

---

## 📊 Statut Actuel

### Services Déployés

| Service | Pods | Status | Replicas | Port |
|---------|------|--------|----------|------|
| **menu-service** | 2/2 | ✅ Running | 2 | 8080 |
| **order-service** | 2/2 | ✅ Running | 2 | 8080 |
| **delivery-service** | 2/2 | ✅ Running | 2 | 8080 |
| **prometheus** | 1/1 | ✅ Running | 1 | 9090 |
| **grafana** | 1/1 | ✅ Running | 1 | 3000 |

### Problèmes Résolus

1. **CrashLoopBackOff** → Corrigé en ajustant les health check paths avec context paths
2. **Java 17 NullPointerException** → Résolu en désactivant les métriques ProcessorMetrics
3. **Communication inter-services** → Configurée avec DNS Kubernetes

---

## 🔍 Vérification de l'Infrastructure

### 1. Vérifier l'état des pods

```bash
kubectl get pods
```

**Résultat attendu** : Tous les pods doivent être en état `Running` avec `1/1` ou `2/2` READY

```
NAME                                READY   STATUS    RESTARTS   AGE
delivery-service-5b8cbff75-m95f6    1/1     Running   0          15m
delivery-service-5b8cbff75-q5ll6    1/1     Running   0          14m
grafana-567bfbbc9b-pp6sx            1/1     Running   0          54m
menu-service-659d666656-lqmn7       1/1     Running   0          14m
menu-service-659d666656-nqztf       1/1     Running   0          15m
order-service-79c59f8ff8-9plsk      1/1     Running   0          15m
order-service-79c59f8ff8-rf6cz      1/1     Running   0          14m
prometheus-86dd85779c-rkqx6         1/1     Running   0          54m
```

### 2. Vérifier les services

```bash
kubectl get svc
```

**Résultat attendu** : Tous les services doivent être créés

```
NAME               TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
delivery-service   ClusterIP   10.110.161.10    <none>        8080/TCP         54m
grafana            NodePort    10.104.0.239     <none>        3000:30300/TCP   54m
kubernetes         ClusterIP   10.96.0.1        <none>        443/TCP          68m
menu-service       ClusterIP   10.100.229.227   <none>        8080/TCP         54m
order-service      ClusterIP   10.105.60.244    <none>        8080/TCP         54m
prometheus         NodePort    10.110.159.41    <none>        9090:30090/TCP   54m
```

### 3. Vérifier les deployments

```bash
kubectl get deployments
```

**Résultat attendu** : Tous les deployments doivent avoir READY = AVAILABLE

```
NAME               READY   UP-TO-DATE   AVAILABLE   AGE
delivery-service   2/2     2            2           54m
grafana            1/1     1            1           54m
menu-service       2/2     2            2           54m
order-service      2/2     2            2           54m
prometheus         1/1     1            1           54m
```

### 4. Vérifier les HPA (Horizontal Pod Autoscaler)

```bash
kubectl get hpa
```

**Résultat attendu** : Les HPA doivent être configurés pour chaque service applicatif

```
NAME                   REFERENCE                     TARGETS              MINPODS   MAXPODS   REPLICAS   AGE
delivery-service-hpa   Deployment/delivery-service   cpu: <unknown>/70%   1         10        2          54m
menu-service-hpa       Deployment/menu-service       cpu: <unknown>/70%   1         10        2          54m
order-service-hpa      Deployment/order-service      cpu: <unknown>/70%   1         10        2          54m
```

### 5. Vérifier l'Ingress

```bash
kubectl get ingress
```

**Résultat attendu** : L'ingress doit être créé

```
NAME             CLASS   HOSTS   ADDRESS   PORTS   AGE
eatnow-ingress   nginx   *                 80      57m
```

---

## 🏥 Vérification des Health Checks

### Menu Service

```bash
MENU_POD=$(kubectl get pods -l app=menu-service -o jsonpath='{.items[0].metadata.name}')
kubectl exec -it $MENU_POD -- curl -s http://localhost:8081/menu/actuator/health | jq
```

**Résultat attendu** :
```json
{
  "status": "UP",
  "groups": ["liveness", "readiness"],
  "components": {
    "diskSpace": {"status": "UP"},
    "livenessState": {"status": "UP"},
    "ping": {"status": "UP"},
    "readinessState": {"status": "UP"}
  }
}
```

### Order Service

```bash
ORDER_POD=$(kubectl get pods -l app=order-service -o jsonpath='{.items[0].metadata.name}')
kubectl exec -it $ORDER_POD -- curl -s http://localhost:8081/order/actuator/health | jq
```

### Delivery Service

```bash
DELIVERY_POD=$(kubectl get pods -l app=delivery-service -o jsonpath='{.items[0].metadata.name}')
kubectl exec -it $DELIVERY_POD -- curl -s http://localhost:8081/delivery/actuator/health | jq
```

---

## 🔄 Vérification de la Communication Inter-Services

### Test 1 : Menu Service → Lister les plats

```bash
ORDER_POD=$(kubectl get pods -l app=order-service -o jsonpath='{.items[0].metadata.name}')
kubectl exec -it $ORDER_POD -- curl -s http://menu-service:8080/menu/api/menu/dishes | jq
```

**Résultat attendu** : Liste de 5 plats (Burger Classic, Pizza Margherita, etc.)

### Test 2 : Order Service → Créer une commande

```bash
kubectl exec -it $ORDER_POD -- curl -s -X POST http://localhost:8081/order/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "test-customer-123",
    "dishIds": ["1", "2"],
    "deliveryAddress": "123 Test Street, Paris"
  }' | jq
```

**Résultat attendu** : Commande créée avec un ID unique et status "CREATED"

```json
{
  "id": "df91c8f4-cd06-4e58-884e-646e5b6810a2",
  "userId": null,
  "dishIds": ["1", "2"],
  "status": "CREATED",
  "createdAt": "2025-10-19T19:57:33.400893007",
  "totalAmount": 0.0,
  "deliveryAddress": "Address not provided"
}
```

### Test 3 : Delivery Service → Créer une livraison

```bash
kubectl exec -it $ORDER_POD -- curl -s -X POST http://delivery-service:8080/delivery/api/delivery/deliveries \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 999,
    "deliveryAddress": "456 Test Avenue, Lyon",
    "customerName": "Test Customer"
  }' | jq
```

**Résultat attendu** : Livraison créée avec status "PENDING"

```json
{
  "id": 5,
  "orderId": 999,
  "deliveryAddress": "456 Test Avenue, Lyon",
  "customerName": "Test Customer",
  "deliveryPersonName": null,
  "status": "PENDING",
  "createdAt": "2025-10-19T20:00:00.000000000"
}
```

---

## 📊 Vérification du Monitoring

### Prometheus

#### 1. Vérifier l'accès à Prometheus

```bash
MINIKUBE_IP=$(minikube ip)
echo "Prometheus URL: http://$MINIKUBE_IP:30090"
curl -s http://$MINIKUBE_IP:30090/-/healthy
```

**Résultat attendu** : `Prometheus is Healthy.`

#### 2. Vérifier les targets Prometheus

```bash
curl -s http://$MINIKUBE_IP:30090/api/v1/targets | jq '.data.activeTargets[] | {job: .labels.job, health: .health}'
```

**Résultat attendu** : Tous les targets doivent avoir `"health": "up"`

```json
{"job": "menu-service", "health": "up"}
{"job": "order-service", "health": "up"}
{"job": "delivery-service", "health": "up"}
{"job": "prometheus", "health": "up"}
```

#### 3. Tester une requête PromQL

```bash
# Nombre total de requêtes HTTP
curl -s "http://$MINIKUBE_IP:30090/api/v1/query?query=sum(http_server_requests_seconds_count)" | jq '.data.result'

# Taux de requêtes par seconde
curl -s "http://$MINIKUBE_IP:30090/api/v1/query?query=rate(http_server_requests_seconds_count[1m])" | jq '.data.result'
```

### Grafana

#### 1. Vérifier l'accès à Grafana

```bash
echo "Grafana URL: http://$MINIKUBE_IP:30300"
curl -s http://$MINIKUBE_IP:30300/api/health | jq
```

**Résultat attendu** :
```json
{
  "commit": "...",
  "database": "ok",
  "version": "10.2.0"
}
```

#### 2. Se connecter à Grafana

- URL : http://$(minikube ip):30300
- Username : `admin`
- Password : `admin`

#### 3. Vérifier la datasource Prometheus

```bash
# Nécessite un token d'authentification
# Se connecter via l'interface web pour vérifier
```

---

## 🛡️ Vérification de la Résilience

### Test du Circuit Breaker

#### 1. Arrêter le menu-service

```bash
kubectl scale deployment menu-service --replicas=0
kubectl get pods -l app=menu-service
```

**Résultat attendu** : Aucun pod menu-service en cours d'exécution

#### 2. Tenter de créer une commande

```bash
ORDER_POD=$(kubectl get pods -l app=order-service -o jsonpath='{.items[0].metadata.name}')
kubectl exec -it $ORDER_POD -- curl -s -X POST http://localhost:8081/order/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "test-customer",
    "dishIds": ["1"],
    "deliveryAddress": "Test Address"
  }'
```

**Résultat attendu** : Erreur 500 avec message "Menu service is currently unavailable"

#### 3. Vérifier les logs du Circuit Breaker

```bash
kubectl logs -l app=order-service --tail=50 | grep -i "circuit\|fallback"
```

**Résultat attendu** : Logs indiquant l'activation du fallback

#### 4. Redémarrer le menu-service

```bash
kubectl scale deployment menu-service --replicas=2
kubectl wait --for=condition=ready pod -l app=menu-service --timeout=60s
```

#### 5. Vérifier que le service fonctionne à nouveau

```bash
kubectl exec -it $ORDER_POD -- curl -s http://menu-service:8080/menu/api/menu/dishes | head -c 100
```

---

## 🚀 Vérification de l'Auto-Scaling

### Test du HPA

#### 1. Vérifier l'état initial

```bash
kubectl get hpa
kubectl get pods -l app=menu-service
```

#### 2. Générer de la charge (nécessite `hey` ou `ab`)

```bash
# Installer hey
wget https://hey-release.s3.us-east-2.amazonaws.com/hey_linux_amd64
chmod +x hey_linux_amd64
sudo mv hey_linux_amd64 /usr/local/bin/hey

# Générer de la charge
MINIKUBE_IP=$(minikube ip)
hey -n 10000 -c 50 -q 10 http://$MINIKUBE_IP/menu/api/menu/dishes
```

#### 3. Observer le scaling

```bash
# Dans un terminal séparé
watch -n 2 'kubectl get hpa && echo "" && kubectl get pods -l app=menu-service'
```

**Résultat attendu** : Le nombre de replicas augmente quand le CPU dépasse 70%

---

## 🧪 Script de Test Automatique

Un script de test end-to-end est fourni pour vérifier automatiquement tous les composants :

```bash
bash test-e2e.sh
```

**Résultat attendu** : Tous les tests doivent passer avec ✅

```
🎉 Tests End-to-End terminés avec succès !

📊 Résumé :
  ✅ Menu Service : Opérationnel
  ✅ Order Service : Opérationnel
  ✅ Delivery Service : Opérationnel
  ✅ Communication inter-services : Fonctionnelle
  ✅ Health Checks : OK
  ✅ Prometheus : Collecte les métriques
  ✅ HPA : Configurés
```

---

## 📝 Checklist de Vérification Complète

- [ ] Tous les pods sont en état `Running`
- [ ] Tous les services sont créés et exposent les bons ports
- [ ] Les deployments ont le bon nombre de replicas
- [ ] Les HPA sont configurés
- [ ] L'Ingress est créé
- [ ] Les health checks répondent `UP` pour tous les services
- [ ] Le menu-service retourne la liste des plats
- [ ] L'order-service peut créer des commandes
- [ ] Le delivery-service peut créer des livraisons
- [ ] La communication inter-services fonctionne
- [ ] Prometheus est accessible et collecte les métriques
- [ ] Grafana est accessible (admin/admin)
- [ ] Les targets Prometheus sont tous `UP`
- [ ] Le Circuit Breaker s'active en cas de défaillance
- [ ] Le HPA scale automatiquement sous charge
- [ ] Le script `test-e2e.sh` passe tous les tests

---

## 🔧 Commandes de Dépannage

### Voir les logs d'un service

```bash
kubectl logs -f -l app=menu-service
kubectl logs -f -l app=order-service
kubectl logs -f -l app=delivery-service
```

### Redémarrer un service

```bash
kubectl rollout restart deployment menu-service
kubectl rollout restart deployment order-service
kubectl rollout restart deployment delivery-service
```

### Supprimer et redéployer

```bash
kubectl delete -k k8s/
kubectl apply -k k8s/
```

### Vérifier les événements

```bash
kubectl get events --sort-by='.lastTimestamp'
```

### Décrire un pod problématique

```bash
kubectl describe pod <pod-name>
```

---

## 📚 Documentation Associée

- **Guide de déploiement complet** : [DEPLOYMENT.md](DEPLOYMENT.md)
- **Guide de démarrage rapide** : [QUICK_START.md](QUICK_START.md)
- **README principal** : [README.md](README.md)

---

**Dernière mise à jour** : 2025-10-19
