plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    kotlin("plugin.jpa") version "1.9.20"
}

group = "com.bankaccount"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // Dépendance sur le module infrastructure
    implementation(project(":infrastructure"))
}

// Configuration pour Spring Boot
springBoot {
    mainClass.set("com.bankaccount.ApplicationKt")
}

jar {
    enabled = false
}

tasks.withType<Test> {
    useJUnitPlatform()
}
