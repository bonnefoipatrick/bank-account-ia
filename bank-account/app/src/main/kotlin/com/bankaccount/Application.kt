package com.bankaccount

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.kafka.annotation.EnableKafka

/**
 * Point d'entrée de l'application Spring Boot.
 * Utilise Clean Architecture avec :
 * - Domain Layer : Logique métiers pure
 * - Application Layer : Cas d'usage et DTOs
 * - Infrastructure Layer : Persistance (JPA), Messagerie (Kafka), API (REST)
 * - Presentation Layer : Contrôleurs REST
 */
@SpringBootApplication
@ComponentScan(basePackages = [
    "com.bankaccount.domain",
    "com.bankaccount.application", 
    "com.bankaccount.infrastructure"
])
@EntityScan(basePackages = ["com.bankaccount.infrastructure.persistence.entity"])
@EnableJpaRepositories(basePackages = ["com.bankaccount.infrastructure.persistence"])
@EnableKafka
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
