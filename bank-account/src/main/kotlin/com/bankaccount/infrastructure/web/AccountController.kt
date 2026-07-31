package com.bankaccount.infrastructure.web

import com.bankaccount.application.dto.*
import com.bankaccount.application.mapper.AccountMapper
import com.bankaccount.application.mapper.TransactionMapper
import com.bankaccount.domain.service.AccountService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Contrôleur REST pour la gestion des comptes bancaires.
 */
@RestController
@RequestMapping("/api/v1/accounts")
class AccountController(
    private val accountService: AccountService,
    private val accountMapper: AccountMapper,
    private val transactionMapper: TransactionMapper
) {

    /**
     * Crée un nouveau compte.
     */
    @PostMapping
    fun createAccount(
        @Valid @RequestBody request: CreateAccountRequest
    ): ResponseEntity<CreateAccountResponse> {
        val account = accountService.createAccount(
            customerId = request.customerId,
            accountNumber = request.accountNumber,
            initialBalance = request.initialBalance,
            currency = request.currency
        )

        val response = CreateAccountResponse(
            account = accountMapper.toDto(account),
            message = "Account created successfully"
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * Récupère un compte par son ID.
     */
    @GetMapping("/{id}")
    fun getAccountById(
        @PathVariable id: UUID
    ): ResponseEntity<AccountDto> {
        val account = accountService.getAccountById(id)
        return ResponseEntity.ok(accountMapper.toDto(account))
    }

    /**
     * Récupère un compte par son numéro.
     */
    @GetMapping("/number/{accountNumber}")
    fun getAccountByNumber(
        @PathVariable accountNumber: String
    ): ResponseEntity<AccountDto> {
        val account = accountService.getAccountByNumber(accountNumber)
        return ResponseEntity.ok(accountMapper.toDto(account))
    }

    /**
     * Récupère tous les comptes d'un client.
     */
    @GetMapping("/customer/{customerId}")
    fun getAccountsByCustomer(
        @PathVariable customerId: UUID
    ): ResponseEntity<List<AccountDto>> {
        val accounts = accountService.getAccountsByCustomer(customerId)
        return ResponseEntity.ok(accountMapper.toDtoList(accounts))
    }

    /**
     * Effectue un dépôt sur un compte.
     */
    @PostMapping("/{accountId}/deposit")
    fun deposit(
        @PathVariable accountId: UUID,
        @Valid @RequestBody request: CreateTransactionRequest
    ): ResponseEntity<TransactionResponse> {
        val (account, transaction) = accountService.deposit(
            accountId = accountId,
            amount = request.amount,
            description = request.description
        )

        val response = TransactionResponse(
            account = accountMapper.toDto(account),
            transaction = transactionMapper.toDto(transaction),
            message = "Deposit successful"
        )

        return ResponseEntity.ok(response)
    }

    /**
     * Effectue un retrait sur un compte.
     */
    @PostMapping("/{accountId}/withdraw")
    fun withdraw(
        @PathVariable accountId: UUID,
        @Valid @RequestBody request: CreateTransactionRequest
    ): ResponseEntity<TransactionResponse> {
        val (account, transaction) = accountService.withdraw(
            accountId = accountId,
            amount = request.amount,
            description = request.description
        )

        val response = TransactionResponse(
            account = accountMapper.toDto(account),
            transaction = transactionMapper.toDto(transaction),
            message = "Withdrawal successful"
        )

        return ResponseEntity.ok(response)
    }

    /**
     * Effectue un virement entre deux comptes.
     */
    @PostMapping("/transfer")
    fun transfer(
        @Valid @RequestBody request: TransferRequest
    ): ResponseEntity<TransferResponse> {
        val (fromAccount, toAccount) = accountService.transfer(
            fromAccountId = request.fromAccountId,
            toAccountId = request.toAccountId,
            amount = request.amount,
            description = request.description
        )

        val response = TransferResponse(
            fromAccount = accountMapper.toDto(fromAccount),
            toAccount = accountMapper.toDto(toAccount),
            message = "Transfer successful"
        )

        return ResponseEntity.ok(response)
    }

    /**
     * Désactive un compte.
     */
    @PatchMapping("/{id}/deactivate")
    fun deactivateAccount(
        @PathVariable id: UUID
    ): ResponseEntity<AccountDto> {
        val account = accountService.deactivateAccount(id)
        return ResponseEntity.ok(accountMapper.toDto(account))
    }

    /**
     * Récupère l'historique des transactions d'un compte.
     */
    @GetMapping("/{accountId}/transactions")
    fun getAccountTransactions(
        @PathVariable accountId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<TransactionDto>> {
        val transactions = accountService.getAccountTransactions(accountId, page, size)
        return ResponseEntity.ok(transactionMapper.toDtoList(transactions))
    }
}
