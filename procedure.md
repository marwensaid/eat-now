# Guide d'exécution pour EatNow

Ce document liste les commandes nécessaires pour valider, compiler et exécuter l'application.

## 1. Validation de la configuration Maven

Cette commande permet de s'assurer que le `pom.xml` parent et ses modules sont correctement déclarés.

```bash
# À exécuter à la racine du projet
mvn validate
```

---

## 2. Compilation de tous les microservices

Cette commande compile l'ensemble des microservices (`menu-service`, `order-service`, `delivery-service`), exécute les tests et installe les artefacts JAR dans votre dépôt Maven local.

```bash
# À exécuter à la racine du projet
mvn clean install
```