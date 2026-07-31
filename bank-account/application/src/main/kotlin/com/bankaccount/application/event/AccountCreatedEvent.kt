package com.bankaccount.application.event

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Événement Kafka émis lorsqu'un compte est créé.
 */
data class AccountCreatedEvent(
    val accountId: UUID,
    val customerId: UUID,
    val accountNumber: String,
    val initialBalance: BigDecimal,
    val currency: String,
    val createdAt: LocalDateTime
)
