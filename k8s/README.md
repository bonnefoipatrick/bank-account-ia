# Manifests Kubernetes pour Bank Account IA

Ce dossier contient tous les manifests Kubernetes pour déployer l'application **bank-account-ia**.

## 📁 Structure

```
k8s/
├── base/                      # Manifests de base (communs à tous les environnements)
│   ├── deployment.yaml        # Déploiement de l'application
│   ├── service.yaml           # Service ClusterIP
│   ├── ingress.yaml           # Ingress pour l'accès HTTP
│   ├── configmap.yaml         # Configuration de l'application
│   └── kustomization.yaml      # Configuration Kustomize de base
│
└── overlays/                 # Overlays pour différents environnements
    ├── dev/                   # Environnement de développement
    │   └── kustomization.yaml
    │
    └── prod/                  # Environnement de production
        └── kustomization.yaml
```

## 🚀 Déploiement

### Prérequis

- [kubectl](https://kubernetes.io/docs/tasks/tools/) installé
- [kustomize](https://kubectl.docs.kubernetes.io/installation/kustomize/) installé (inclus avec kubectl)
- Accès à un cluster Kubernetes

### Déploiement en développement

```bash
# Se placer dans le bon répertoire
cd bank-account/k8s/overlays/dev

# Appliquer la configuration
kubectl apply -k .

# Vérifier le déploiement
kubectl get all -n bank-account-dev
```

### Déploiement en production

```bash
# Se placer dans le bon répertoire
cd bank-account/k8s/overlays/prod

# Appliquer la configuration
kubectl apply -k .

# Vérifier le déploiement
kubectl get all -n bank-account-prod
```

## 📋 Manifests de base

### deployment.yaml

Déploiement de l'application avec:
- 1 réplica (2 en production)
- Configuration des ressources (CPU/Mémoire)
- Probes de santé (liveness et readiness)
- Sécurité (non-root, capabilities drop)
- Configuration via ConfigMap

### service.yaml

Service ClusterIP exposant le port 8080.

### ingress.yaml

Ingress NGINX avec:
- Redirection HTTP
- Support CORS
- Annotations pour ArgoCD

### configmap.yaml

Configuration de l'application:
- Spring Boot profiles
- Base de données H2
- Configuration Kafka
- Logging

## 🔧 Personnalisation

### Changer le nombre de réplicas

Modifier le fichier `overlays/<env>/kustomization.yaml`:

```yaml
patches:
  - patch: |-
      - op: replace
        path: /spec/replicas
        value: 3  # Nouveau nombre de réplicas
```

### Changer les ressources

Modifier le fichier `overlays/<env>/kustomization.yaml`:

```yaml
patches:
  - patch: |-
      - op: replace
        path: /spec/template/spec/containers/0/resources/limits/memory
        value: "4Gi"
  - patch: |-
      - op: replace
        path: /spec/template/spec/containers/0/resources/limits/cpu
        value: "2"
```

### Changer l'image Docker

Modifier le fichier `overlays/<env>/kustomization.yaml`:

```yaml
images:
  - name: ghcr.io/bonnefoipatrick/bank-account-ia
    newName: ghcr.io/bonnefoipatrick/bank-account-ia
    newTag: "2.0.0"  # Nouvelle version
```

### Ajouter des variables d'environnement

Modifier le fichier `overlays/<env>/kustomization.yaml`:

```yaml
configMapGenerator:
  - name: bank-account-<env>-config
    behavior: merge
    literals:
      - NOUVELLE_VARIABLE=valeur
```

## 📊 Vérification

### Vérifier les pods

```bash
kubectl get pods -n bank-account-<env>
```

### Vérifier les logs

```bash
kubectl logs -f deployment/bank-account-app -n bank-account-<env>
```

### Vérifier les services

```bash
kubectl get svc -n bank-account-<env>
```

### Vérifier l'ingress

```bash
kubectl get ingress -n bank-account-<env>
```

## 🔒 Sécurité

### Accès à la console H2 (développement uniquement)

En développement, la console H2 est activée:
- URL: `http://<ingress-host>/h2-console`
- JDBC URL: `jdbc:h2:mem:bankdb`
- User: `sa`
- Password: (vide)

⚠️ **En production, la console H2 est désactivée pour des raisons de sécurité.**

## 📝 Bonnes pratiques

1. **Ne jamais commiter de secrets** dans les manifests
2. Utiliser des **Secrets Kubernetes** pour les informations sensibles
3. Configurer des **ResourceQuotas** pour limiter l'utilisation des ressources
4. Utiliser des **NetworkPolicies** pour restreindre le trafic réseau
5. Configurer des **PodDisruptionBudgets** pour la haute disponibilité

## 🔗 Liens utiles

- [Kubernetes Documentation](https://kubernetes.io/docs/home/)
- [Kustomize Documentation](https://kubectl.docs.kubernetes.io/references/kustomize/)
- [NGINX Ingress Controller](https://kubernetes.github.io/ingress-nginx/)
