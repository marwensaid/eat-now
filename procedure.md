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

*   **API (GET all)**: [http://localhost:8081/plats](http://localhost:8081/plats)
*   **Documentation Swagger UI**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

### Lancer `order-service`

**Attention :** `menu-service` doit être démarré avant de lancer `order-service`.

```bash
# Dans un nouveau terminal, à la racine du projet
mvn spring-boot:run -pl order-service
```

*   **API (GET all)**: [http://localhost:8082/commandes](http://localhost:8082/commandes)
*   **Documentation Swagger UI**: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)


### Lancer `delivery-service`

```bash
# Dans un troisième terminal, à la racine du projet
mvn spring-boot:run -pl delivery-service
```

*   **API**: Les endpoints sont accessibles via `http://localhost:8083/livraisons`
*   **Documentation Swagger UI**: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)

---

## 4. Conteneurisation des microservices

Cette étape consiste à créer une image Docker pour chaque microservice. Les commandes suivantes doivent être exécutées depuis la **racine du projet**.

```bash
# Créer l'image pour menu-service
docker build -t eatnow/menu-service:latest -f menu-service/Dockerfile .

# Créer l'image pour order-service
docker build -t eatnow/order-service:latest -f order-service/Dockerfile .

# Créer l'image pour delivery-service
docker build -t eatnow/delivery-service:latest -f delivery-service/Dockerfile .
```

*   `-t eatnow/menu-service:latest` : Nomme (`tag`) l'image pour une identification facile.
*   `-f menu-service/Dockerfile` : Spécifie l'emplacement du Dockerfile à utiliser.
*   `.` : Indique que le contexte de build est le répertoire courant (la racine du projet), ce qui est crucial pour que les commandes `COPY` dans les Dockerfiles fonctionnent.

---
