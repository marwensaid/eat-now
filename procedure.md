# Procédure de déploiement de l'application EatNow

Ce document décrit les étapes pour compiler, tester, et déployer l'application EatNow.

## Étape 1 : Configuration du Projet et de Maven

Cette première étape consiste à structurer le projet et à configurer le fichier `pom.xml` parent qui gérera les dépendances communes, les versions et les modules de nos microservices.

L'arborescence des dossiers a été alignée sur les exigences du sujet (`menu-service`, `order-service`, `delivery-service`).

### Commandes de vérification

Pour vérifier que le `pom.xml` parent est correctement configuré et qu'il reconnaît ses modules, exécutez la commande suivante à la racine du projet. Elle ne doit produire aucune erreur.

```bash
# Valide la configuration du POM parent sans construire les sous-modules
mvn validate
```