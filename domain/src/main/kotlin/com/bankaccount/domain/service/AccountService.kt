package com.bankaccount.domain.service

import com.bankaccount.domain.exception.AccountAlreadyExistsException
import com.bankaccount.domain.exception.AccountNotExistingException
import com.bankaccount.domain.exception.AccountNotFoundException
import com.bankaccount.domain.exception.InsufficientBalanceException
import com.bankaccount.domain.model.Account
import com.bankaccount.domain.model.Transaction
import com.bankaccount.domain.model.TransactionType
import com.bankaccount.domain.repository.AccountRepository
import com.bankaccount.domain.repository.TransactionRepository
import java.math.BigDecimal
import java.util.UUID

/**
 * Service métiers pour la gestion des comptes bancaires.
 * Contient la logique métiers pure, sans dépendance à Spring ou Kafka.
 */
class AccountService(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) {

    /**
     * Crée un nouveau compte bancaire.
     */
    fun createAccount(
        customerId: UUID,
        accountNumber: String,
        initialBalance: BigDecimal = BigDecimal.ZERO,
        currency: String = "EUR"
    ): Account {
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw AccountAlreadyExistsException(accountNumber)
        }

        val account = Account.create(
            customerId = customerId,
            accountNumber = accountNumber,
            initialBalance = initialBalance,
            currency = currency
        )

        return accountRepository.save(account)
    }

    /**
     * Récupère un compte par son ID.
     */
    fun getAccountById(id: UUID): Account {
        return accountRepository.findById(id)
            ?: throw AccountNotFoundException(id.toString())
    }

    /**
     * Récupère un compte par son numéro.
     */
    fun getAccountByNumber(accountNumber: String): Account {
        return accountRepository.findByAccountNumber(accountNumber)
            ?: throw AccountNotExistingException(accountNumber)
    }

    /**
     * Récupère tous les comptes d'un client.
     */
    fun getAccountsByCustomer(customerId: UUID): List<Account> {
        return accountRepository.findAllByCustomerId(customerId)
    }

    /**
     * Effectue un dépôt sur un compte.
     */
    fun deposit(
        accountId: UUID,
        amount: BigDecimal,
        description: String = "Deposit"
    ): Pair<Account, Transaction> {
        require(amount > BigDecimal.ZERO) { "Deposit amount must be positive" }

        val account = getAccountById(accountId)
        val newBalance = account.balance + amount

        val updatedAccount = account.withNewBalance(newBalance)
        accountRepository.save(updatedAccount)

        val transaction = Transaction.create(
            accountId = accountId,
            amount = amount,
            type = TransactionType.DEPOSIT,
            description = description
        )
        transactionRepository.save(transaction)

        return Pair(updatedAccount, transaction)
    }

    /**
     * Effectue un retrait sur un compte.
     */
    fun withdraw(
        accountId: UUID,
        amount: BigDecimal,
        description: String = "Withdrawal"
    ): Pair<Account, Transaction> {
        require(amount > BigDecimal.ZERO) { "Withdrawal amount must be positive" }

        val account = getAccountById(accountId)
        if (account.balance < amount) {
            throw InsufficientBalanceException(account.balance, amount)
        }

        val newBalance = account.balance - amount
        val updatedAccount = account.withNewBalance(newBalance)
        accountRepository.save(updatedAccount)

        val transaction = Transaction.create(
            accountId = accountId,
            amount = amount,
            type = TransactionType.WITHDRAWAL,
            description = description
        )
        transactionRepository.save(transaction)

        return Pair(updatedAccount, transaction)
    }

    /**
     * Effectue un virement entre deux comptes.
     */
    fun transfer(
        fromAccountId: UUID,
        toAccountId: UUID,
        amount: BigDecimal,
        description: String = "Transfer"
    ): Pair<Account, Account> {
        require(amount > BigDecimal.ZERO) { "Transfer amount must be positive" }

        val fromAccount = getAccountById(fromAccountId)
        val toAccount = getAccountById(toAccountId)

        if (fromAccount.balance < amount) {
            throw InsufficientBalanceException(fromAccount.balance, amount)
        }

        // Retrait du compte source
        val fromNewBalance = fromAccount.balance - amount
        val updatedFromAccount = fromAccount.withNewBalance(fromNewBalance)
        accountRepository.save(updatedFromAccount)

        // Dépôt sur le compte destination
        val toNewBalance = toAccount.balance + amount
        val updatedToAccount = toAccount.withNewBalance(toNewBalance)
        accountRepository.save(updatedToAccount)

        // Enregistrement des transactions
        val withdrawalTransaction = Transaction.create(
            accountId = fromAccountId,
            amount = amount,
            type = TransactionType.TRANSFER_OUT,
            description = "$description (to ${toAccount.accountNumber})"
        )
        transactionRepository.save(withdrawalTransaction)

        val depositTransaction = Transaction.create(
            accountId = toAccountId,
            amount = amount,
            type = TransactionType.TRANSFER_IN,
            description = "$description (from ${fromAccount.accountNumber})"
        )
        transactionRepository.save(depositTransaction)

        return Pair(updatedFromAccount, updatedToAccount)
    }

    /**
     * Désactive un compte.
     */
    fun deactivateAccount(accountId: UUID): Account {
        val account = getAccountById(accountId)
        val deactivatedAccount = account.deactivate()
        return accountRepository.save(deactivatedAccount)
    }

    /**
     * Récupère l'historique des transactions d'un compte.
     */
    fun getAccountTransactions(
        accountId: UUID,
        page: Int = 0,
        size: Int = 20
    ): List<Transaction> {
        getAccountById(accountId) // Vérifie que le compte existe
        return transactionRepository.findAllByAccountId(accountId, page, size)
    }
}
