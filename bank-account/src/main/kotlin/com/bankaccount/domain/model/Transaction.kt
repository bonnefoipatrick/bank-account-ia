package com.bankaccount.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Entité métiers représentant une transaction bancaire.
 * Immuable pour respecter les principes du Domain-Driven Design (DDD).
 */
data class Transaction(
    val id: UUID,
    val accountId: UUID,
    val amount: BigDecimal,
    val type: TransactionType,
    val description: String,
    val createdAt: LocalDateTime,
    val reference: String? = null
) {
    init {
        require(amount != BigDecimal.ZERO) { "Transaction amount cannot be zero" }
        require(description.isNotBlank()) { "Description cannot be blank" }
    }

    companion object {
        fun create(
            accountId: UUID,
            amount: BigDecimal,
            type: TransactionType,
            description: String,
            reference: String? = null
        ): Transaction {
            return Transaction(
                id = UUID.randomUUID(),
                accountId = accountId,
                amount = amount,
                type = type,
                description = description,
                createdAt = LocalDateTime.now(),
                reference = reference
            )
        }
    }
}

/**
 * Types de transactions supportés.
 */
enum class TransactionType {
    DEPOSIT,       // Dépôt
    WITHDRAWAL,    // Retrait
    TRANSFER_IN,   // Virement entrant
    TRANSFER_OUT   // Virement sortant
}
