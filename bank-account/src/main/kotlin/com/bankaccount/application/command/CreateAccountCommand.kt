package com.bankaccount.application.command

import java.math.BigDecimal
import java.util.UUID

/**
 * Commande Kafka pour la création d'un compte bancaire.
 */
data class CreateAccountCommand(
    val customerId: UUID,
    val accountNumber: String,
    val initialBalance: BigDecimal = BigDecimal.ZERO,
    val currency: String = "EUR"
)
