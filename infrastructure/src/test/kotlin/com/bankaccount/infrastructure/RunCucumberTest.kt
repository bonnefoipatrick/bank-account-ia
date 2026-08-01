package com.bankaccount.infrastructure

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

/**
 * Classe pour exécuter les tests Cucumber.
 * Utilise JUnit 5 comme runner.
 */
@RunWith(Cucumber::class)
@CucumberOptions(
    // Chemin vers les fichiers feature
    features = ["classpath:features/bank_account"],
    
    // Chemin vers les step definitions
    glue = ["com.bankaccount.infrastructure.steps"],
    
    // Plugins pour les rapports
    plugin = [
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/cucumber.json",
        "junit:target/cucumber-reports/cucumber.xml",
        "usage:target/cucumber-reports/cucumber-usage.json"
    ],
    
    // Tags à inclure (tous par défaut)
    tags = ["@bank-account"],
    
    // Monochrome pour une sortie plus lisible
    monochrome = true,
    
    // Afficher les étapes passées
    publish = true,
    
    // Dry run pour vérifier les étapes sans exécuter
    dryRun = false,
    
    // Strict mode pour échouer sur les étapes non définies
    strict = true
)
class RunCucumberTest
