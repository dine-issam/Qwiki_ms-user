# MS-User - Qwiki Project

## Description

`ms-user` est un microservice backend développé avec **Spring Boot** pour le projet **Qwiki**, une plateforme intelligente de livraison.\
Ce microservice gère l'authentification, l'autorisation et les rôles des utilisateurs (Admin, Client, Marchand, Livreur), et assure la sécurité de l'accès aux autres microservices via **JWT**.

## Fonctionnalités principales

- Gestion des utilisateurs : création, mise à jour, suppression, récupération.
- Authentification sécurisée avec **JWT** et gestion des rôles.
- Gestion des permissions pour Admins, Clients, Marchands et Livreurs.
- Sécurisation des endpoints REST.
- Intégration avec Redis pour la gestion des tokens et sessions (cache).

## Technologies utilisées

- **Backend** : Java, Spring Boot, Spring Security, JWT
- **Base de données** : PostgreSQL
- **Cache / Token** : Redis
- **Communication microservices** : REST, Feign Client (si applicable)
- **Versioning / Collaboration** : Git / GitHub

## Installation et exécution

1. Cloner le dépôt :

```bash
git clone https://github.com/<votre-utilisateur>/ms-user.git
```

2. Configurer la base PostgreSQL et Redis dans `application.properties` ou `application.yml`.
3. Construire et exécuter le projet :

```bash
mvn clean install
mvn spring-boot:run
```

## API Endpoints

- `POST /api/auth/login` – Authentification utilisateur
- `POST /api/auth/register` – Création d’un nouvel utilisateur
- `GET /api/users` – Récupérer tous les utilisateurs (admin uniquement)
- `GET /api/users/{id}` – Récupérer un utilisateur par ID
- `PUT /api/users/{id}` – Mettre à jour un utilisateur
- `DELETE /api/users/{id}` – Supprimer un utilisateur

*(Ajouter d’autres endpoints si nécessaire)*

## Contribution

Contributions bienvenues ! Merci de créer une **issue** avant toute pull request.

## License

Ce projet est open-source sous licence MIT.

