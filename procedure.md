# Procédure de déploiement de l'application EatNow

Ce document décrit les étapes pour compiler, tester, et déployer l'application EatNow.

## Étape 1 : Configuration du Projet et de Maven

### Commandes de vérification

Pour vérifier que le `pom.xml` parent est correctement configuré et qu'il reconnaît ses modules, exécutez la commande suivante à la racine du projet. Elle ne doit produire aucune erreur.

```bash
# Valide la configuration du POM parent sans construire les sous-modules
mvn validate
```

## Étape 2 : Configuration du microservice `menu-service`

### Commande de compilation

À ce stade, le projet complet (parent + `menu-service`) peut être compilé. Cette commande va télécharger les dépendances, compiler le code et lancer les tests (qui sont vides pour l'instant).

```bash
# Compile l'ensemble des modules et installe les artefacts dans le repository local
mvn clean install
```

