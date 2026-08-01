package com.bankaccount.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Entité JPA pour la persistance des comptes bancaires.
 */
@Entity
@Table(name = "accounts")
data class AccountEntity(
    @Id
    @GeneratedValue(generator = "UUID")
    val id: UUID? = null,

    @Column(name = "customer_id", nullable = false)
    val customerId: UUID,

    @Column(name = "account_number", nullable = false, unique = true)
    val accountNumber: String,

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    var balance: BigDecimal,

    @Column(name = "currency", nullable = false, length = 3)
    val currency: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) {
    // Constructeur secondaire pour JPA
    constructor() : this(
        id = null,
        customerId = UUID.randomUUID(),
        accountNumber = "",
        balance = BigDecimal.ZERO,
        currency = "EUR"
    )
}
