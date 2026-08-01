// Projet racine - Configuration commune
plugins {
    // Apply the shared build logic from a convention plugin.
    id("kotlin-conventions")
    id("testing-conventions")
    id("dokka-conventions")
//  id("publishing-conventions") // If everything was configured correctly, you could use it to publish the artifacts. But it is not working with Spring as I thought.
    id("spring-conventions")
    // Apply the Application plugin to add support for building an executable JVM application.
    //application
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

}
