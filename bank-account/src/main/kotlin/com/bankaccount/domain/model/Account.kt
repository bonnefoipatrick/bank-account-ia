package com.bankaccount.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Entité métiers représentant un compte bancaire.
 * Immuable pour respecter les principes du Domain-Driven Design (DDD).
 */
data class Account(
    val id: UUID,
    val customerId: UUID,
    val accountNumber: String,
    val balance: BigDecimal,
    val currency: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isActive: Boolean
) {
    init {
        require(accountNumber.isNotBlank()) { "Account number cannot be blank" }
        require(balance >= BigDecimal.ZERO) { "Balance cannot be negative" }
        require(currency.length == 3) { "Currency must be a 3-letter code (ISO 4217)" }
    }

    /**
     * Crée un nouveau compte avec un solde mis à jour.
     */
    fun withNewBalance(newBalance: BigDecimal): Account {
        require(newBalance >= BigDecimal.ZERO) { "Balance cannot be negative" }
        return this.copy(
            balance = newBalance,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * Désactive le compte.
     */
    fun deactivate(): Account {
        return this.copy(
            isActive = false,
            updatedAt = LocalDateTime.now()
        )
    }

    companion object {
        fun create(
            customerId: UUID,
            accountNumber: String,
            initialBalance: BigDecimal = BigDecimal.ZERO,
            currency: String = "EUR"
        ): Account {
            return Account(
                id = UUID.randomUUID(),
                customerId = customerId,
                accountNumber = accountNumber,
                balance = initialBalance,
                currency = currency,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                isActive = true
            )
        }
    }
}
