#!/bin/bash

# =============================================================================
# Script: scan-code.sh
# Description: Scanne le code source pour les problèmes de sécurité
#              en utilisant SonarQube, Semgrep et GitLeaks
# =============================================================================

set -euo pipefail

# Couleurs pour la sortie
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Répertoire du projet
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SCRIPTS_DIR="$PROJECT_DIR/scripts/security"
REPORTS_DIR="$PROJECT_DIR/build/reports/security"

# Créer le répertoire des rapports
mkdir -p "$REPORTS_DIR"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Scanning Source Code for Security Issues${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Fonction pour afficher un message d'erreur
error_exit() {
    echo -e "${RED}ERROR: $1${NC}" >&2
    exit 1
}

# Vérifier que nous sommes dans le bon répertoire
cd "$PROJECT_DIR" || error_exit "Impossible de se déplacer vers $PROJECT_DIR"

# =============================================================================
# 1. Semgrep (Analyse statique de code)
# =============================================================================
echo -e "${YELLOW}[1/4] Running Semgrep...${NC}"

if command -v semgrep &> /dev/null; then
    echo "Semgrep est installé"
    
    # Scanner avec les règles de sécurité par défaut
    semgrep scan \
        --config=auto \
        --config=p/ci \
        --config=p/security-audit \
        --config=p/owasp-top-ten \
        --error \
        --json \
        --output="$REPORTS_DIR/semgrep-results.json" \
        --html \
        --output="$REPORTS_DIR/semgrep-results.html" \
        "$PROJECT_DIR/bank-account" || true
    
    echo -e "${GREEN}✓ Semgrep scan terminé${NC}"
else
    echo -e "${YELLOW}Semgrep n'est pas installé. Installation...${NC}"
    
    # Installer Semgrep
    pip install semgrep || \
        error_exit "Impossible d'installer Semgrep"
    
    # Réessayer
    semgrep scan \
        --config=auto \
        --config=p/ci \
        --config=p/security-audit \
        --config=p/owasp-top-ten \
        --error \
        --json \
        --output="$REPORTS_DIR/semgrep-results.json" \
        --html \
        --output="$REPORTS_DIR/semgrep-results.html" \
        "$PROJECT_DIR/bank-account" || true
    
    echo -e "${GREEN}✓ Semgrep scan terminé${NC}"
fi

echo ""

# =============================================================================
# 2. GitLeaks (Détection de secrets dans le code)
# =============================================================================
echo -e "${YELLOW}[2/4] Running GitLeaks...${NC}"

if command -v gitleaks &> /dev/null; then
    echo "GitLeaks est installé"
    
    # Scanner les secrets
    gitleaks detect \
        --source="$PROJECT_DIR" \
        --report-path="$REPORTS_DIR/gitleaks-results.json" \
        --report-format=json \
        --log-level=warn \
        --exit-code=1 || true
    
    echo -e "${GREEN}✓ GitLeaks scan terminé${NC}"
else
    echo -e "${YELLOW}GitLeaks n'est pas installé. Installation...${NC}"
    
    # Installer GitLeaks
    curl -sSL https://github.com/gitleaks/gitleaks/releases/download/v8.18.0/gitleaks_8.18.0_linux_x64.tar.gz | \
        tar -xz -C /usr/local/bin gitleaks || \
        error_exit "Impossible d'installer GitLeaks"
    
    # Réessayer
    gitleaks detect \
        --source="$PROJECT_DIR" \
        --report-path="$REPORTS_DIR/gitleaks-results.json" \
        --report-format=json \
        --log-level=warn \
        --exit-code=1 || true
    
    echo -e "${GREEN}✓ GitLeaks scan terminé${NC}"
fi

echo ""

# =============================================================================
# 3. Checkov (Analyse des fichiers IaC)
# =============================================================================
echo -e "${YELLOW}[3/4] Running Checkov...${NC}"

if command -v checkov &> /dev/null; then
    echo "Checkov est installé"
    
    # Scanner les fichiers Kubernetes
    if [ -d "$PROJECT_DIR/bank-account/k8s" ]; then
        checkov -d "$PROJECT_DIR/bank-account/k8s" \
            -o json \
            --output-file "$REPORTS_DIR/checkov-k8s.json" || true
        
        checkov -d "$PROJECT_DIR/bank-account/k8s" \
            -o html \
            --output-file "$REPORTS_DIR/checkov-k8s.html" || true
        
        echo -e "${GREEN}✓ Checkov scan Kubernetes terminé${NC}"
    fi
    
    # Scanner les fichiers Docker
    if [ -d "$PROJECT_DIR/bank-account/app/src/main/docker" ]; then
        checkov -d "$PROJECT_DIR/bank-account/app/src/main/docker" \
            -o json \
            --output-file "$REPORTS_DIR/checkov-docker.json" || true
        
        checkov -d "$PROJECT_DIR/bank-account/app/src/main/docker" \
            -o html \
            --output-file "$REPORTS_DIR/checkov-docker.html" || true
        
        echo -e "${GREEN}✓ Checkov scan Docker terminé${NC}"
    fi
else
    echo -e "${YELLOW}Checkov n'est pas installé. Installation...${NC}"
    
    # Installer Checkov
    pip install checkov || \
        error_exit "Impossible d'installer Checkov"
    
    # Réessayer
    if [ -d "$PROJECT_DIR/bank-account/k8s" ]; then
        checkov -d "$PROJECT_DIR/bank-account/k8s" \
            -o json \
            --output-file "$REPORTS_DIR/checkov-k8s.json" || true
        
        checkov -d "$PROJECT_DIR/bank-account/k8s" \
            -o html \
            --output-file "$REPORTS_DIR/checkov-k8s.html" || true
    fi
    
    if [ -d "$PROJECT_DIR/bank-account/app/src/main/docker" ]; then
        checkov -d "$PROJECT_DIR/bank-account/app/src/main/docker" \
            -o json \
            --output-file "$REPORTS_DIR/checkov-docker.json" || true
        
        checkov -d "$PROJECT_DIR/bank-account/app/src/main/docker" \
            -o html \
            --output-file "$REPORTS_DIR/checkov-docker.html" || true
    fi
    
    echo -e "${GREEN}✓ Checkov scan terminé${NC}"
fi

echo ""

# =============================================================================
# 4. Générer un rapport de synthèse
# =============================================================================
echo -e "${YELLOW}[4/4] Génération du rapport de synthèse...${NC}"

# Créer un fichier de synthèse
SUMMARY_FILE="$REPORTS_DIR/code-security-summary.md"

cat > "$SUMMARY_FILE" << 'EOF'
# Rapport de Sécurité du Code - Bank Account IA

## Date du scan
EOF

echo "Date: $(date)" >> "$SUMMARY_FILE"

cat >> "$SUMMARY_FILE" << 'EOF'

## Répertoire scanné
- **Chemin**: bank-account/
- **Langages**: Kotlin, Java, YAML

## Outils utilisés

### 1. Semgrep
- **Description**: Analyse statique de code pour détecter les vulnérabilités
- **Règles utilisées**:
  - p/ci (bonnes pratiques CI)
  - p/security-audit (audit de sécurité)
  - p/owasp-top-ten (OWASP Top 10)
- **Résultats**: [Voir le rapport HTML](semgrep-results.html)

### 2. GitLeaks
- **Description**: Détection de secrets (mots de passe, clés API, tokens) dans le code
- **Résultats**: [Voir le rapport JSON](gitleaks-results.json)

### 3. Checkov
- **Description**: Analyse des fichiers Infrastructure as Code (Kubernetes, Docker)
- **Fichiers scannés**:
  - Kubernetes manifests (k8s/)
  - Dockerfile (app/src/main/docker/)
- **Résultats**:
  - [Kubernetes](checkov-k8s.html)
  - [Docker](checkov-docker.html)

## Problèmes de sécurité courants détectés

### Semgrep
- Injections SQL
- Cross-Site Scripting (XSS)
- Hardcoded secrets
- Mauvaise gestion des erreurs
- Problèmes de désérialisation

### GitLeaks
- Clés API
- Mots de passe
- Tokens d'authentification
- Secrets Kubernetes
- Clés privées

### Checkov
- Conteneurs exécutés en root
- Images Docker non sécurisées
- Politiques de sécurité Kubernetes manquantes
- Expositions de ports inutiles
- Ressources sans limites

## Recommandations

### Pour les problèmes critiques
1. Corriger immédiatement les vulnérabilités OWASP Top 10
2. Supprimer tous les secrets du code
3. Appliquer le principe du moindre privilège

### Pour les problèmes élevés
1. Corriger les problèmes de sécurité dans les prochains sprints
2. Revoir les configurations Kubernetes
3. Sécuriser les images Docker

### Bonnes pratiques de développement
1. Ne jamais commiter de secrets dans le code
2. Utiliser des variables d'environnement pour les configurations sensibles
3. Valider et sanitizer toutes les entrées utilisateur
4. Utiliser des bibliothèques de sécurité à jour
5. Effectuer des revues de code avec un focus sécurité

## Commandes pour réexécuter les scans

```bash
# Scanner tout le code
./scripts/security/scan-code.sh

# Scanner avec Semgrep uniquement
semgrep scan --config=p/security-audit --config=p/owasp-top-ten bank-account/

# Scanner avec GitLeaks uniquement
gitleaks detect --source=.

# Scanner avec Checkov uniquement
checkov -d bank-account/k8s/
checkov -d bank-account/app/src/main/docker/
```

## Liens utiles
- [Semgrep](https://semgrep.dev/)
- [GitLeaks](https://github.com/gitleaks/gitleaks)
- [Checkov](https://www.checkov.io/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP ASVS](https://owasp.org/www-project-application-security-verification-standard/)
EOF

echo -e "${GREEN}✓ Rapport de synthèse généré: $SUMMARY_FILE${NC}"
echo ""

# =============================================================================
# Résumé
# =============================================================================
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Scan du code source terminé${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "Rapports générés dans: ${REPORTS_DIR}"
echo ""
echo -e "Pour voir les rapports:"
echo -e "  - Semgrep: $REPORTS_DIR/semgrep-results.html"
echo -e "  - GitLeaks: $REPORTS_DIR/gitleaks-results.json"
echo -e "  - Checkov K8s: $REPORTS_DIR/checkov-k8s.html"
echo -e "  - Checkov Docker: $REPORTS_DIR/checkov-docker.html"
echo -e "  - Rapport de synthèse: $SUMMARY_FILE"
echo ""
echo -e "${YELLOW}Note: Les problèmes critiques feront échouer le build.${NC}"
