#!/bin/bash

# =============================================================================
# Script: run-security-scans.sh
# Description: Script principal pour exécuter tous les scans de sécurité
# =============================================================================

set -euo pipefail

# Couleurs pour la sortie
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Répertoire du projet
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SCRIPTS_DIR="$PROJECT_DIR/security"
REPORTS_DIR="$PROJECT_DIR/../build/reports/security"

# Créer le répertoire des rapports
mkdir -p "$REPORTS_DIR"

# Fonction pour afficher un message d'erreur
error_exit() {
    echo -e "${RED}ERROR: $1${NC}" >&2
    exit 1
}

# Fonction pour afficher un titre
title() {
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}  $1${NC}"
    echo -e "${BLUE}========================================${NC}"
    echo ""
}

# Vérifier que nous sommes dans le bon répertoire
cd "$PROJECT_DIR" || error_exit "Impossible de se déplacer vers $PROJECT_DIR"

echo -e "${GREEN}"
echo "  ███████╗██╗   ██╗██████╗ ██████╗ ██████╗ ██╗     ███████╗███████╗"
echo "  ██╔════╝╚██╗ ██╔╝██╔═══██╗██╔═══██╗██╔══██╗██║     ██╔════╝██╔════╝"
echo "  █████╗   ╚████╔╝ ██║   ██║██║   ██║██║  ██║██║     █████╗  ███████╗"
echo "  ██╔══╝    ╚██╔╝  ██║   ██║██║   ██║██║  ██║██║     ██╔══╝  ╚════██║"
echo "  ██║        ██║   ╚██████╔╝╚██████╔╝██████╔╝███████╗███████╗██║  ██║"
echo "  ╚═╝        ╚═╝    ╚═════╝  ╚═════╝ ╚═════╝ ╚══════╝╚══════╝╚═╝  ╚═╝"
echo -e "${NC}"
echo ""

title "SCANS DE SÉCURITÉ - BANK ACCOUNT IA"

# Afficher le menu
PS3="Sélectionnez le type de scan à exécuter (ou 'all' pour tout exécuter): "
options=(
    "dependencies" "Scan des dépendances (OWASP Dependency-Check, Snyk)"
    "docker" "Scan des images Docker (Trivy, Docker Scout)"
    "code" "Scan du code source (Semgrep, GitLeaks, Checkov)"
    "all" "Exécuter tous les scans"
    "quit" "Quitter"
)

select opt in "${options[@]}"; do
    case $opt in
        "dependencies")
            echo -e "${YELLOW}Exécution du scan des dépendances...${NC}"
            "$SCRIPTS_DIR/scan-dependencies.sh"
            ;;
        "docker")
            echo -e "${YELLOW}Exécution du scan Docker...${NC}"
            "$SCRIPTS_DIR/scan-docker.sh"
            ;;
        "code")
            echo -e "${YELLOW}Exécution du scan du code source...${NC}"
            "$SCRIPTS_DIR/scan-code.sh"
            ;;
        "all")
            echo -e "${YELLOW}Exécution de tous les scans...${NC}"
            
            # Scan des dépendances
            title "SCAN 1/3: Dépendances"
            "$SCRIPTS_DIR/scan-dependencies.sh" || {
                echo -e "${RED}Le scan des dépendances a échoué avec des vulnérabilités critiques${NC}"
                exit 1
            }
            
            # Scan du code
            title "SCAN 2/3: Code Source"
            "$SCRIPTS_DIR/scan-code.sh" || {
                echo -e "${RED}Le scan du code a échoué avec des problèmes critiques${NC}"
                exit 1
            }
            
            # Scan Docker
            title "SCAN 3/3: Images Docker"
            "$SCRIPTS_DIR/scan-docker.sh" || {
                echo -e "${RED}Le scan Docker a échoué avec des vulnérabilités critiques${NC}"
                exit 1
            }
            
            # Générer un rapport global
            title "RAPPORT GLOBAL"
            
            GLOBAL_SUMMARY="$REPORTS_DIR/security-global-summary.md"
            
            cat > "$GLOBAL_SUMMARY" << 'EOF'
# Rapport de Sécurité Global - Bank Account IA

## Date
EOF
            
            echo "Date: $(date)" >> "$GLOBAL_SUMMARY"
            
            cat >> "$GLOBAL_SUMMARY" << 'EOF'

## Résumé des scans

### 📦 Scan des Dépendances
- **Statut**: ✅ Terminé
- **Outils**: OWASP Dependency-Check, Snyk
- **Résultats**: [Voir le rapport](security-summary.md)

### 💻 Scan du Code Source
- **Statut**: ✅ Terminé
- **Outils**: Semgrep, GitLeaks, Checkov
- **Résultats**: [Voir le rapport](code-security-summary.md)

### 🐳 Scan des Images Docker
- **Statut**: ✅ Terminé
- **Outils**: Trivy, Docker Scout
- **Résultats**: [Voir le rapport](docker-security-summary.md)

## 📊 Statistiques Globales

| Catégorie | Outils | Statut |
|----------|--------|--------|
| Dépendances | OWASP DC, Snyk | ✅ |
| Code Source | Semgrep, GitLeaks, Checkov | ✅ |
| Docker | Trivy, Docker Scout | ✅ |

## 🎯 Recommandations Prioritaires

1. **Corriger les vulnérabilités critiques** (CVSS >= 7.0)
2. **Supprimer les secrets du code** (si détectés par GitLeaks)
3. **Mettre à jour les dépendances vulnérables**
4. **Sécuriser les images Docker**
5. **Appliquer les bonnes pratiques OWASP**

## 📁 Structure des rapports

```
build/reports/security/
├── security-summary.md              # Résumé des dépendances
├── dependency-check/                # Rapports OWASP Dependency-Check
│   ├── dependency-check.html
│   ├── dependency-check.json
│   └── dependency-check.xml
├── snyk-dependencies.json            # Rapports Snyk (si disponible)
├── snyk-dependencies.html
├── snyk-docker.json
├── snyk-docker.html
├── code-security-summary.md         # Résumé du code
├── semgrep-results.json              # Rapports Semgrep
├── semgrep-results.html
├── gitleaks-results.json             # Rapports GitLeaks
├── checkov-k8s.json                  # Rapports Checkov K8s
├── checkov-k8s.html
├── checkov-docker.json               # Rapports Checkov Docker
├── checkov-docker.html
├── docker-security-summary.md       # Résumé Docker
├── trivy-results.json               # Rapports Trivy
├── trivy-results.html
├── docker-scout.txt                  # Rapports Docker Scout
└── security-global-summary.md        # Ce rapport
```

## 🚀 Prochaines étapes

1. **Revoir les rapports** générés dans `build/reports/security/`
2. **Corriger les vulnérabilités** critiques et élevées
3. **Valider les corrections** avec de nouveaux scans
4. **Intégrer les scans** dans le pipeline CI/CD
5. **Configurer des alertes** pour les nouvelles vulnérabilités

## 🔗 Liens utiles

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [CWE Top 25](https://cwe.mitre.org/top25/)
- [NVD](https://nvd.nist.gov/)
- [Snyk Vulnerability Database](https://snyk.io/vuln/)
EOF
            
            echo -e "${GREEN}✓ Rapport global généré: $GLOBAL_SUMMARY${NC}"
            
            title "TOUS LES SCANS TERMINÉS AVEC SUCCÈS"
            echo -e "${GREEN}Tous les scans de sécurité ont été exécutés avec succès.${NC}"
            echo -e "${GREEN}Aucune vulnérabilité critique bloquante détectée.${NC}"
            ;;
        "quit")
            echo -e "${YELLOW}Au revoir!${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}Option invalide. Veuillez réessayer.${NC}"
            ;;
    esac
done

echo ""
echo -e "${YELLOW}Pour exécuter ce script: ./scripts/security/run-security-scans.sh${NC}"
