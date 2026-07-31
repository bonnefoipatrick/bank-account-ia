plugins {
    kotlin("jvm") version "1.9.20"
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
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // Dépendance sur le module domain
    implementation(project(":domain"))
    
    // Spring Boot (pour les annotations @Component, @Service, etc.)
    implementation("org.springframework.boot:spring-boot-starter:3.2.0")
    
    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.0")
    
    // Jackson pour la sérialisation
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    
    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    
    // Mockito
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    
    // AssertJ
    testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
