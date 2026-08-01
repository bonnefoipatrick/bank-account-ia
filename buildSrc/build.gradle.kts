import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main'
    // that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
        name = "confluent builds"
    }
    maven {
        url = uri("https://plugins.gradle.org/m2/")
        name="gradle plugin m2"
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(
            JavaLanguageVersion.of(libs.versions.jdk.get())
        )
    }
    compilerOptions {
        @Suppress("SpellCheckingInspection")
        freeCompilerArgs.add("-Xjsr305=strict")

        freeCompilerArgs.add("-Xannotation-default-target=param-property")
        allWarningsAsErrors = true

        jvmTarget.set(JvmTarget.valueOf("JVM_${libs.versions.jdk.get()}"))
        languageVersion.set(
            KotlinVersion.valueOf(
                "KOTLIN_${
                    libs.versions.kotlin.get().substringBeforeLast(".").replace(".", "_")
                }"
            )
        )
        apiVersion.set(
            KotlinVersion.valueOf(
                "KOTLIN_${
                    libs.versions.kotlin.get().substringBeforeLast(".").replace(".", "_")
                }"
            )
        )
    }
}

dependencies {
    // buildSrc in combination with this plugin ensures that the version set here
    // will be set to the same for all other Kotlin dependencies / plugins in the project.
    implementation(libs.kotlin.gradlePlugin)

    // https://kotlinlang.org/docs/all-open-plugin.html
    // contains also https://kotlinlang.org/docs/all-open-plugin.html#spring-support
    // The all-open compiler plugin adapts Kotlin to the requirements of those frameworks and makes classes annotated
    // with a specific annotation and their members open without the explicit open keyword.
    implementation(libs.kotlin.allopenPlugin)

    // https://kotlinlang.org/docs/no-arg-plugin.html
    // contains also https://kotlinlang.org/docs/no-arg-plugin.html#jpa-support
    // The no-arg compiler plugin generates an additional zero-argument constructor for classes
    // with a specific annotation.
    implementation(libs.kotlin.noargPlugin)

    // https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/
    // The Spring Boot Gradle Plugin provides Spring Boot support in Gradle.
    // It allows you to package executable jar or war archives, run Spring Boot applications,
    // and use the dependency management provided by spring-boot-dependencies
    implementation(libs.springBoot.gradlePlugin)

    // https://github.com/Kotlin/dokka
    // Dokka is a documentation engine for Kotlin like JavaDoc for Java
    implementation(libs.dokka.gradlePlugin)

    implementation(libs.spring.dependencyManagementPlugin)

    // https://detekt.dev/docs/gettingstarted/gradle/
    // A static code analyzer for Kotlin
    //implementation(libs.detekt.gradlePlugin)
}

layout.buildDirectory.set(project.layout.projectDirectory.dir("${rootDir}/build"))