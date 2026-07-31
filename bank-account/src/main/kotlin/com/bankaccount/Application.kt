package com.bankaccount

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Point d'entrée de l'application Spring Boot.
 * Utilise Clean Architecture avec :
 * - Domain Layer : Logique métiers pure
 * - Application Layer : Cas d'usage et DTOs
 * - Infrastructure Layer : Persistance (JPA), Messagerie (Kafka), API (REST)
 * - Presentation Layer : Contrôleurs REST
 */
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
