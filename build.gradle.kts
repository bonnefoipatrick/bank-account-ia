// Build script racine pour le projet bank-account-ia
// Ce fichier est optionnel car le projet bank-account a son propre build.gradle.kts

plugins {
    id("org.springframework.boot") version "3.2.0" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    kotlin("jvm") version "1.9.20" apply false
    kotlin("plugin.spring") version "1.9.20" apply false
    kotlin("plugin.jpa") version "1.9.20" apply false
}
