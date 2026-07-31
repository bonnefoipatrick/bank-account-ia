package com.bankaccount.domain.repository

import com.bankaccount.domain.model.Account
import java.util.UUID

/**
 * Interface du repository pour les comptes bancaires.
 * Définit les opérations de persistance sans dépendre de l'implémentation technique.
 */
interface AccountRepository {
    
    /**
     * Sauvegarde un compte.
     */
    fun save(account: Account): Account

    /**
     * Récupère un compte par son ID.
     */
    fun findById(id: UUID): Account?

    /**
     * Récupère un compte par son numéro de compte.
     */
    fun findByAccountNumber(accountNumber: String): Account?

    /**
     * Récupère tous les comptes d'un client.
     */
    fun findAllByCustomerId(customerId: UUID): List<Account>

    /**
     * Supprime un compte par son ID.
     */
    fun deleteById(id: UUID)

    /**
     * Vérifie si un numéro de compte existe déjà.
     */
    fun existsByAccountNumber(accountNumber: String): Boolean
}
