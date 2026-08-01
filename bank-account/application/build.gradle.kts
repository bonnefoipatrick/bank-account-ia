plugins {
    kotlin("jvm") version "2.4.10"
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
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // Dpendance sur le module domain
    implementation(project(":domain"))
    
    // Spring Boot (pour les annotations @Component, @Service, etc.)
    implementation("org.springframework.boot:spring-boot-starter:4.0.0")
    
    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation:4.0.0")
    
    // Jackson pour la srialisation
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    
    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0")
    
    // Mockito
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    
    // AssertJ
    testImplementation("org.assertj:assertj-core:3.26.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
