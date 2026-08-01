
import gradle.kotlin.dsl.accessors._a1daecb58da665e576c1cdb31f3d7fbb.publishing
import org.gradle.kotlin.dsl.from
import org.jetbrains.kotlin.gradle.fus.internal.isGitLab
import org.jetbrains.kotlin.gradle.targets.js.npm.fromSrcPackageJson
import kotlin.text.set

/*
*
* In theory, you can use this convention to publish a jar file to a maven repository.
* In case of Spring boot, this will fail because of the bootJar with the error message:
*
* Execution failed for task ':message-dashboard:publishMavenPublicationToMavenLocal'.
* > Failed to publish publication 'maven' to repository 'mavenLocal'
*   > Artifact message-dashboard-DEVELOPMENT-SNAPSHOT.jar wasn't produced by this build.
*
* To fix it, you have to configure the plugin as described here:
*
* https://docs.spring.io/spring-boot/docs/2.4.4/gradle-plugin/reference/htmlsingle/#publishing-your-application-maven
*
*/

plugins {
    id("java-conventions")
    id("dokka-conventions")
    `maven-publish`
}

tasks.register<Jar>("sourcesJar") {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaGeneratePublicationHtml"))
    dependsOn(tasks.named("dokkaGeneratePublicationHtml"))

}

publishing {
    publications {
        create<MavenPublication>("javadocJar") {
            from(components["java"])
        }
        create<MavenPublication>("kotlinSourcesJar") {
            from(components["kotlin"])
        }
    }
}

/*
publishing {
    publications {
        fromComponent(components["java"], javadocJar.get())
    }

    repositories {
        gitLab(getGitLabToken(project))
    }
}
*/