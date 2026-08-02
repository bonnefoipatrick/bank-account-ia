# Build stage
# Utilisation de Java 25 avec Eclipse Temurin
FROM eclipse-temurin:25-jdk-jammy as build
WORKDIR /workspace

# Copier les fichiers Gradle
COPY bank-account/build.gradle.kts ./bank-account/
COPY bank-account/settings.gradle.kts ./bank-account/
COPY build.gradle.kts ./
COPY settings.gradle.kts ./

# Copier tous les modules
COPY bank-account/domain ./bank-account/domain/
COPY bank-account/application ./bank-account/application/
COPY bank-account/infrastructure ./bank-account/infrastructure/
COPY bank-account/app ./bank-account/app/

# Builder l'application avec Gradle
# Note: Spring Boot 4.0.0 et Kotlin 2.4.10 nécessitent Java 25
RUN cd bank-account && \
    ./gradlew :app:bootJar --no-daemon --stacktrace --java-home=/usr/lib/jvm/eclipse-temurin-25-jdk-jammy

# Runtime stage
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

# Copier le JAR construit
COPY --from=build /workspace/bank-account/app/build/libs/*.jar app.jar

# Créeer un utilisateur non-root pour la sécurité9
RUN useradd -r -m -U -d /app -s /bin/false appuser && \
    chown -R appuser:appuser /app

# Exposer le port de l'application
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Démarrer l'application
USER appuser
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
