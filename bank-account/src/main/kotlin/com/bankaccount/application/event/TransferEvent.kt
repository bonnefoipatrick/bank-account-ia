package com.bankaccount.application.event

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Événement Kafka émis lorsqu'un virement est effectué.
 */
data class TransferEvent(
    val transferId: UUID,
    val fromAccountId: UUID,
    val toAccountId: UUID,
    val amount: BigDecimal,
    val description: String,
    val createdAt: LocalDateTime,
    val fromNewBalance: BigDecimal,
    val toNewBalance: BigDecimal
)
