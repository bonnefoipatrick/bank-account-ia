package com.bankaccount.infrastructure.persistence

import com.bankaccount.infrastructure.persistence.entity.TransactionEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository JPA pour les transactions.
 */
@Repository
interface TransactionJpaRepository : JpaRepository<TransactionEntity, UUID> {
    
    /**
     * Trouve toutes les transactions d'un compte.
     */
    fun findAllByAccountId(accountId: UUID): List<TransactionEntity>

    /**
     * Trouve les transactions d'un compte avec pagination.
     */
    fun findAllByAccountId(accountId: UUID, pageable: Pageable): List<TransactionEntity>
}
