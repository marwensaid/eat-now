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

---

## 3. Exécution des microservices (en local)

### Lancer `menu-service`

```bash
# Se placer à la racine du projet et exécuter la commande
mvn spring-boot:run -pl menu-service
```

Une fois le service démarré, vous pouvez y accéder via les URLs suivantes :

*   **API (GET all)**: [http://localhost:8081/plats](http://localhost:8081/plats)
*   **Documentation Swagger UI**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)