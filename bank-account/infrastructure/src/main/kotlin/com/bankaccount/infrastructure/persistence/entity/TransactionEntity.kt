package com.bankaccount.infrastructure.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Entité JPA pour la persistance des transactions.
 */
@Entity
@Table(name = "transactions")
data class TransactionEntity(
    @Id
    @GeneratedValue(generator = "UUID")
    val id: UUID? = null,

    @Column(name = "account_id", nullable = false)
    val accountId: UUID,

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    val amount: BigDecimal,

    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val type: TransactionTypeEntity,

    @Column(name = "description", nullable = false, length = 255)
    val description: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "reference", length = 100)
    val reference: String? = null
) {
    // Constructeur secondaire pour JPA
    constructor() : this(
        id = null,
        accountId = UUID.randomUUID(),
        amount = BigDecimal.ZERO,
        type = TransactionTypeEntity.DEPOSIT,
        description = ""
    )
}

/**
 * Énumération pour les types de transactions en base de données.
 */
enum class TransactionTypeEntity {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_IN,
    TRANSFER_OUT
}
