
plugins {
    // Apply the shared build logic from a convention plugin.
    id("kotlin-conventions")
    id("testing-conventions")
    id("dokka-conventions")
//  id("publishing-conventions") // If everything was configured correctly, you could use it to publish the artifacts. But it is not working with Spring as I thought.
    id("spring-conventions")
}
group = "com.bankaccount"
version = "0.0.1-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // Dépendance sur les autres modules
    implementation(project(":domain"))
    implementation(project(":application"))
    
    // Spring Boot (version 4.0.0) - les versions sont gérés par le plugin dependency-management
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
    // spring-kafka-test est inclus dans spring-boot-starter-kafka, pas besoin de l'ajouter séparément
    
    // Spring Security Test
    testImplementation("org.springframework.security:spring-security-test")
    
    // Mockito
    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    
    // AssertJ
    testImplementation("org.assertj:assertj-core")
    
    // Cucumber pour les tests BDD
    testImplementation("io.cucumber:cucumber-java:7.34.4")
    testImplementation("io.cucumber:cucumber-junit:7.34.4")
    testImplementation("io.cucumber:cucumber-spring:7.34.4")
    
    // RestAssured pour les tests API
    testImplementation("io.rest-assured:rest-assured:6.0.1")
    testImplementation("io.rest-assured:json-path:6.0.1")
    testImplementation("io.rest-assured:xml-path:6.0.1")
    
    // JSON Assert pour les tests API
    testImplementation("org.skyscreamer:jsonassert")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
