package com.bankaccount.infrastructure

import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

/**
 * Configuration Spring pour les tests Cucumber.
 * Permet d'utiliser l'injection de dépendances Spring dans les step definitions.
 */
@TestConfiguration
@CucumberContextConfiguration
class CucumberSpringConfiguration {

    // Vous pouvez ajouter des beans spécifiques pour les tests ici
    // Par exemple, des mocks ou des configurations de test
    
    // @Bean
    // @Primary
    // fun mockAccountService(): AccountService {
    //     return mockk()
    // }
}
