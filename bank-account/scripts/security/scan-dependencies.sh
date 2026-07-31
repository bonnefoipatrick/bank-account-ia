#!/bin/bash

# =============================================================================
# Script: scan-dependencies.sh
# Description: Scanne les dépendances du projet pour les vulnérabilités
#              en utilisant OWASP Dependency-Check et Snyk
# =============================================================================

set -euo pipefail

# Couleurs pour la sortie
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Répertoire du projet
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SCRIPTS_DIR="$PROJECT_DIR/scripts/security"
REPORTS_DIR="$PROJECT_DIR/build/reports/security"

# Créer le répertoire des rapports
mkdir -p "$REPORTS_DIR"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Scanning Dependencies for Vulnerabilities${NC}"
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
# 1. OWASP Dependency-Check
# =============================================================================
echo -e "${YELLOW}[1/3] Running OWASP Dependency-Check...${NC}"

if command -v dependency-check.sh &> /dev/null; then
    echo "OWASP Dependency-Check est installé"
    
    # Exécuter Dependency-Check
    dependency-check.sh \
        --project "bank-account-ia" \
        --scan "$PROJECT_DIR/bank-account" \
        --format "HTML" \
        --format "JSON" \
        --format "XML" \
        --out "$REPORTS_DIR/dependency-check" \
        --log "$REPORTS_DIR/dependency-check.log" \
        --failOnCVSS 7 \
        --cveModified 30 \
        --cvePublished 30
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ OWASP Dependency-Check terminé avec succès${NC}"
    else
        echo -e "${RED}✗ OWASP Dependency-Check a trouvé des vulnérabilités critiques${NC}"
        exit 1
    fi
else
    echo -e "${YELLOW}OWASP Dependency-Check n'est pas installé. Installation...${NC}"
    
    # Télécharger et installer Dependency-Check
    DEPENDENCY_CHECK_VERSION="8.4.0"
    DEPENDENCY_CHECK_DIR="$HOME/.dependency-check"
    DEPENDENCY_CHECK_URL="https://github.com/jeremylong/DependencyCheck/releases/download/v$DEPENDENCY_CHECK_VERSION/dependency-check-$DEPENDENCY_CHECK_VERSION-release.zip"
    
    mkdir -p "$DEPENDENCY_CHECK_DIR"
    curl -L "$DEPENDENCY_CHECK_URL" -o "$DEPENDENCY_CHECK_DIR/dependency-check.zip" || \
        error_exit "Impossible de télécharger Dependency-Check"
    
    unzip -o "$DEPENDENCY_CHECK_DIR/dependency-check.zip" -d "$DEPENDENCY_CHECK_DIR" || \
        error_exit "Impossible de décompresser Dependency-Check"
    
    export PATH="$DEPENDENCY_CHECK_DIR/dependency-check/bin:$PATH"
    
    # Réessayer
    dependency-check.sh \
        --project "bank-account-ia" \
        --scan "$PROJECT_DIR/bank-account" \
        --format "HTML" \
        --format "JSON" \
        --format "XML" \
        --out "$REPORTS_DIR/dependency-check" \
        --log "$REPORTS_DIR/dependency-check.log" \
        --failOnCVSS 7
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ OWASP Dependency-Check terminé avec succès${NC}"
    else
        echo -e "${RED}✓ OWASP Dependency-Check a trouvé des vulnérabilités critiques${NC}"
        exit 1
    fi
fi

echo ""

# =============================================================================
# 2. Snyk (si disponible)
# =============================================================================
echo -e "${YELLOW}[2/3] Running Snyk...${NC}"

if command -v snyk &> /dev/null; then
    echo "Snyk est installé"
    
    # Authentification Snyk (nécessite un token)
    if [ -z "${SNYK_TOKEN:-}" ]; then
        echo -e "${YELLOW}SNYK_TOKEN non défini. Sauter Snyk.${NC}"
        echo -e "${YELLOW}Pour utiliser Snyk, définissez la variable d'environnement SNYK_TOKEN${NC}"
    else
        # Scanner les dépendances Gradle
        cd "$PROJECT_DIR/bank-account"
        snyk test --all-projects \
            --severity-threshold=high \
            --json-file-output="$REPORTS_DIR/snyk-dependencies.json" \
            --html-file-output="$REPORTS_DIR/snyk-dependencies.html" || true
        
        # Scanner le Dockerfile
        if [ -f "$PROJECT_DIR/bank-account/app/src/main/docker/Dockerfile" ]; then
            snyk container test "$PROJECT_DIR/bank-account/app/src/main/docker/Dockerfile" \
                --json-file-output="$REPORTS_DIR/snyk-docker.json" \
                --html-file-output="$REPORTS_DIR/snyk-docker.html" || true
        fi
        
        echo -e "${GREEN}✓ Snyk scan terminé${NC}"
    fi
else
    echo -e "${YELLOW}Snyk n'est pas installé. Installation...${NC}"
    echo -e "${YELLOW}Pour installer Snyk: npm install -g snyk${NC}"
    echo -e "${YELLOW}Puis définissez la variable SNYK_TOKEN${NC}"
fi

echo ""

# =============================================================================
# 3. Générer un rapport de synthèse
# =============================================================================
echo -e "${YELLOW}[3/3] Génération du rapport de synthèse...${NC}"

# Créer un fichier de synthèse
SUMMARY_FILE="$REPORTS_DIR/security-summary.md"

cat > "$SUMMARY_FILE" << 'EOF'
# Rapport de Sécurité - Bank Account IA

## Date du scan
EOF

echo "Date: $(date)" >> "$SUMMARY_FILE"

cat >> "$SUMMARY_FILE" << 'EOF'

## Outils utilisés

### 1. OWASP Dependency-Check
- **Version**: 8.4.0
- **Objectif**: Détecter les vulnérabilités dans les dépendances
- **Seuil**: CVSS >= 7 (High/Critical)
- **Résultats**: [Voir le rapport HTML](dependency-check.html)

### 2. Snyk
- **Objectif**: Détecter les vulnérabilités dans les dépendances et les images Docker
- **Seuil**: High/Critical
- **Résultats**: 
  - [Dépendances](snyk-dependencies.html)
  - [Docker](snyk-docker.html)

## Recommandations

### Pour les vulnérabilités critiques (CVSS >= 7.0)
1. Mettre à jour immédiatement les dépendances vulnérables
2. Vérifier les CVE et appliquer les correctifs
3. Tester les changements avant le déploiement

### Pour les vulnérabilités élevées (CVSS >= 4.0)
1. Planifier une mise à jour dans les prochains sprints
2. Évaluer l'impact sur l'application

### Pour les vulnérabilités moyennes et faibles
1. Surveiller les mises à jour
2. Appliquer les correctifs lors des mises à jour régulières

## Commandes pour réexécuter les scans

```bash
# OWASP Dependency-Check
./scripts/security/scan-dependencies.sh

# Snyk (nécessite SNYK_TOKEN)
export SNYK_TOKEN="votre_token"
./scripts/security/scan-dependencies.sh

# Trivy (pour les images Docker)
./scripts/security/scan-docker.sh
```

## Liens utiles
- [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/)
- [Snyk](https://snyk.io/)
- [CVE Database](https://cve.mitre.org/)
- [NVD](https://nvd.nist.gov/)
EOF

echo -e "${GREEN}✓ Rapport de synthèse généré: $SUMMARY_FILE${NC}"
echo ""

# =============================================================================
# Résumé
# =============================================================================
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Scan des dépendances terminé${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "Rapports générés dans: ${REPORTS_DIR}"
echo ""
echo -e "Pour voir les rapports:"
echo -e "  - OWASP Dependency-Check: $REPORTS_DIR/dependency-check.html"
echo -e "  - Rapport de synthèse: $SUMMARY_FILE"
echo ""

if [ -f "$REPORTS_DIR/snyk-dependencies.html" ]; then
    echo -e "  - Snyk Dependencies: $REPORTS_DIR/snyk-dependencies.html"
fi

if [ -f "$REPORTS_DIR/snyk-docker.html" ]; then
    echo -e "  - Snyk Docker: $REPORTS_DIR/snyk-docker.html"
fi

echo ""
echo -e "${YELLOW}Note: Les vulnérabilités critiques (CVSS >= 7) feront échouer le build.${NC}"
