# ⚡ Quick Start Guide - EatNow

## 🚀 Déploiement en 5 minutes

### Prérequis
- Minikube installé et démarré
- kubectl configuré
- Docker installé

### Étape 1 : Démarrer Minikube

```bash
minikube start --cpus=4 --memory=8192
minikube addons enable ingress
minikube addons enable metrics-server
```

### Étape 2 : Déployer l'application

```bash
# Cloner le projet (si pas déjà fait)
cd eat-now

# Déployer avec le script automatique
chmod +x build-and-deploy.sh
./build-and-deploy.sh
```

### Étape 3 : Configurer l'accès

```bash
# Ajouter eatnow.local à /etc/hosts
echo "$(minikube ip) eatnow.local" | sudo tee -a /etc/hosts
```

### Étape 4 : Tester

```bash
# Lister les plats
curl http://eatnow.local/menu/api/menu/dishes

# Créer une commande
curl -X POST http://eatnow.local/order/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "dishIds": ["1", "2"],
    "totalAmount": 25.50,
    "deliveryAddress": "123 Rue de Paris",
    "customerName": "Jean Dupont"
  }'

# Vérifier la livraison créée
curl http://eatnow.local/delivery/api/delivery/deliveries
```

### Étape 5 : Accéder au monitoring

```bash
# Prometheus
echo "Prometheus: http://$(minikube ip):30090"

# Grafana (admin/admin)
echo "Grafana: http://$(minikube ip):30300"
```

## 🎯 URLs importantes

| Service | URL |
|---------|-----|
| Menu Swagger | http://eatnow.local/menu/swagger-ui.html |
| Order Swagger | http://eatnow.local/order/swagger-ui.html |
| Delivery Swagger | http://eatnow.local/delivery/swagger-ui.html |
| Prometheus | http://$(minikube ip):30090 |
| Grafana | http://$(minikube ip):30300 |

## 🔧 Commandes utiles

```bash
# Voir les pods
kubectl get pods

# Voir les logs d'un service
kubectl logs -f -l app=order-service

# Redémarrer un service
kubectl rollout restart deployment order-service

# Scaler un service
kubectl scale deployment menu-service --replicas=3

# Nettoyer tout
kubectl delete -k k8s/
```

## 📖 Documentation complète

Pour plus de détails, consultez [DEPLOYMENT.md](DEPLOYMENT.md)
