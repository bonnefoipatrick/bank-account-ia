package com.bankaccount.domain.repository

import com.bankaccount.domain.model.Transaction
import java.util.UUID

/**
 * Interface du repository pour les transactions.
 * Définit les opérations de persistance sans dépendre de l'implémentation technique.
 */
interface TransactionRepository {
    
    /**
     * Sauvegarde une transaction.
     */
    fun save(transaction: Transaction): Transaction

    /**
     * Récupère une transaction par son ID.
     */
    fun findById(id: UUID): Transaction?

    /**
     * Récupère toutes les transactions d'un compte.
     */
    fun findAllByAccountId(accountId: UUID): List<Transaction>

    /**
     * Récupère les transactions d'un compte avec pagination.
     */
    fun findAllByAccountId(accountId: UUID, page: Int, size: Int): List<Transaction>

    /**
     * Supprime une transaction par son ID.
     */
    fun deleteById(id: UUID)
}
