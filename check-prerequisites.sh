#!/bin/bash

# Script de vérification des prérequis pour EatNow

# Couleurs
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "🔍 EatNow - Vérification des prérequis"
echo "======================================"
echo ""

ALL_OK=true

# Fonction pour vérifier une commande
check_command() {
    if command -v $1 &> /dev/null; then
        VERSION=$($2 2>&1 || echo "version inconnue")
        echo -e "${GREEN}✅ $1 est installé${NC}"
        echo -e "   Version: $VERSION"
        return 0
    else
        echo -e "${RED}❌ $1 n'est pas installé${NC}"
        echo -e "${YELLOW}   $3${NC}"
        ALL_OK=false
        return 1
    fi
}

# Vérifier Docker
echo -e "${BLUE}=== Docker ===${NC}"
check_command "docker" "docker --version" "Installation: https://docs.docker.com/get-docker/"
echo ""

# Vérifier kubectl
echo -e "${BLUE}=== kubectl ===${NC}"
check_command "kubectl" "kubectl version --client --short" "Installation: https://kubernetes.io/docs/tasks/tools/"
echo ""

# Vérifier Minikube
echo -e "${BLUE}=== Minikube ===${NC}"
check_command "minikube" "minikube version" "Installation: https://minikube.sigs.k8s.io/docs/start/"
echo ""

# Vérifier Java
echo -e "${BLUE}=== Java ===${NC}"
check_command "java" "java -version" "Installation: https://www.oracle.com/java/technologies/downloads/"
echo ""

# Vérifier curl
echo -e "${BLUE}=== curl ===${NC}"
check_command "curl" "curl --version | head -n1" "Installation: apt install curl (Linux) ou brew install curl (macOS)"
echo ""

# Vérifier jq (optionnel)
echo -e "${BLUE}=== jq (optionnel) ===${NC}"
if check_command "jq" "jq --version" "Installation: apt install jq (Linux) ou brew install jq (macOS)"; then
    echo -e "${YELLOW}   jq est utile pour formater les réponses JSON${NC}"
fi
echo ""

# Vérifier si Minikube est démarré
echo -e "${BLUE}=== État de Minikube ===${NC}"
if minikube status &> /dev/null; then
    echo -e "${GREEN}✅ Minikube est démarré${NC}"
    MINIKUBE_IP=$(minikube ip 2>/dev/null)
    echo -e "   IP: $MINIKUBE_IP"
    
    # Vérifier les addons
    echo ""
    echo -e "${BLUE}=== Addons Minikube ===${NC}"
    
    if minikube addons list | grep "ingress" | grep -q "enabled"; then
        echo -e "${GREEN}✅ Addon ingress est activé${NC}"
    else
        echo -e "${YELLOW}⚠️  Addon ingress n'est pas activé${NC}"
        echo -e "${YELLOW}   Exécutez: minikube addons enable ingress${NC}"
        ALL_OK=false
    fi
    
    if minikube addons list | grep "metrics-server" | grep -q "enabled"; then
        echo -e "${GREEN}✅ Addon metrics-server est activé${NC}"
    else
        echo -e "${YELLOW}⚠️  Addon metrics-server n'est pas activé${NC}"
        echo -e "${YELLOW}   Exécutez: minikube addons enable metrics-server${NC}"
        ALL_OK=false
    fi
else
    echo -e "${RED}❌ Minikube n'est pas démarré${NC}"
    echo -e "${YELLOW}   Exécutez: minikube start --cpus=4 --memory=8192${NC}"
    ALL_OK=false
fi
echo ""

# Vérifier kubectl context
echo -e "${BLUE}=== Contexte kubectl ===${NC}"
CURRENT_CONTEXT=$(kubectl config current-context 2>/dev/null || echo "")
if [ -n "$CURRENT_CONTEXT" ]; then
    echo -e "${GREEN}✅ kubectl est configuré${NC}"
    echo -e "   Contexte actuel: $CURRENT_CONTEXT"
else
    echo -e "${RED}❌ kubectl n'est pas configuré${NC}"
    ALL_OK=false
fi
echo ""

# Vérifier les ressources système
echo -e "${BLUE}=== Ressources système ===${NC}"
if [ "$(uname)" = "Linux" ]; then
    TOTAL_MEM=$(free -g | awk '/^Mem:/{print $2}')
    echo -e "   Mémoire totale: ${TOTAL_MEM}GB"
    if [ "$TOTAL_MEM" -lt 8 ]; then
        echo -e "${YELLOW}⚠️  Mémoire recommandée: 8GB minimum${NC}"
    else
        echo -e "${GREEN}✅ Mémoire suffisante${NC}"
    fi
elif [ "$(uname)" = "Darwin" ]; then
    TOTAL_MEM=$(sysctl -n hw.memsize | awk '{print int($1/1024/1024/1024)}')
    echo -e "   Mémoire totale: ${TOTAL_MEM}GB"
    if [ "$TOTAL_MEM" -lt 8 ]; then
        echo -e "${YELLOW}⚠️  Mémoire recommandée: 8GB minimum${NC}"
    else
        echo -e "${GREEN}✅ Mémoire suffisante${NC}"
    fi
fi
echo ""

# Résumé
echo "======================================"
if [ "$ALL_OK" = true ]; then
    echo -e "${GREEN}🎉 Tous les prérequis sont satisfaits !${NC}"
    echo ""
    echo "Vous pouvez maintenant déployer l'application :"
    echo -e "${BLUE}  ./build-and-deploy.sh${NC}"
    echo ""
    echo "Ou tester le déploiement existant :"
    echo -e "${BLUE}  ./test-deployment.sh${NC}"
    exit 0
else
    echo -e "${RED}❌ Certains prérequis ne sont pas satisfaits${NC}"
    echo ""
    echo "Veuillez installer/configurer les éléments manquants avant de continuer."
    exit 1
fi
