🎓 Examen Pratique – Architecture Microservices & Cloud-Native (4h)

Sujet : Application de restauration « EatNow »
Technos : Java 17 – Spring Boot 3 – Kubernetes – Grafana – Prometheus

Manal
Mehdi

⸻

🎯 Objectif général

Vous devez concevoir, développer et déployer une application simplifiée de restauration (type Uber Eats) composée de 3 microservices Spring Boot.

Chaque microservice sera :
	•	autonome (pas de base de données externe),
	•	déployé sur Kubernetes,
	•	instrumenté pour la supervision (Prometheus + Grafana),
	•	résilient (circuit breaker, retry, timeout),
	•	documenté (Swagger / OpenAPI),
	•	scalable (HPA ou paramétrage du déploiement).

Vous avez 4 heures pour :
	1.	Développer les 3 microservices,
	2.	Les dockeriser et les déployer sur Kubernetes (avec Helm ou manifests YAML),
	3.	Mettre en place le monitoring,
	4.	Réaliser un schéma d’architecture global (Draw.io),
	5.	Préparer un README clair pour le déploiement et le test.

⸻

🧩 1. Contexte fonctionnel

L’application EatNow permet à un utilisateur :
	•	de consulter des plats disponibles,
	•	de passer une commande,
	•	de suivre la livraison de sa commande.

L’ensemble repose sur 3 microservices qui communiquent entre eux :

|   **Microservice**   |   **Rôle principal**   |   **Exemples de fonctionnalités **   |
| --- | --- | --- |
|   **menu-service**   |   Gestion du catalogue des plats   |   \- Lister tous les plats- Consulter le détail d’un plat- Ajouter un plat- Modifier ou supprimer un plat   |
|   **order-service**   |   Gestion des commandes clients   |   \- Créer une commande (références plats)- Consulter une commande- Lister les commandes d’un utilisateur- Mettre à jour le statut (CREATED → DELIVERED)   |
|   **delivery-service**   |   Gestion des livraisons   |   \- Créer une livraison associée à une commande- Assigner un livreur- Modifier le statut de livraison- Consulter la livraison   |

Les données peuvent être stockées en mémoire (List ou Map).
Aucune base de données externe n’est requise.

⸻

⚙️ 2. Exigences techniques

🧱 Développement
	•	Langage : Java 17
	•	Framework : Spring Boot 3
	•	Gestion de dépendances : Maven ou Gradle
	•	Communication inter-services : appels HTTP REST (WebClient ou RestTemplate)
	•	Résilience : Resilience4j (Circuit Breaker, Retry, Timeout)
	•	Observabilité : Micrometer + Actuator (endpoint /actuator/prometheus)
	•	Documentation : Swagger/OpenAPI via springdoc-openapi
	•	Stockage : collections en mémoire (aucune DB externe)

⸻

☸️ Déploiement Kubernetes

Chaque microservice doit :
	•	être containerisé avec Docker,
	•	avoir un Deployment et un Service exposé (type NodePort ou LoadBalancer),
	•	comporter des probes (liveness et readiness),
	•	inclure un HorizontalPodAutoscaler (HPA) basé sur l’utilisation CPU,
	•	être accessible via un point d’entrée unique (Ingress ou NodePort).

Optionnel : utilisation d’un Helm chart pour le déploiement global.

⸻

📈 Observabilité & Monitoring

Vous devez déployer :
	•	Prometheus pour la collecte des métriques des 3 microservices,
	•	Grafana pour la visualisation.

⚠️ Le code et les manifests de Prometheus & Grafana vous sont fournis dans le sujet (ou disponibles sur le dépôt de cours).
Votre tâche est de :
	•	configurer les ServiceMonitor ou les cibles dans Prometheus,
	•	créer un dashboard Grafana simple affichant :
	•	le nombre de requêtes HTTP reçues par service,
	•	le taux d’erreurs,
	•	le temps de réponse moyen.

⸻

🧩 Résilience

Implémentez dans vos appels inter-services :
	•	un circuit breaker,
	•	un retry (avec 2 ou 3 tentatives),
	•	un timeout (2s max).

Prévoyez un fallback (ex. message d’erreur simple si le service appelé est indisponible).

⸻

📜 Documentation

Chaque microservice doit exposer son interface via Swagger :
	•	accessible sur /swagger-ui.html ou /api-docs.

⸻

🧠 3. Partie théorique

Vous devez dessiner l’architecture complète de l’application sur Draw.io (ou équivalent).

Votre schéma doit montrer :
	•	les 3 microservices avec leurs interactions (REST),
	•	les flux de communication (internes / externes),
	•	les composants de monitoring (Prometheus, Grafana),
	•	les mécanismes de résilience,
	•	la scalabilité (HPA),
	•	les points d’exposition (Ingress, NodePort, etc.).

🧩 Format attendu :
	•	Fichier .drawio ou export .png dans votre dépôt,
	•	Schéma lisible et cohérent avec votre implémentation.

⸻

📦 4. Livrables attendus

À la fin des 4 heures, vous devez avoir :
	1.	3 dossiers de microservices : menu-service, order-service, delivery-service
	2.	Un Dockerfile par service
	3.	Les manifests Kubernetes (ou un chart Helm)
	4.	Le fichier d’architecture Draw.io
	5.	Le README.md avec :
	•	instructions de build et déploiement,
	•	URLs d’accès aux services,
	•	liens vers la documentation API,
	•	captures d’écran du dashboard Grafana.

⸻

🧭 5. Conseils
	•	Concentrez-vous sur la simplicité fonctionnelle et la cohérence technique.
	•	Vous pouvez utiliser des listes statiques (List, Map) pour simuler les données.
	•	Utilisez les Actuator endpoints pour exposer les métriques.
	•	Si un microservice ne fonctionne pas, documentez ce que vous avez tenté.

⸻
