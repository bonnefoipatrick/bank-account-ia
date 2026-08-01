plugins {
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.spring") version "2.4.10"
    kotlin("plugin.jpa") version "2.4.10"
}

group = "com.bankaccount"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_25
}

repositories {
    mavenCentral()
}

dependencies {
    // Dpendance sur le module infrastructure
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
