plugins {
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
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // Dépendances sur les autres modules
    implementation(project(":domain"))
    implementation(project(":application"))
    
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // Kafka
    implementation("org.springframework.boot:spring-boot-starter-kafka")
    
    // Database (H2)
    runtimeOnly("com.h2database:h2")
    
    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // spring-kafka-test est inclus dans spring-boot-starter-kafka, pas besoin de l'ajouter séparément
    
    // Spring Security Test
    testImplementation("org.springframework.security:spring-security-test")
    
    // Mockito
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    
    // AssertJ
    testImplementation("org.assertj:assertj-core:3.24.2")
    
    // Cucumber pour les tests BDD
    testImplementation("io.cucumber:cucumber-java:7.14.0")
    testImplementation("io.cucumber:cucumber-junit:7.14.0")
    testImplementation("io.cucumber:cucumber-spring:7.14.0")
    
    // RestAssured pour les tests API
    testImplementation("io.rest-assured:rest-assured:5.3.2")
    testImplementation("io.rest-assured:json-path:5.3.2")
    testImplementation("io.rest-assured:xml-path:5.3.2")
    
    // JSON Assert pour les tests API
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
