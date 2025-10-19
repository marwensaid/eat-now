#!/bin/bash
set -e

echo "🚀 EatNow - Build and Deploy Script"
echo "===================================="

# Couleurs pour les messages
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Mode de déploiement : local (minikube) ou remote (docker hub)
DEPLOY_MODE=${1:-local}

echo -e "${YELLOW}Mode de déploiement: $DEPLOY_MODE${NC}"

# 1. Build all JARs
echo ""
echo "📦 Étape 1/5 : Build des JARs avec Gradle"
echo "=========================================="
for service in menu-service order-service delivery-service; do
  echo -e "${YELLOW}Building $service...${NC}"
  (cd $service && ./gradlew clean build -x test)
  echo -e "${GREEN}✅ $service built successfully${NC}"
done

# 2. Vérifie la présence des JARs
echo ""
echo "🔍 Étape 2/5 : Vérification des JARs"
echo "===================================="
for service in menu-service order-service delivery-service; do
  JAR=$(ls $service/build/libs/*.jar 2>/dev/null | head -n1)
  if [[ ! -f "$JAR" ]]; then
    echo -e "${RED}❌ JAR introuvable pour $service !${NC}"
    exit 1
  else
    echo -e "${GREEN}✅ JAR trouvé pour $service : $JAR${NC}"
  fi
done

# 3. Configuration Docker selon le mode
echo ""
echo "🐳 Étape 3/5 : Build des images Docker"
echo "======================================"
if [ "$DEPLOY_MODE" = "local" ]; then
  echo -e "${YELLOW}Configuration Docker pour Minikube...${NC}"
  eval $(minikube docker-env)
else
  echo -e "${YELLOW}Configuration Docker pour Docker Hub...${NC}"
fi

# 4. Build Docker images
for service in menu-service order-service delivery-service; do
  echo -e "${YELLOW}Building Docker image for $service...${NC}"
  docker build -t janovp/$service:latest ./$service
  echo -e "${GREEN}✅ Image janovp/$service:latest built${NC}"
  
  # Push to Docker Hub si mode remote
  if [ "$DEPLOY_MODE" = "remote" ]; then
    echo -e "${YELLOW}Pushing janovp/$service:latest to Docker Hub...${NC}"
    docker push janovp/$service:latest
    echo -e "${GREEN}✅ Image pushed to Docker Hub${NC}"
  fi
done

# 5. Déploiement Kubernetes
echo ""
echo "☸️  Étape 4/5 : Déploiement Kubernetes"
echo "======================================"

# Vérifier si les déploiements existent déjà
if kubectl get deployment menu-service &> /dev/null; then
  echo -e "${YELLOW}Déploiements existants détectés. Redémarrage...${NC}"
  for service in menu-service order-service delivery-service; do
    echo -e "${YELLOW}Restarting deployment for $service...${NC}"
    kubectl rollout restart deployment $service
    echo -e "${GREEN}✅ $service restarted${NC}"
  done
else
  echo -e "${YELLOW}Première installation. Déploiement complet...${NC}"
  kubectl apply -k k8s/
  echo -e "${GREEN}✅ Tous les services déployés${NC}"
fi

# 6. Vérification du déploiement
echo ""
echo "✅ Étape 5/5 : Vérification du déploiement"
echo "=========================================="

echo -e "${YELLOW}Attente du démarrage des pods...${NC}"
kubectl wait --for=condition=ready pod -l app=menu-service --timeout=120s || true
kubectl wait --for=condition=ready pod -l app=order-service --timeout=120s || true
kubectl wait --for=condition=ready pod -l app=delivery-service --timeout=120s || true
kubectl wait --for=condition=ready pod -l app=prometheus --timeout=120s || true
kubectl wait --for=condition=ready pod -l app=grafana --timeout=120s || true

echo ""
echo -e "${GREEN}📊 État des pods :${NC}"
kubectl get pods

echo ""
echo -e "${GREEN}🌐 Services exposés :${NC}"
kubectl get svc

echo ""
echo -e "${GREEN}📈 HPA configurés :${NC}"
kubectl get hpa

echo ""
echo "🎉 Build et déploiement terminés avec succès !"
echo ""
echo "📝 Accès aux services :"
echo "======================"
if command -v minikube &> /dev/null; then
  MINIKUBE_IP=$(minikube ip)
  echo "  • Menu Service:     http://eatnow.local/menu/swagger-ui.html"
  echo "  • Order Service:    http://eatnow.local/order/swagger-ui.html"
  echo "  • Delivery Service: http://eatnow.local/delivery/swagger-ui.html"
  echo "  • Prometheus:       http://$MINIKUBE_IP:30090"
  echo "  • Grafana:          http://$MINIKUBE_IP:30300 (admin/admin)"
  echo ""
  echo "💡 N'oubliez pas d'ajouter à /etc/hosts :"
  echo "   echo \"$MINIKUBE_IP eatnow.local\" | sudo tee -a /etc/hosts"
fi
echo ""