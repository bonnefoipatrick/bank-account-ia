package com.bankaccount.application.dto

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * DTO pour la représentation d'une transaction dans l'API.
 */
data class TransactionDto(
    val id: UUID,
    val accountId: UUID,
    val amount: BigDecimal,
    val type: String,  // TransactionType.name
    val description: String,
    val createdAt: LocalDateTime,
    val reference: String? = null
)

/**
 * DTO pour la création d'une transaction (dépôt/retrait).
 */
data class CreateTransactionRequest(
    val amount: BigDecimal,
    val type: String,  // "DEPOSIT" ou "WITHDRAWAL"
    val description: String = "",
    val reference: String? = null
)

/**
 * DTO pour un virement.
 */
data class TransferRequest(
    val fromAccountId: UUID,
    val toAccountId: UUID,
    val amount: BigDecimal,
    val description: String = "Transfer"
)

/**
 * DTO pour la réponse après une transaction.
 */
data class TransactionResponse(
    val account: AccountDto,
    val transaction: TransactionDto,
    val message: String
)

/**
 * DTO pour la réponse après un virement.
 */
data class TransferResponse(
    val fromAccount: AccountDto,
    val toAccount: AccountDto,
    val message: String
)
