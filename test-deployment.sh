#!/bin/bash

# Script de test automatique pour EatNow
# Ce script vérifie que tous les composants fonctionnent correctement

set -e

# Couleurs
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "🧪 EatNow - Script de test automatique"
echo "======================================"
echo ""

# Fonction pour afficher les résultats
print_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✅ $2${NC}"
    else
        echo -e "${RED}❌ $2${NC}"
        return 1
    fi
}

# Compteur de tests
TOTAL_TESTS=0
PASSED_TESTS=0

# Test 1: Vérifier que Kubernetes est accessible
echo -e "${BLUE}=== Test 1: Vérification de Kubernetes ===${NC}"
TOTAL_TESTS=$((TOTAL_TESTS + 1))
if kubectl cluster-info &> /dev/null; then
    print_result 0 "Kubernetes est accessible"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    print_result 1 "Kubernetes n'est pas accessible"
fi
echo ""

# Test 2: Vérifier que tous les pods sont en cours d'exécution
echo -e "${BLUE}=== Test 2: Vérification des pods ===${NC}"
for service in menu-service order-service delivery-service prometheus grafana; do
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    POD_STATUS=$(kubectl get pods -l app=$service -o jsonpath='{.items[0].status.phase}' 2>/dev/null)
    if [ "$POD_STATUS" = "Running" ]; then
        print_result 0 "Pod $service est Running"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        print_result 1 "Pod $service n'est pas Running (status: $POD_STATUS)"
    fi
done
echo ""

# Test 3: Vérifier que tous les services existent
echo -e "${BLUE}=== Test 3: Vérification des services K8s ===${NC}"
for service in menu-service order-service delivery-service prometheus grafana; do
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if kubectl get svc $service &> /dev/null; then
        print_result 0 "Service $service existe"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        print_result 1 "Service $service n'existe pas"
    fi
done
echo ""

# Test 4: Vérifier que l'ingress existe
echo -e "${BLUE}=== Test 4: Vérification de l'Ingress ===${NC}"
TOTAL_TESTS=$((TOTAL_TESTS + 1))
if kubectl get ingress &> /dev/null; then
    print_result 0 "Ingress existe"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    print_result 1 "Ingress n'existe pas"
fi
echo ""

# Test 5: Vérifier que les HPA existent
echo -e "${BLUE}=== Test 5: Vérification des HPA ===${NC}"
for service in menu-service order-service delivery-service; do
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if kubectl get hpa $service &> /dev/null; then
        print_result 0 "HPA $service existe"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        print_result 1 "HPA $service n'existe pas"
    fi
done
echo ""

# Test 6: Vérifier la configuration de /etc/hosts
echo -e "${BLUE}=== Test 6: Vérification de /etc/hosts ===${NC}"
TOTAL_TESTS=$((TOTAL_TESTS + 1))
if grep -q "eatnow.local" /etc/hosts; then
    print_result 0 "eatnow.local est configuré dans /etc/hosts"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    print_result 1 "eatnow.local n'est pas configuré dans /etc/hosts"
    echo -e "${YELLOW}💡 Exécutez: echo \"\$(minikube ip) eatnow.local\" | sudo tee -a /etc/hosts${NC}"
fi
echo ""

# Test 7: Tester les health checks
echo -e "${BLUE}=== Test 7: Health checks des services ===${NC}"
for service in menu order delivery; do
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://eatnow.local/$service/actuator/health 2>/dev/null || echo "000")
    if [ "$HTTP_CODE" = "200" ]; then
        print_result 0 "$service-service health check OK (HTTP $HTTP_CODE)"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        print_result 1 "$service-service health check FAILED (HTTP $HTTP_CODE)"
    fi
done
echo ""

# Test 8: Tester l'API Menu Service
echo -e "${BLUE}=== Test 8: API Menu Service ===${NC}"
TOTAL_TESTS=$((TOTAL_TESTS + 1))
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://eatnow.local/menu/api/menu/dishes 2>/dev/null || echo "000")
if [ "$HTTP_CODE" = "200" ]; then
    print_result 0 "GET /menu/api/menu/dishes OK (HTTP $HTTP_CODE)"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    print_result 1 "GET /menu/api/menu/dishes FAILED (HTTP $HTTP_CODE)"
fi
echo ""

# Test 9: Tester l'API Order Service
echo -e "${BLUE}=== Test 9: API Order Service ===${NC}"
TOTAL_TESTS=$((TOTAL_TESTS + 1))
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://eatnow.local/order/api/orders 2>/dev/null || echo "000")
if [ "$HTTP_CODE" = "200" ]; then
    print_result 0 "GET /order/api/orders OK (HTTP $HTTP_CODE)"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    print_result 1 "GET /order/api/orders FAILED (HTTP $HTTP_CODE)"
fi
echo ""

# Test 10: Tester l'API Delivery Service
echo -e "${BLUE}=== Test 10: API Delivery Service ===${NC}"
TOTAL_TESTS=$((TOTAL_TESTS + 1))
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://eatnow.local/delivery/api/deliveries 2>/dev/null || echo "000")
if [ "$HTTP_CODE" = "200" ]; then
    print_result 0 "GET /delivery/api/deliveries OK (HTTP $HTTP_CODE)"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    print_result 1 "GET /delivery/api/deliveries FAILED (HTTP $HTTP_CODE)"
fi
echo ""

# Test 11: Tester le flux complet (création de commande)
echo -e "${BLUE}=== Test 11: Flux complet (création de commande) ===${NC}"
TOTAL_TESTS=$((TOTAL_TESTS + 1))
ORDER_RESPONSE=$(curl -s -X POST http://eatnow.local/order/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "test-user",
    "dishIds": ["1", "2"],
    "totalAmount": 25.50,
    "deliveryAddress": "123 Test Street",
    "customerName": "Test User"
  }' 2>/dev/null)

if echo "$ORDER_RESPONSE" | grep -q "id"; then
    print_result 0 "Création de commande OK"
    PASSED_TESTS=$((PASSED_TESTS + 1))
    
    # Extraire l'ID de la commande
    ORDER_ID=$(echo "$ORDER_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
    DELIVERY_ID=$(echo "$ORDER_RESPONSE" | grep -o '"deliveryId":[0-9]*' | cut -d':' -f2)
    
    if [ -n "$DELIVERY_ID" ]; then
        echo -e "${GREEN}  ↳ Livraison créée automatiquement (ID: $DELIVERY_ID)${NC}"
    fi
else
    print_result 1 "Création de commande FAILED"
fi
echo ""

# Test 12: Vérifier Prometheus
echo -e "${BLUE}=== Test 12: Prometheus ===${NC}"
TOTAL_TESTS=$((TOTAL_TESTS + 1))
MINIKUBE_IP=$(minikube ip 2>/dev/null || echo "")
if [ -n "$MINIKUBE_IP" ]; then
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://$MINIKUBE_IP:30090/-/healthy 2>/dev/null || echo "000")
    if [ "$HTTP_CODE" = "200" ]; then
        print_result 0 "Prometheus est accessible (HTTP $HTTP_CODE)"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        print_result 1 "Prometheus n'est pas accessible (HTTP $HTTP_CODE)"
    fi
else
    print_result 1 "Impossible de récupérer l'IP de Minikube"
fi
echo ""

# Test 13: Vérifier Grafana
echo -e "${BLUE}=== Test 13: Grafana ===${NC}"
TOTAL_TESTS=$((TOTAL_TESTS + 1))
if [ -n "$MINIKUBE_IP" ]; then
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://$MINIKUBE_IP:30300/api/health 2>/dev/null || echo "000")
    if [ "$HTTP_CODE" = "200" ]; then
        print_result 0 "Grafana est accessible (HTTP $HTTP_CODE)"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        print_result 1 "Grafana n'est pas accessible (HTTP $HTTP_CODE)"
    fi
fi
echo ""

# Test 14: Vérifier les métriques Prometheus
echo -e "${BLUE}=== Test 14: Métriques Prometheus ===${NC}"
for service in menu order delivery; do
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://eatnow.local/$service/actuator/prometheus 2>/dev/null || echo "000")
    if [ "$HTTP_CODE" = "200" ]; then
        print_result 0 "$service-service expose les métriques Prometheus"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        print_result 1 "$service-service n'expose pas les métriques Prometheus (HTTP $HTTP_CODE)"
    fi
done
echo ""

# Test 15: Vérifier Swagger
echo -e "${BLUE}=== Test 15: Documentation Swagger ===${NC}"
for service in menu order delivery; do
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://eatnow.local/$service/swagger-ui.html 2>/dev/null || echo "000")
    if [ "$HTTP_CODE" = "200" ]; then
        print_result 0 "$service-service Swagger accessible"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        print_result 1 "$service-service Swagger non accessible (HTTP $HTTP_CODE)"
    fi
done
echo ""

# Résumé
echo "======================================"
echo -e "${BLUE}📊 Résumé des tests${NC}"
echo "======================================"
echo -e "Total de tests: $TOTAL_TESTS"
echo -e "${GREEN}Tests réussis: $PASSED_TESTS${NC}"
echo -e "${RED}Tests échoués: $((TOTAL_TESTS - PASSED_TESTS))${NC}"
echo ""

PERCENTAGE=$((PASSED_TESTS * 100 / TOTAL_TESTS))
echo -e "Taux de réussite: ${PERCENTAGE}%"
echo ""

if [ $PASSED_TESTS -eq $TOTAL_TESTS ]; then
    echo -e "${GREEN}🎉 Tous les tests sont passés avec succès !${NC}"
    echo ""
    echo "📝 URLs d'accès :"
    echo "  • Menu Service:     http://eatnow.local/menu/swagger-ui.html"
    echo "  • Order Service:    http://eatnow.local/order/swagger-ui.html"
    echo "  • Delivery Service: http://eatnow.local/delivery/swagger-ui.html"
    if [ -n "$MINIKUBE_IP" ]; then
        echo "  • Prometheus:       http://$MINIKUBE_IP:30090"
        echo "  • Grafana:          http://$MINIKUBE_IP:30300 (admin/admin)"
    fi
    exit 0
else
    echo -e "${YELLOW}⚠️  Certains tests ont échoué. Vérifiez les logs ci-dessus.${NC}"
    echo ""
    echo "🔍 Commandes de diagnostic :"
    echo "  kubectl get pods"
    echo "  kubectl get svc"
    echo "  kubectl logs -l app=order-service"
    exit 1
fi
