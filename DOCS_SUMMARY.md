# 📚 Documentation EatNow - Index

## 📋 Fichiers de Documentation

### 🏠 README.md (12 KB)
**Point d'entrée principal du projet**
- Vue d'ensemble de l'architecture
- Statut du déploiement actuel
- Liens vers les autres documentations
- Badges et informations générales

### 🚀 QUICK_START.md (2.1 KB)
**Guide de démarrage rapide en 5 étapes**
- Déploiement en 5 minutes
- Commandes essentielles
- URLs importantes
- Tests rapides

### 📖 DEPLOYMENT.md (18 KB)
**Guide complet de déploiement**
- Architecture détaillée
- Instructions de build
- Configuration Kubernetes
- Monitoring (Prometheus/Grafana)
- Résilience (Circuit Breaker, Retry, Timeout)
- Troubleshooting complet
- Problèmes résolus avec solutions

### ✅ VERIFICATION.md (12 KB)
**Guide de vérification et tests**
- Statut actuel du déploiement
- Problèmes résolus
- Commandes de vérification infrastructure
- Tests de communication inter-services
- Vérification du monitoring
- Tests de résilience
- Tests d'auto-scaling
- Checklist complète

### 📝 TODO.md (8.4 KB)
**Liste des tâches et suivi**
- Tâches complétées
- Tâches en cours
- Tâches futures
- Priorités

## 🎯 Quelle Documentation Utiliser ?

| Besoin | Fichier à consulter |
|--------|---------------------|
| **Découvrir le projet** | README.md |
| **Déployer rapidement** | QUICK_START.md |
| **Déploiement complet** | DEPLOYMENT.md |
| **Vérifier le système** | VERIFICATION.md |
| **Voir les tâches** | TODO.md |

## 🛠️ Scripts Disponibles

| Script | Description |
|--------|-------------|
| `build-and-deploy.sh` | Build et déploiement automatique |
| `test-e2e.sh` | Tests end-to-end automatiques |
| `test-deployment.sh` | Tests de déploiement |
| `check-prerequisites.sh` | Vérification des prérequis |

## 🌐 URLs Rapides

```bash
# Prometheus
echo "http://$(minikube ip):30090"

# Grafana (admin/admin)
echo "http://$(minikube ip):30300"
```

---

**Dernière mise à jour** : 2025-10-19
