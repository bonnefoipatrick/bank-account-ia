# Configuration ArgoCD pour Bank Account IA

Ce dossier contient les configurations ArgoCD pour déployer l'application **bank-account-ia** dans différents environnements.

## 📋 Prérequis

- Un cluster Kubernetes fonctionnel
- [ArgoCD](https://argoproj.github.io/cd/) installé sur le cluster
- Accès au dépôt GitHub (avec les bonnes permissions)
- Un registry Docker (GitHub Container Registry, Docker Hub, etc.)

## 🚀 Déploiement

### 1. Installer ArgoCD

```bash
# Créer le namespace argocd
kubectl create namespace argocd

# Installer ArgoCD
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
```

### 2. Accéder à l'interface ArgoCD

```bash
# Obtenir le mot de passe admin
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d

# Port-forward pour accéder à l'UI
kubectl port-forward svc/argocd-server -n argocd 8080:443
```

Accédez à https://localhost:8080 avec le nom d'utilisateur `admin` et le mot de passe obtenu.

### 3. Déployer les applications ArgoCD

#### Pour l'environnement de développement:

```bash
# Appliquer la configuration ArgoCD pour dev
kubectl apply -f bank-account-dev.yaml
```

#### Pour l'environnement de production:

```bash
# Appliquer la configuration ArgoCD pour prod
kubectl apply -f bank-account-prod.yaml
```

### 4. Synchroniser manuellement (si nécessaire)

```bash
# Synchroniser l'application dev
argocd app sync bank-account-dev

# Synchroniser l'application prod
argocd app sync bank-account-prod
```

## 📊 Structure des applications

### bank-account-dev
- **Namespace**: `bank-account-dev`
- **Source**: `bank-account/k8s/overlays/dev`
- **Synchronisation**: Automatique (toutes les 5 minutes)
- **Self-Healing**: Activé
- **Prune**: Activé

### bank-account-prod
- **Namespace**: `bank-account-prod`
- **Source**: `bank-account/k8s/overlays/prod`
- **Synchronisation**: Manuelle uniquement
- **Self-Healing**: Désactivé
- **Prune**: Désactivé

## 🔧 Configuration personnalisée

### Changer la version de l'image

Pour mettre à jour la version de l'image déployée:

1. Modifier le fichier `k8s/overlays/<env>/kustomization.yaml`
2. Changer la valeur de `newTag` dans la section `images`
3. Synchroniser l'application ArgoCD

Exemple:
```yaml
images:
  - name: ghcr.io/bonnefoipatrick/bank-account-ia
    newName: ghcr.io/bonnefoipatrick/bank-account-ia
    newTag: "1.0.0"  # Changer cette valeur
```

### Changer l'environnement

Pour changer l'environnement (dev/prod):

1. Modifier le fichier `k8s/overlays/<env>/kustomization.yaml`
2. Changer les variables d'environnement dans `configMapGenerator`
3. Synchroniser l'application ArgoCD

### Ajouter des ressources supplémentaires

Pour ajouter des ressources Kubernetes supplémentaires (HPA, PDB, etc.):

1. Créer le fichier dans `k8s/overlays/<env>/`
2. Ajouter le fichier dans la section `resources` du `kustomization.yaml`

## 📝 Bonnes pratiques

1. **Développement**: Utiliser la synchronisation automatique pour un déploiement continu
2. **Production**: Utiliser la synchronisation manuelle pour un contrôle total
3. **Rollback**: Utiliser l'interface ArgoCD pour faire un rollback vers une version précédente
4. **Surveillance**: Configurer des notifications pour les changements d'état

## 🔗 Liens utiles

- [Documentation ArgoCD](https://argoproj.github.io/cd/)
- [Kustomize Documentation](https://kubectl.docs.kubernetes.io/references/kustomize/)
- [ArgoCD CLI](https://argoproj.github.io/cd/cli/)
