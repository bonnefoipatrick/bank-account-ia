package com.bankaccount.application.command

import java.math.BigDecimal
import java.util.UUID

/**
 * Commande Kafka pour un dépôt sur un compte.
 */
data class DepositCommand(
    val accountId: UUID,
    val amount: BigDecimal,
    val description: String = "Deposit"
)
