# EatNow - Déploiement Kubernetes

### Prérequis : Images Docker Hub

Les images sont déjà construites et poussées sur Docker Hub sous le compte `janovp` :

- `janovp/menu-service:latest`
- `janovp/delivery-service:latest`

Si vous voulez les reconstruire :

```bash
# Se connecter à Docker Hub
docker login

# Builder les images
docker build -t janovp/menu-service:latest menu-service/
docker build -t janovp/delivery-service:latest delivery-service/

# Pousser sur Docker Hub
docker push janovp/menu-service:latest
docker push janovp/delivery-service:latest
```

## Déploiement

### Option 1: Avec Kustomize (recommandé)
```bash
cd k8s
kubectl apply -k .
```

### Option 2: Application individuelle
```bash
cd k8s
kubectl apply -f menu-service-deployment.yaml
kubectl apply -f menu-service-service.yaml
kubectl apply -f menu-service-hpa.yaml
kubectl apply -f delivery-service-deployment.yaml
kubectl apply -f delivery-service-service.yaml
kubectl apply -f delivery-service-hpa.yaml
kubectl apply -f ingress.yaml
```

### Prérequis : NGINX Ingress Controller
```bash
# Activer l'Ingress Controller dans Minikube
minikube addons enable ingress

# Vérifier qu'il fonctionne
kubectl get pods -n ingress-nginx
```

## Accès aux services

### Via Ingress (point d'entrée unifié - 1 seule adresse/port)

**Adresse unique :** `http://192.168.39.87`

**URLs d'accès :**
- **Menu Service :** http://192.168.39.87/menu
- **Delivery Service :** http://192.168.39.87/delivery
- **Swagger Menu :** http://192.168.39.87/menu/swagger-ui/index.html
- **Swagger Delivery :** http://192.168.39.87/delivery/swagger-ui/index.html

### Configuration /etc/hosts (optionnel)
```bash
# Ajouter dans /etc/hosts pour un nom plus joli
192.168.39.87 eatnow.local
```

Puis utiliser :
- http://eatnow.local/menu
- http://eatnow.local/delivery

## Vérification du déploiement

### Status des pods
```bash
kubectl get pods
kubectl get deployments
kubectl get hpa
```

### Logs
```bash
kubectl logs -l app=menu-service
kubectl logs -l app=delivery-service
```

### Health checks
```bash
kubectl exec -it <menu-pod-name> -- curl http://localhost:8081/actuator/health
kubectl exec -it <delivery-pod-name> -- curl http://localhost:8082/actuator/health
```

## API Documentation

- Menu Service Swagger: http://192.168.39.87/menu/swagger-ui/index.html
- Delivery Service Swagger: http://192.168.39.87/delivery/swagger-ui/index.html

## Monitoring

Les métriques Prometheus sont disponibles sur :
- Menu Service: http://192.168.39.87/menu/actuator/prometheus
- Delivery Service: http://192.168.39.87/delivery/actuator/prometheus

## Nettoyage

```bash
cd k8s
kubectl delete -k .
```

Ou :
```bash
kubectl delete deployments,services,hpa,ingress --selector app in (menu-service,delivery-service)
```
