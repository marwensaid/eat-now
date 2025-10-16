#!/bin/bash
set -e

# 1. Build all JARs
for service in menu-service delivery-service order-service; do
  echo "Building $service..."
  (cd $service && ./gradlew clean build)
done

# 2. Vérifie la présence des JARs
for service in menu-service delivery-service order-service; do
  JAR=$(ls $service/build/libs/*.jar 2>/dev/null | head -n1)
  if [[ ! -f "$JAR" ]]; then
    echo "❌ JAR introuvable pour $service !"
    exit 1
  else
    echo "✅ JAR trouvé pour $service : $JAR"
  fi
done

# 3. Utilise le Docker de Minikube
eval $(minikube docker-env)

# 4. Build Docker images
for service in menu-service delivery-service order-service; do
  echo "Building Docker image for $service..."
  docker build -t janovp/$service:latest ./$service
done

# 5. Redéploie les services
for service in menu-service delivery-service order-service; do
  echo "Restarting deployment for $service..."
  kubectl rollout restart deployment $service || true
done

echo "🎉 Build, vérification et déploiement terminés !"