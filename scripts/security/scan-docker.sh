#!/bin/bash

# =============================================================================
# Script: scan-docker.sh
# Description: Scanne les images Docker pour les vulnérabilités
#              en utilisant Trivy et Docker Scout
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
DOCKER_DIR="$PROJECT_DIR/bank-account/app/src/main/docker"

# Créer le répertoire des rapports
mkdir -p "$REPORTS_DIR"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Scanning Docker Images for Vulnerabilities${NC}"
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
# 1. Builder l'image Docker
# =============================================================================
echo -e "${YELLOW}[1/4] Building Docker image...${NC}"

IMAGE_NAME="bank-account-ia"
IMAGE_TAG="security-scan"
FULL_IMAGE_NAME="$IMAGE_NAME:$IMAGE_TAG"

if [ -f "$DOCKER_DIR/Dockerfile" ]; then
    docker build \
        -t "$FULL_IMAGE_NAME" \
        -f "$DOCKER_DIR/Dockerfile" \
        "$PROJECT_DIR/bank-account" || \
        error_exit "Impossible de builder l'image Docker"
    
    echo -e "${GREEN}✓ Image Docker construite: $FULL_IMAGE_NAME${NC}"
else
    error_exit "Dockerfile non trouvé dans $DOCKER_DIR"
fi

echo ""

# =============================================================================
# 2. Trivy (Scanner de vulnérabilités pour les images Docker)
# =============================================================================
echo -e "${YELLOW}[2/4] Running Trivy...${NC}"

if command -v trivy &> /dev/null; then
    echo "Trivy est installé"
    
    # Scanner l'image avec Trivy
    trivy image \
        --severity HIGH,CRITICAL \
        --format json \
        --output "$REPORTS_DIR/trivy-results.json" \
        --format html \
        --output "$REPORTS_DIR/trivy-results.html" \
        "$FULL_IMAGE_NAME" || true
    
    # Vérifier s'il y a des vulnérabilités critiques
    CRITICAL_COUNT=$(trivy image \
        --severity CRITICAL \
        --format template \
        --template "@contrib/gitlab.tpl" \
        "$FULL_IMAGE_NAME" 2>/dev/null | grep -c "CRITICAL" || echo "0")
    
    if [ "$CRITICAL_COUNT" -gt 0 ]; then
        echo -e "${RED}✗ Trivy a trouvé $CRITICAL_COUNT vulnérabilité(s) CRITIQUE(s)${NC}"
        exit 1
    else
        echo -e "${GREEN}✓ Trivy scan terminé - Aucune vulnérabilité critique trouvée${NC}"
    fi
else
    echo -e "${YELLOW}Trivy n'est pas installé. Installation...${NC}"
    
    # Installer Trivy
    curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh | sh -s -- -b /usr/local/bin || \
        error_exit "Impossible d'installer Trivy"
    
    # Réessayer
    trivy image \
        --severity HIGH,CRITICAL \
        --format json \
        --output "$REPORTS_DIR/trivy-results.json" \
        --format html \
        --output "$REPORTS_DIR/trivy-results.html" \
        "$FULL_IMAGE_NAME" || true
    
    echo -e "${GREEN}✓ Trivy scan terminé${NC}"
fi

echo ""

# =============================================================================
# 3. Docker Scout (si disponible)
# =============================================================================
echo -e "${YELLOW}[3/4] Running Docker Scout...${NC}"

if command -v docker scout &> /dev/null; then
    echo "Docker Scout est disponible"
    
    # Analyser l'image avec Docker Scout
    docker scout quickview "$FULL_IMAGE_NAME" > "$REPORTS_DIR/docker-scout.txt" 2>&1 || true
    
    # Vérifier les vulnérabilités
    if grep -q "CRITICAL\|HIGH" "$REPORTS_DIR/docker-scout.txt"; then
        echo -e "${RED}✗ Docker Scout a trouvé des vulnérabilités${NC}"
    else
        echo -e "${GREEN}✓ Docker Scout scan terminé${NC}"
    fi
else
    echo -e "${YELLOW}Docker Scout n'est pas disponible. Sauter.${NC}"
fi

echo ""

# =============================================================================
# 4. Générer un rapport de synthèse
# =============================================================================
echo -e "${YELLOW}[4/4] Génération du rapport de synthèse...${NC}"

# Créer un fichier de synthèse
SUMMARY_FILE="$REPORTS_DIR/docker-security-summary.md"

cat > "$SUMMARY_FILE" << 'EOF'
# Rapport de Sécurité Docker - Bank Account IA

## Date du scan
EOF

echo "Date: $(date)" >> "$SUMMARY_FILE"

cat >> "$SUMMARY_FILE" << 'EOF'

## Image scannée
- **Nom**: bank-account-ia
- **Tag**: security-scan
- **Dockerfile**: bank-account/app/src/main/docker/Dockerfile

## Outils utilisés

### 1. Trivy
- **Description**: Scanner de vulnérabilités pour les conteneurs
- **Sévérités scannées**: HIGH, CRITICAL
- **Résultats**: [Voir le rapport HTML](trivy-results.html)

### 2. Docker Scout
- **Description**: Analyse des vulnérabilités Docker
- **Résultats**: [Voir le rapport texte](docker-scout.txt)

## Vulnérabilités trouvées

### Trivy
EOF

# Extraire les statistiques de Trivy
if [ -f "$REPORTS_DIR/trivy-results.json" ]; then
    echo "" >> "$SUMMARY_FILE"
    echo "| Sévérité | Nombre |" >> "$SUMMARY_FILE"
    echo "|----------|--------|" >> "$SUMMARY_FILE"
    
    # Compter les vulnérabilités par sévérité
    for severity in CRITICAL HIGH MEDIUM LOW UNKNOWN; do
        count=$(grep -o "\"$severity\"" "$REPORTS_DIR/trivy-results.json" 2>/dev/null | wc -l || echo "0")
        echo "| $severity | $count |" >> "$SUMMARY_FILE"
    done
    
    echo "" >> "$SUMMARY_FILE"
fi

cat >> "$SUMMARY_FILE" << 'EOF'

## Recommandations

### Pour les vulnérabilités critiques
1. Mettre à jour immédiatement la base image (eclipse-temurin:17-jre-jammy)
2. Vérifier les CVE et appliquer les correctifs
3. Rebuilder et redéployer l'image

### Pour les vulnérabilités élevées
1. Planifier une mise à jour dans les prochains jours
2. Évaluer l'impact sur l'application

### Bonnes pratiques Docker
1. Utiliser des images minimales (distroless, alpine)
2. Mettre à jour régulièrement les images de base
3. Scanner les images avant le déploiement
4. Utiliser des images signées et vérifiées

## Commandes pour réexécuter les scans

```bash
# Builder et scanner l'image
./scripts/security/scan-docker.sh

# Scanner une image existante
trivy image --severity HIGH,CRITICAL bank-account-ia:latest

# Voir les vulnérabilités avec Docker Scout
docker scout quickview bank-account-ia:latest
```

## Liens utiles
- [Trivy](https://github.com/aquasecurity/trivy)
- [Docker Scout](https://docs.docker.com/scout/)
- [CVE Database](https://cve.mitre.org/)
- [NVD](https://nvd.nist.gov/)
- [OWASP Docker Security](https://cheatsheetseries.owasp.org/cheatsheets/Docker_Security_Cheat_Sheet.html)
EOF

echo -e "${GREEN}✓ Rapport de synthèse généré: $SUMMARY_FILE${NC}"
echo ""

# =============================================================================
# Nettoyage
# =============================================================================
echo -e "${YELLOW}Nettoyage de l'image Docker...${NC}"
docker rmi "$FULL_IMAGE_NAME" 2>/dev/null || true
echo -e "${GREEN}✓ Nettoyage terminé${NC}"
echo ""

# =============================================================================
# Résumé
# =============================================================================
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Scan Docker terminé${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "Rapports générés dans: ${REPORTS_DIR}"
echo ""
echo -e "Pour voir les rapports:"
echo -e "  - Trivy: $REPORTS_DIR/trivy-results.html"
echo -e "  - Rapport de synthèse: $SUMMARY_FILE"
echo ""

if [ -f "$REPORTS_DIR/docker-scout.txt" ]; then
    echo -e "  - Docker Scout: $REPORTS_DIR/docker-scout.txt"
fi

echo ""
echo -e "${YELLOW}Note: Les vulnérabilités critiques feront échouer le build.${NC}"
