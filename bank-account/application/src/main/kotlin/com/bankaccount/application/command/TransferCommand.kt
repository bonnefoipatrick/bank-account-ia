package com.bankaccount.application.command

import java.math.BigDecimal
import java.util.UUID

/**
 * Commande Kafka pour un virement entre deux comptes.
 */
data class TransferCommand(
    val fromAccountId: UUID,
    val toAccountId: UUID,
    val amount: BigDecimal,
    val description: String = "Transfer"
)
