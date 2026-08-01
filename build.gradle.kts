// Build script racine pour le projet bank-account-ia
// Ce fichier est optionnel car le projet bank-account a son propre build.gradle.kts

plugins {
    id("org.springframework.boot") version "4.0.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    kotlin("jvm") version "2.4.10" apply false
    kotlin("plugin.spring") version "2.4.10" apply false
    kotlin("plugin.jpa") version "2.4.10" apply false
}
