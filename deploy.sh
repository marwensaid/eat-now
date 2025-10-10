#!/bin/bash

# Ce script automatise le build et le déploiement des microservices.
# Il arrête le processus si une seule commande échoue.
set -e

# --- Configuration ---
# Changez cette version à chaque nouveau déploiement si vous le souhaitez.
export VERSION="v11"

echo "--- Début du déploiement de la version $VERSION ---"

# --- Étape 1: Recompiler le projet ---
echo "\n[1/5] Recompilation des projets Maven..."
mvn clean install

# --- Étape 2: Build des images Docker ---
echo "\n[2/5] Build des images Docker (sans cache)..."
docker build -t "eatnow/menu-service:$VERSION" -f menu-service/Dockerfile .
docker build -t "eatnow/order-service:$VERSION" -f order-service/Dockerfile .
docker build -t "eatnow/delivery-service:$VERSION" -f delivery-service/Dockerfile .

# --- Étape 3: Mise à jour des fichiers de déploiement ---
echo "\n[3/5] Mise à jour des fichiers de déploiement Kubernetes..."
sed -i "s|image: eatnow/menu-service:.*|image: eatnow/menu-service:$VERSION|g" deployment/menu-service-deployment.yaml
sed -i "s|image: eatnow/order-service:.*|image: eatnow/order-service:$VERSION|g" deployment/order-service-deployment.yaml
sed -i "s|image: eatnow/delivery-service:.*|image: eatnow/delivery-service:$VERSION|g" deployment/delivery-service-deployment.yaml

# --- Étape 4: Chargement des images dans Minikube ---
echo "\n[4/5] Chargement des images dans le cluster Minikube..."
minikube image load "eatnow/menu-service:$VERSION"
minikube image load "eatnow/order-service:$VERSION"
minikube image load "eatnow/delivery-service:$VERSION"

# --- Étape 5: Application des déploiements ---
echo "\n[5/5] Application des manifestes sur Kubernetes..."
kubectl apply -f deployment/menu-service-deployment.yaml
kubectl apply -f deployment/order-service-deployment.yaml
kubectl apply -f deployment/delivery-service-deployment.yaml

echo "\n--- Déploiement de la version $VERSION terminé avec succès ! ---"
echo "--- Vérification du statut des pods... ---"
sleep 5
kubectl get pods
