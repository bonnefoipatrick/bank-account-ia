# Bank Account - Frontend Angular

Module frontend Angular (dernière version stable, Angular 22) pour l'application
`bank-account-ia`. Il consomme l'API REST exposée par le module Spring Boot
`app` (`com.bankaccount.infrastructure.web.AccountController`).

## Fonctionnalités

- Recherche des comptes d'un client (`GET /api/v1/accounts/customer/{customerId}`)
- Création d'un compte (`POST /api/v1/accounts`)
- Détail d'un compte + historique des transactions
  (`GET /api/v1/accounts/{id}`, `GET /api/v1/accounts/{id}/transactions`)
- Dépôt / retrait (`POST /api/v1/accounts/{id}/deposit|withdraw`)
- Virement entre deux comptes (`POST /api/v1/accounts/transfer`)
- Désactivation d'un compte (`PATCH /api/v1/accounts/{id}/deactivate`)

## Stack technique

- Angular 22 (composants standalone, signals, nouvelle syntaxe de contrôle
  `@if` / `@for`, nouveau builder `@angular/build`)
- TypeScript strict
- Node.js >= 22.12 (ou 24.x / 26.x)

## Prérequis

- Node.js 22.12+ et npm 10+
- Le backend `bank-account-service` doit tourner sur `http://localhost:8080`
  (voir `../app`)

## Démarrage en développement

```bash
cd frontend
npm install
npm start
```

L'application est servie sur `http://localhost:4200`. Le fichier
`proxy.conf.json` redirige automatiquement les appels `/api/**` vers
`http://localhost:8080`, donc aucune configuration CORS supplémentaire n'est
nécessaire en local.

## Build de production

```bash
npm run build:prod
```

Le résultat est généré dans `dist/bank-account-frontend/browser`.

## Docker

Un `Dockerfile` multi-stage (build Node puis serveur `nginx`) est fourni,
sur le même modèle que `app/src/main/docker/Dockerfile` :

```bash
docker build -t bank-account-frontend .
docker run -p 8081:80 bank-account-frontend
```

En production, `nginx.conf` proxie `/api/` vers le service `bank-account-service`
(nom résolu via Kubernetes/Docker network). Adapter ce nom si besoin
(voir `k8s/base/service.yaml`).

## Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── core/
│   │   │   ├── models/        # Types alignés sur les DTOs Kotlin
│   │   │   └── services/      # AccountService (client HTTP)
│   │   ├── features/accounts/ # Composants standalone (liste, création, détail)
│   │   ├── shared/pipes/      # Pipe de formatage monétaire
│   │   ├── app.config.ts      # Configuration bootstrap (router, HttpClient)
│   │   └── app.routes.ts      # Routes de l'application
│   └── environments/          # Config par environnement (apiBaseUrl)
├── angular.json
├── package.json
├── proxy.conf.json
├── Dockerfile
└── nginx.conf
```

## Intégration au dépôt

Ce module a été ajouté aux côtés des modules Gradle existants
(`app`, `application`, `domain`, `infrastructure`) mais **n'est pas un
sous-module Gradle** : il possède son propre `package.json` et cycle de build
Node, indépendant de `settings.gradle.kts`. Pensez à l'ajouter à votre
pipeline CI/CD (`jenkins/Jenkinsfile`) et à vos manifests `k8s/` si vous
souhaitez le déployer comme un service à part entière.
