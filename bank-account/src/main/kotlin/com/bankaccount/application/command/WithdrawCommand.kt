package com.bankaccount.application.command

import java.math.BigDecimal
import java.util.UUID

/**
 * Commande Kafka pour un retrait sur un compte.
 */
data class WithdrawCommand(
    val accountId: UUID,
    val amount: BigDecimal,
    val description: String = "Withdrawal"
)
