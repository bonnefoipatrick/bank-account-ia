package com.bankaccount.application.dto

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * DTO pour la représentation d'un compte bancaire dans l'API.
 */
data class AccountDto(
    val id: UUID,
    val customerId: UUID,
    val accountNumber: String,
    val balance: BigDecimal,
    val currency: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isActive: Boolean
)

/**
 * DTO pour la création d'un compte.
 */
data class CreateAccountRequest(
    val customerId: UUID,
    val accountNumber: String,
    val initialBalance: BigDecimal = BigDecimal.ZERO,
    val currency: String = "EUR"
)

/**
 * DTO pour la réponse après création d'un compte.
 */
data class CreateAccountResponse(
    val account: AccountDto,
    val message: String = "Account created successfully"
)

/**
 * DTO pour la mise à jour partielle d'un compte.
 */
data class UpdateAccountRequest(
    val isActive: Boolean? = null
)
