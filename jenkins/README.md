# Pipeline CI/CD Jenkins pour Bank Account IA

Ce dossier contient la configuration du pipeline Jenkins pour construire, tester et déployer l'application **bank-account-ia**.

## 📋 Prérequis

- [Jenkins](https://www.jenkins.io/) installé et configuré
- [Docker](https://www.docker.com/) installé sur les nœuds Jenkins
- [kubectl](https://kubernetes.io/docs/tasks/tools/) installé (pour le déploiement)
- [ArgoCD CLI](https://argoproj.github.io/cd/cli/) installé (optionnel)
- Accès au dépôt GitHub
- Accès au registry Docker (GitHub Container Registry, Docker Hub, etc.)

## 🚀 Configuration

### 1. Installer les plugins Jenkins nécessaires

- **Git Plugin** - Pour cloner le dépôt
- **Docker Pipeline Plugin** - Pour construire les images Docker
- **Kubernetes Plugin** - Pour déployer sur Kubernetes (optionnel)
- **Pipeline Plugin** - Pour les pipelines as code
- **Credentials Plugin** - Pour gérer les secrets

### 2. Configurer les credentials

#### GitHub Credentials

1. Aller dans **Jenkins > Manage Jenkins > Manage Credentials**
2. Ajouter un nouveau credential de type **Username with password**
   - Username: `votre_username_github`
   - Password: `votre_token_github` (avec accès repo)
   - ID: `GITHUB_CREDENTIALS`

#### Docker Registry Credentials

1. Aller dans **Jenkins > Manage Jenkins > Manage Credentials**
2. Ajouter un nouveau credential de type **Username with password**
   - Username: `votre_username_docker`
   - Password: `votre_token_docker`
   - ID: `DOCKER_HUB_CREDENTIALS`

#### Kubernetes Credentials (optionnel)

1. Ajouter un credential de type **Kubeconfig**
   - ID: `KUBECONFIG_CREDENTIALS`
   - Contenu: votre fichier kubeconfig

### 3. Créer un nouveau Pipeline

1. Aller dans **Jenkins > New Item**
2. Sélectionner **Pipeline**
3. Donner un nom: `bank-account-ia-pipeline`
4. Sélectionner **Pipeline script from SCM**
5. Configurer:
   - SCM: **Git**
   - Repository URL: `https://github.com/bonnefoipatrick/bank-account-ia.git`
   - Credentials: `GITHUB_CREDENTIALS`
   - Branch: `main`
   - Script Path: `Jenkinsfile`
6. Sauvegarder

## 📊 Pipeline Stages

Le pipeline contient les étapes suivantes:

### 1. Checkout
- Clone le dépôt Git

### 2. Build
- Exécute `./gradlew clean build`
- Archive les artefacts (JAR files)
- Archive les rapports de tests

### 3. Test
- Exécute `./gradlew test`
- Publie les résultats des tests (JUnit)

### 4. Docker Build
- Construit l'image Docker avec le tag `BUILD_NUMBER`
- Tag aussi comme `latest`
- Option: Pousse l'image vers le registry

### 5. Deploy to Dev (optionnel)
- Déclenche un déploiement en développement
- Utilise ArgoCD pour synchroniser
- **Condition**: Branch = main

### 6. Deploy to Prod (manuel)
- Attend une confirmation manuelle
- Déclenche un déploiement en production
- **Condition**: Branch = main + confirmation manuelle

## 🔧 Personnalisation

### Changer le registry Docker

Modifier dans le `Jenkinsfile`:

```groovy
environment {
    DOCKER_REGISTRY = 'votre-registry.io'
    DOCKER_IMAGE_NAME = 'votre-org/bank-account-ia'
}
```

### Changer les namespaces Kubernetes

Modifier dans le `Jenkinsfile`:

```groovy
environment {
    K8S_NAMESPACE_DEV = 'votre-namespace-dev'
    K8S_NAMESPACE_PROD = 'votre-namespace-prod'
}
```

### Activer le push Docker automatique

Décommenter dans le `Jenkinsfile`:

```groovy
// Login vers le registry
withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CREDENTIALS', 
    usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
    sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS} ${DOCKER_REGISTRY}"
}

// Pousser l'image
sh "docker push ${DOCKER_IMAGE_NAME}:${IMAGE_TAG}"
sh "docker push ${DOCKER_IMAGE_NAME}:latest"
```

### Changer la stratégie de déploiement

Pour utiliser `kubectl` directement au lieu d'ArgoCD:

```groovy
sh """
    kubectl set image deployment/bank-account-app \\
        bank-account-app=${DOCKER_IMAGE_NAME}:${IMAGE_TAG} \\
        -n ${K8S_NAMESPACE_DEV}
"""
```

## 📝 Bonnes pratiques

1. **Sécurité**: Ne jamais stocker de secrets en clair dans le Jenkinsfile
2. **Tests**: Toujours exécuter les tests avant le déploiement
3. **Rollback**: Prévoir un mécanisme de rollback
4. **Notifications**: Configurer des notifications (Slack, Email) pour les échecs
5. **Monitoring**: Surveiller les builds et déploiements

## 🔗 Liens utiles

- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Jenkins Pipeline Syntax](https://www.jenkins.io/doc/book/pipeline/syntax/)
- [Docker Documentation](https://docs.docker.com/)
- [Kubernetes Documentation](https://kubernetes.io/docs/home/)
- [ArgoCD Documentation](https://argoproj.github.io/cd/)

## 🛠️ Dépannage

### Erreur: "docker: not found"

**Solution**: Installer Docker sur les nœuds Jenkins ou utiliser un agent avec Docker.

### Erreur: "gradlew: permission denied"

**Solution**: Exécuter `chmod +x ./gradlew` avant le build.

### Erreur: "No such file or directory"

**Solution**: Vérifier que le chemin vers le Dockerfile est correct dans le Jenkinsfile.

### Erreur: "Authentication required"

**Solution**: Vérifier que les credentials sont correctement configurés dans Jenkins.
