plugins {
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
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // Dpendance sur les autres modules
    implementation(project(":domain"))
    implementation(project(":application"))
    
    // Spring Boot (version 4.0.0) - les versions sont grs par le plugin dependency-management
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // Kafka
    implementation("org.springframework.boot:spring-boot-starter-kafka")
    
    // Database (H2) - version compatible avec Spring Boot 4.0
    runtimeOnly("com.h2database:h2")
    
    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // spring-kafka-test est inclus dans spring-boot-starter-kafka, pas besoin de l'ajouter s9par9ment
    
    // Spring Security Test
    testImplementation("org.springframework.security:spring-security-test")
    
    // Mockito
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito.kotlin:mockito-kotlin")
    
    // AssertJ
    testImplementation("org.assertj:assertj-core")
    
    // Cucumber pour les tests BDD
    testImplementation("io.cucumber:cucumber-java")
    testImplementation("io.cucumber:cucumber-junit")
    testImplementation("io.cucumber:cucumber-spring")
    
    // RestAssured pour les tests API
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.rest-assured:json-path")
    testImplementation("io.rest-assured:xml-path")
    
    // JSON Assert pour les tests API
    testImplementation("org.skyscreamer:jsonassert")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
