# Scripts de Sécurité - Bank Account IA

Ce dossier contient les scripts pour exécuter des **scans de sécurité complets** sur le projet.

## 📁 Structure

```
scripts/security/
├── scan-dependencies.sh    # Scan des dépendances (OWASP Dependency-Check, Snyk)
├── scan-docker.sh         # Scan des images Docker (Trivy, Docker Scout)
├── scan-code.sh           # Scan du code source (Semgrep, GitLeaks, Checkov)
├── run-security-scans.sh  # Script principal pour exécuter tous les scans
└── README.md              # Ce fichier
```

## 🚀 Prérequis

### Outils requis

| Outil | Description | Installation |
|-------|-------------|--------------|
| **OWASP Dependency-Check** | Scan des vulnérabilités dans les dépendances | Auto-installé par le script |
| **Snyk** | Scan des vulnérabilités (nécessite un token) | `npm install -g snyk` |
| **Trivy** | Scan des images Docker | Auto-installé par le script |
| **Docker Scout** | Analyse des images Docker | Inclus avec Docker Desktop |
| **Semgrep** | Analyse statique de code | Auto-installé par le script |
| **GitLeaks** | Détection de secrets | Auto-installé par le script |
| **Checkov** | Scan des fichiers IaC | Auto-installé par le script |

### Variables d'environnement

| Variable | Description | Obligatoire |
|----------|-------------|------------|
| `SNYK_TOKEN` | Token d'API Snyk | Non (mais recommandé) |

## 📊 Scans disponibles

### 1. Scan des Dépendances (`scan-dependencies.sh`)

**Objectif**: Détecter les vulnérabilités dans les bibliothèques tierces.

**Outils utilisés**:
- OWASP Dependency-Check (obligatoire)
- Snyk (optionnel, nécessite un token)

**Sévérités scannées**:
- CRITICAL (CVSS >= 9.0)
- HIGH (CVSS >= 7.0)

**Commande**:
```bash
./scripts/security/scan-dependencies.sh
```

**Rapports générés**:
- `build/reports/security/dependency-check.html`
- `build/reports/security/dependency-check.json`
- `build/reports/security/snyk-dependencies.html` (si Snyk est configuré)
- `build/reports/security/security-summary.md`

### 2. Scan Docker (`scan-docker.sh`)

**Objectif**: Détecter les vulnérabilités dans les images Docker.

**Outils utilisés**:
- Trivy (obligatoire)
- Docker Scout (optionnel)

**Sévérités scannées**:
- CRITICAL
- HIGH

**Commande**:
```bash
./scripts/security/scan-docker.sh
```

**Rapports générés**:
- `build/reports/security/trivy-results.html`
- `build/reports/security/trivy-results.json`
- `build/reports/security/docker-scout.txt`
- `build/reports/security/docker-security-summary.md`

### 3. Scan du Code Source (`scan-code.sh`)

**Objectif**: Détecter les problèmes de sécurité dans le code source.

**Outils utilisés**:
- Semgrep (analyse statique)
- GitLeaks (détection de secrets)
- Checkov (analyse des fichiers IaC)

**Problèmes détectés**:
- Injections SQL
- Cross-Site Scripting (XSS)
- Hardcoded secrets
- Mauvaise gestion des erreurs
- Problèmes de désérialisation
- Configurations Kubernetes non sécurisées
- Images Docker non sécurisées

**Commande**:
```bash
./scripts/security/scan-code.sh
```

**Rapports générés**:
- `build/reports/security/semgrep-results.html`
- `build/reports/security/gitleaks-results.json`
- `build/reports/security/checkov-k8s.html`
- `build/reports/security/checkov-docker.html`
- `build/reports/security/code-security-summary.md`

### 4. Tous les Scans (`run-security-scans.sh`)

**Objectif**: Exécuter tous les scans de sécurité en une seule commande.

**Commande**:
```bash
./scripts/security/run-security-scans.sh
```

Ce script propose un menu interactif pour sélectionner les scans à exécuter.

## 🎯 Intégration CI/CD

### Dans Jenkins

Le `Jenkinsfile` est déjà configuré pour exécuter les scans de sécurité:

1. **Scan du code** - Avant le build
2. **Scan des dépendances** - Avant le build
3. **Tests unitaires** - Après le build
4. **Tests BDD (Cucumber)** - Après le build
5. **Tests de sécurité API** - Après le build
6. **Scan Docker** - Après le build de l'image

### Configuration

Pour activer/désactiver les scans dans Jenkins:

```groovy
environment {
    RUN_SECURITY_SCANS = "true"  // ou "false" pour désactiver
    FAIL_ON_CRITICAL_VULNERABILITIES = "true"  // Échouer sur les vulnérabilités critiques
}
```

### En local

Pour exécuter les scans localement:

```bash
# Donner les permissions
chmod +x scripts/security/*.sh

# Exécuter tous les scans
./scripts/security/run-security-scans.sh

# Ou exécuter un scan spécifique
./scripts/security/scan-dependencies.sh
./scripts/security/scan-code.sh
./scripts/security/scan-docker.sh
```

## 📋 Bonnes pratiques

### 1. Intégration continue
- Exécuter les scans à chaque commit
- Échouer le build sur les vulnérabilités critiques
- Notifier l'équipe des nouvelles vulnérabilités

### 2. Gestion des vulnérabilités
- **Critique (CVSS >= 9.0)**: Corriger immédiatement
- **Élevée (CVSS >= 7.0)**: Corriger dans les 7 jours
- **Moyenne (CVSS >= 4.0)**: Corriger dans le prochain sprint
- **Faible (CVSS < 4.0)**: Surveiller et corriger si possible

### 3. Sécurité du code
- Ne jamais commiter de secrets dans le code
- Utiliser des variables d'environnement pour les configurations sensibles
- Valider et sanitizer toutes les entrées utilisateur
- Utiliser des bibliothèques de sécurité à jour
- Effectuer des revues de code avec un focus sécurité

### 4. Sécurité des dépendances
- Mettre à jour régulièrement les dépendances
- Utiliser des outils comme Dependabot pour les mises à jour automatiques
- Surveiller les nouvelles vulnérabilités (CVE)

### 5. Sécurité Docker
- Utiliser des images minimales (distroless, alpine)
- Mettre à jour régulièrement les images de base
- Scanner les images avant le déploiement
- Utiliser des images signées et vérifiées

## 🔗 Liens utiles

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/)
- [Snyk](https://snyk.io/)
- [Trivy](https://github.com/aquasecurity/trivy)
- [Semgrep](https://semgrep.dev/)
- [GitLeaks](https://github.com/gitleaks/gitleaks)
- [Checkov](https://www.checkov.io/)
- [CVE Database](https://cve.mitre.org/)
- [NVD](https://nvd.nist.gov/)
- [Docker Security](https://docs.docker.com/engine/security/)
- [Kubernetes Security](https://kubernetes.io/docs/concepts/security/)

## 🛠️ Dépannage

### Erreur: "Command not found"

**Solution**: Le script installera automatiquement l'outil manquant. Assurez-vous d'avoir:
- `curl` installé
- `unzip` installé (pour Dependency-Check)
- `pip` installé (pour Semgrep, Checkov)
- Droits d'écriture dans `/usr/local/bin`

### Erreur: "Permission denied"

**Solution**: Donner les permissions d'exécution:
```bash
chmod +x scripts/security/*.sh
```

### Erreur: "SNYK_TOKEN not set"

**Solution**: Définir la variable d'environnement:
```bash
export SNYK_TOKEN="votre_token_snyk"
./scripts/security/scan-dependencies.sh
```

### Erreur: "Docker not running"

**Solution**: Démarrer Docker avant d'exécuter les scans:
```bash
sudo systemctl start docker
```

## 📊 Exemple de rapport

Après l'exécution des scans, vous trouverez un rapport complet dans:
```
build/reports/security/
```

Structure typique:
```
build/reports/security/
├── security-summary.md              # Résumé des dépendances
├── dependency-check/                # Rapports OWASP
├── snyk-dependencies.json            # Rapports Snyk
├── code-security-summary.md         # Résumé du code
├── semgrep-results.html              # Rapports Semgrep
├── gitleaks-results.json             # Rapports GitLeaks
├── checkov-k8s.html                  # Rapports Checkov K8s
├── docker-security-summary.md       # Résumé Docker
├── trivy-results.html               # Rapports Trivy
└── security-global-summary.md        # Rapport global
```

## 🎓 Formation

Pour en savoir plus sur la sécurité des applications:
- [OWASP Cheat Sheets](https://cheatsheetseries.owasp.org/)
- [Google Cloud Security Best Practices](https://cloud.google.com/security/best-practices)
- [AWS Security Best Practices](https://aws.amazon.com/architecture/security/)
- [Microsoft Security Best Practices](https://docs.microsoft.com/en-us/security/)
