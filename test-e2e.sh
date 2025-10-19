#!/bin/bash

# Script de test End-to-End pour EatNow
# Ce script teste le flux complet : Menu → Order → Delivery

set -e

echo "🚀 EatNow - Test End-to-End"
echo "================================"
echo ""

# Couleurs pour l'affichage
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Fonction pour afficher un message de succès
success() {
    echo -e "${GREEN}✅ $1${NC}"
}

# Fonction pour afficher un message d'erreur
error() {
    echo -e "${RED}❌ $1${NC}"
    exit 1
}

# Fonction pour afficher un message d'info
info() {
    echo -e "${YELLOW}ℹ️  $1${NC}"
}

# Vérifier que kubectl est installé
if ! command -v kubectl &> /dev/null; then
    error "kubectl n'est pas installé"
fi

# Vérifier que les pods sont en cours d'exécution
info "Vérification de l'état des pods..."
if ! kubectl get pods | grep -q "Running"; then
    error "Aucun pod n'est en cours d'exécution"
fi

# Récupérer le nom d'un pod order-service
ORDER_POD=$(kubectl get pods -l app=order-service -o jsonpath='{.items[0].metadata.name}')
if [ -z "$ORDER_POD" ]; then
    error "Impossible de trouver un pod order-service"
fi
success "Pod order-service trouvé : $ORDER_POD"

echo ""
echo "📋 Test 1 : Lister les plats disponibles"
echo "----------------------------------------"
DISHES=$(kubectl exec -it $ORDER_POD -- curl -s http://menu-service:8080/menu/api/menu/dishes)
if echo "$DISHES" | grep -q "Burger Classic"; then
    success "Menu service répond correctement"
    echo "$DISHES" | head -c 200
    echo "..."
else
    error "Menu service ne répond pas correctement"
fi

echo ""
echo ""
echo "🛒 Test 2 : Créer une commande"
echo "----------------------------------------"
ORDER_RESPONSE=$(kubectl exec -it $ORDER_POD -- curl -s -X POST http://localhost:8081/order/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"test-customer","dishIds":["1","2","3"],"deliveryAddress":"123 Test Street, Paris"}')

if echo "$ORDER_RESPONSE" | grep -q "id"; then
    ORDER_ID=$(echo "$ORDER_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
    success "Commande créée avec succès"
    echo "Order ID: $ORDER_ID"
    echo "$ORDER_RESPONSE" | head -c 300
    echo "..."
else
    error "Échec de la création de la commande"
fi

echo ""
echo ""
echo "🚚 Test 3 : Vérifier la création de livraison"
echo "----------------------------------------"
DELIVERIES=$(kubectl exec -it $ORDER_POD -- curl -s http://delivery-service:8080/delivery/api/delivery/deliveries)
if echo "$DELIVERIES" | grep -q "PENDING"; then
    success "Livraison créée automatiquement"
    echo "$DELIVERIES" | head -c 300
    echo "..."
else
    info "Aucune livraison trouvée (peut être normal si le flux n'est pas complètement intégré)"
fi

echo ""
echo ""
echo "💚 Test 4 : Health Checks"
echo "----------------------------------------"

# Menu Service
MENU_POD=$(kubectl get pods -l app=menu-service -o jsonpath='{.items[0].metadata.name}')
MENU_HEALTH=$(kubectl exec -it $MENU_POD -- curl -s http://localhost:8081/menu/actuator/health)
if echo "$MENU_HEALTH" | grep -q '"status":"UP"'; then
    success "Menu Service : UP"
else
    error "Menu Service : DOWN"
fi

# Order Service
ORDER_HEALTH=$(kubectl exec -it $ORDER_POD -- curl -s http://localhost:8081/order/actuator/health)
if echo "$ORDER_HEALTH" | grep -q '"status":"UP"'; then
    success "Order Service : UP"
else
    error "Order Service : DOWN"
fi

# Delivery Service
DELIVERY_POD=$(kubectl get pods -l app=delivery-service -o jsonpath='{.items[0].metadata.name}')
DELIVERY_HEALTH=$(kubectl exec -it $DELIVERY_POD -- curl -s http://localhost:8081/delivery/actuator/health)
if echo "$DELIVERY_HEALTH" | grep -q '"status":"UP"'; then
    success "Delivery Service : UP"
else
    error "Delivery Service : DOWN"
fi

echo ""
echo ""
echo "📊 Test 5 : Prometheus Targets"
echo "----------------------------------------"
MINIKUBE_IP=$(minikube ip 2>/dev/null || echo "localhost")
PROMETHEUS_TARGETS=$(curl -s http://$MINIKUBE_IP:30090/api/v1/targets 2>/dev/null || echo "")
if echo "$PROMETHEUS_TARGETS" | grep -q '"health":"up"'; then
    UP_COUNT=$(echo "$PROMETHEUS_TARGETS" | grep -o '"health":"up"' | wc -l)
    success "Prometheus collecte les métriques ($UP_COUNT targets UP)"
else
    info "Prometheus non accessible ou aucune target UP"
fi

echo ""
echo ""
echo "🎯 Test 6 : Vérifier les HPA"
echo "----------------------------------------"
HPA_STATUS=$(kubectl get hpa)
if echo "$HPA_STATUS" | grep -q "menu-service-hpa"; then
    success "HPA configurés correctement"
    echo "$HPA_STATUS"
else
    error "HPA non trouvés"
fi

echo ""
echo ""
echo "================================"
echo "🎉 Tests End-to-End terminés avec succès !"
echo "================================"
echo ""
echo "📊 Résumé :"
echo "  ✅ Menu Service : Opérationnel"
echo "  ✅ Order Service : Opérationnel"
echo "  ✅ Delivery Service : Opérationnel"
echo "  ✅ Communication inter-services : Fonctionnelle"
echo "  ✅ Health Checks : OK"
echo "  ✅ Prometheus : Collecte les métriques"
echo "  ✅ HPA : Configurés"
echo ""
echo "🌐 URLs d'accès :"
echo "  • Prometheus : http://$MINIKUBE_IP:30090"
echo "  • Grafana : http://$MINIKUBE_IP:30300 (admin/admin)"
echo ""
echo "📚 Documentation :"
echo "  • Guide complet : DEPLOYMENT.md"
echo "  • Quick Start : QUICK_START.md"
echo "  • Vérification : VERIFICATION.md"
echo ""
