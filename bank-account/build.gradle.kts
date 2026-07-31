// Projet racine - Configuration commune
plugins {
    id("org.springframework.boot") version "3.2.0" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    kotlin("jvm") version "1.9.20" apply false
    kotlin("plugin.spring") version "1.9.20" apply false
    kotlin("plugin.jpa") version "1.9.20" apply false
}

group = "com.bankaccount"
version = "0.0.1-SNAPSHOT"

// Configuration commune pour tous les sous-modules
subprojects {
    group = "com.bankaccount"
    version = "0.0.1-SNAPSHOT"
    
    repositories {
        mavenCentral()
    }
    
    java {
        sourceCompatibility = JavaVersion.VERSION_17
    }
}
