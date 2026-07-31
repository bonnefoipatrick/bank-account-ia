package com.bankaccount.infrastructure.persistence

import com.bankaccount.infrastructure.persistence.entity.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository JPA pour les comptes bancaires.
 */
@Repository
interface AccountJpaRepository : JpaRepository<AccountEntity, UUID> {
    
    /**
     * Trouve un compte par son numéro de compte.
     */
    fun findByAccountNumber(accountNumber: String): AccountEntity?

    /**
     * Trouve tous les comptes d'un client.
     */
    fun findAllByCustomerId(customerId: UUID): List<AccountEntity>

    /**
     * Vérifie si un numéro de compte existe.
     */
    fun existsByAccountNumber(accountNumber: String): Boolean
}
