import org.springframework.boot.gradle.tasks.bundling.BootJar

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
    // D&pendance sur le module infrastructure
    implementation(project(":infrastructure"))
}

// Configuration pour Spring Boot
springBoot {
    mainClass.set("com.bankaccount.ApplicationKt")
}


kotlin {
    jvmToolchain(25)
}

tasks.register("explodedJar", Copy::class) {
    layout.buildDirectory.dir("exploded")
}

tasks.named<BootJar>("bootJar") {
    archiveFileName.set("${project.parent?.name}-${project.version}.${archiveExtension.get()}")
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.withType<Test> {
    useJUnitPlatform()
}
