package com.bankaccount.application.event

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Événement Kafka émis lorsqu'une transaction est effectuée.
 */
data class TransactionEvent(
    val transactionId: UUID,
    val accountId: UUID,
    val amount: BigDecimal,
    val type: String,  // TransactionType.name
    val description: String,
    val createdAt: LocalDateTime,
    val newBalance: BigDecimal
)
