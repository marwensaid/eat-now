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

*   **API (GET all)**: [http://localhost:8080/commandes](http://localhost:8082/commandes)
*   **Documentation Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8082/swagger-ui.html)


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
docker build -t "eatnow/menu-service:v9" -f menu-service/Dockerfile .

# Créer l'image pour order-service
docker build -t "eatnow/order-service:v9" -f order-service/Dockerfile .

# Créer l'image pour delivery-service
docker build -t "eatnow/delivery-service:v9" -f delivery-service/Dockerfile .
```

*   `-t eatnow/menu-service:latest` : Nomme (`tag`) l'image pour une identification facile.
*   `-f menu-service/Dockerfile` : Spécifie l'emplacement du Dockerfile à utiliser.
*   `.` : Indique que le contexte de build est le répertoire courant (la racine du projet), ce qui est crucial pour que les commandes `COPY` dans les Dockerfiles fonctionnent.

---

## 5. Déploiement sur Kubernetes (option : deploy.sh)

### a. Chargement des images dans le cluster (pour Minikube)

Avant de pouvoir déployer les services, vous devez rendre les images Docker locales accessibles à votre cluster Kubernetes. Si vous utilisez Minikube, la commande suivante charge les images dans le démon Docker de Minikube.

```bash
# Charger chaque image dans le cluster
minikube image load eatnow/menu-service:v9
minikube image load eatnow/order-service:v9
minikube image load eatnow/delivery-service:v9
```

### b. Déployer `menu-service`

```bash
# Appliquer le manifeste de déploiement et de service
kubectl apply -f deployment/menu-service-deployment.yaml
```

#### Vérification

```bash
# Vérifier que le pod est en cours d'exécution (peut prendre quelques instants)
kubectl get pods -l app=menu-service

# Vérifier que le service est créé
kubectl get service menu-service
```

### c. Déployer `order-service`

```bash
# Appliquer le manifeste de déploiement et de service
kubectl apply -f deployment/order-service-deployment.yaml
```

#### Vérification

```bash
# Vérifier que le pod est en cours d'exécution
kubectl get pods -l app=order-service

# Vérifier que le service est créé
kubectl get service order-service
```

### d. Déployer `delivery-service`

```bash
# Appliquer le manifeste de déploiement et de service
kubectl apply -f deployment/delivery-service-deployment.yaml
```

#### Vérification

```bash
# Vérifier que le pod est en cours d'exécution
kubectl get pods -l app=delivery-service

# Vérifier que le service est créé
kubectl get service delivery-service
```

---

## 6. Configuration de la Scalabilité et du Monitoring


```bash
# Appliquer le manifeste pour l''autoscaling
kubectl apply -f deployment/hpa.yaml

# Appliquer le manifeste pour que Prometheus découvre nos services
kubectl apply -f deployment/monitoring.yaml
```

#### Vérification

```bash
# Vérifier que les HPA sont créés (la cible <unknown> au début est normale)
kubectl get hpa

# Vérifier que le ServiceMonitor est créé
kubectl get servicemonitor eatnow-app-monitor
```

---

## 7. Accès à l''application et au Monitoring

### a. Déployer le point d''entrée Ingress

Cette commande crée le point d''entrée unique pour tous les services.

```bash
kubectl apply -f deployment/ingress.yaml
```

### b. Configurer l''accès local

Pour accéder à l''application via le nom d''hôte `eatnow.local`, vous devez mapper l''adresse IP de votre Ingress Controller à ce nom.

1.  **Trouvez l''IP de votre Ingress Controller**. Pour Minikube, utilisez :
    ```bash
    minikube ip
    ```
    Pour d''autres environnements (Docker Desktop, etc.), l''IP peut être `localhost` ou trouvée via `kubectl get ingress`.

2.  **Modifiez votre fichier `hosts`**.
    Ajoutez la ligne suivante à votre fichier `/etc/hosts` (sur Linux/macOS) ou `C:\Windows\System32\drivers\etc\hosts` (sur Windows) :
    ```
    <MINIKUBE_IP> eatnow.local
    ```
    Remplacez `<MINIKUBE_IP>` par l''IP obtenue à l''étape précédente.

### c. Accéder aux services

Les services sont maintenant accessibles via les URLs suivantes :

*   **Menu Service**: [http://eatnow.local/api/menu/plats](http://eatnow.local/api/menu/plats)
*   **Order Service**: [http://eatnow.local/api/orders/commandes](http://eatnow.local/api/orders/commandes)
*   **Delivery Service**: [http://eatnow.local/api/deliveries/livraisons](http://eatnow.local/api/deliveries/livraisons)

### d. Accéder au Dashboard Grafana

1.  **Exposez le service Grafana** en local sur le port 3000. (En supposant que Grafana est déployé dans le namespace `monitoring`).
    ```bash
    kubectl port-forward svc/monitoring-grafana 3000:80
    ```

2.  **Ouvrez Grafana** dans votre navigateur : [http://localhost:3000](http://localhost:3000)
    *   Les identifiants par défaut sont souvent `admin` / `admin`.

3.  **Importez le dashboard**.
    *   Allez dans `Dashboards` -> `New` -> `Import`.
    *   Copiez-collez le contenu du fichier `monitoring/grafana/dashboard.json`.
    *   Sélectionnez votre source de données Prometheus et cliquez sur `Import`.

---
