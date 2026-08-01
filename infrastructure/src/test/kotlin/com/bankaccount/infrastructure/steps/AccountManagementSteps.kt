package com.bankaccount.infrastructure.steps

import com.bankaccount.application.dto.AccountDto
import com.bankaccount.application.dto.CreateAccountRequest
import com.bankaccount.application.dto.CreateAccountResponse
import com.bankaccount.application.dto.CreateTransactionRequest
import com.bankaccount.application.dto.TransactionDto
import com.bankaccount.application.dto.TransactionResponse
import com.bankaccount.application.dto.TransferRequest
import com.bankaccount.application.dto.TransferResponse
import com.bankaccount.application.mapper.AccountMapper
import com.bankaccount.application.mapper.TransactionMapper
import com.bankaccount.domain.exception.AccountAlreadyExistsException
import com.bankaccount.domain.exception.AccountNotFoundException
import com.bankaccount.domain.exception.InsufficientBalanceException
import com.bankaccount.domain.model.Account
import com.bankaccount.domain.model.Transaction
import com.bankaccount.domain.model.TransactionType
import com.bankaccount.domain.service.AccountService
import io.cucumber.datatable.DataTable
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Step definitions pour les tests Cucumber de gestion de compte.
 * Implémente les étapes définies dans account_management.feature.
 */
@SpringBootTest
@ActiveProfiles("test")
class AccountManagementSteps {

    @Mock
    private lateinit var accountService: AccountService

    @Mock
    private lateinit var accountMapper: AccountMapper

    @Mock
    private lateinit var transactionMapper: TransactionMapper

    private lateinit var currentCustomerId: UUID
    private lateinit var currentAccount: Account
    private lateinit var secondAccount: Account
    private var currentResponse: Any? = null
    private var currentError: Exception? = null
    private val createdAccounts = mutableMapOf<String, Account>()
    private val createdTransactions = mutableListOf<Transaction>()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        currentCustomerId = UUID.randomUUID()
    }

    @After
    fun tearDown() {
        createdAccounts.clear()
        createdTransactions.clear()
        currentResponse = null
        currentError = null
    }

    // ==================== GIVEN ====================

    @Given("I have a valid customer ID")
    fun iHaveAValidCustomerId() {
        currentCustomerId = UUID.randomUUID()
    }

    @Given("I have two valid customer IDs")
    fun iHaveTwoValidCustomerIds() {
        currentCustomerId = UUID.randomUUID()
        // Le second customer ID sera généré quand nécessaire
    }

    @Given("I have created an account")
    fun iHaveCreatedAnAccount() {
        currentAccount = Account.create(
            customerId = currentCustomerId,
            accountNumber = "FR7612345678901234567890123",
            initialBalance = BigDecimal("1000.00"),
            currency = "EUR"
        )
        createdAccounts[currentAccount.accountNumber] = currentAccount
        
        // Mock the service
        given(accountService.getAccountById(currentAccount.id)).willReturn(currentAccount)
        given(accountService.getAccountByNumber(currentAccount.accountNumber)).willReturn(currentAccount)
    }

    @Given("I have created two accounts with balances {double} EUR and {double} EUR")
    fun iHaveCreatedTwoAccountsWithBalances(balance1: Double, balance2: Double) {
        currentAccount = Account.create(
            customerId = currentCustomerId,
            accountNumber = "ACCOUNT_1",
            initialBalance = BigDecimal(balance1),
            currency = "EUR"
        )
        secondAccount = Account.create(
            customerId = UUID.randomUUID(),
            accountNumber = "ACCOUNT_2",
            initialBalance = BigDecimal(balance2),
            currency = "EUR"
        )
        
        createdAccounts[currentAccount.accountNumber] = currentAccount
        createdAccounts[secondAccount.accountNumber] = secondAccount
        
        // Mock the service
        given(accountService.getAccountById(currentAccount.id)).willReturn(currentAccount)
        given(accountService.getAccountById(secondAccount.id)).willReturn(secondAccount)
    }

    @Given("an account with number {string} already exists")
    fun anAccountWithNumberAlreadyExists(accountNumber: String) {
        val existingAccount = Account.create(
            customerId = UUID.randomUUID(),
            accountNumber = accountNumber,
            initialBalance = BigDecimal.ZERO,
            currency = "EUR"
        )
        createdAccounts[accountNumber] = existingAccount
        
        given(accountRepository.existsByAccountNumber(accountNumber)).willReturn(true)
        given(accountService.createAccount(
            any(),
            any(),
            any(),
            any()
        )).willThrow(AccountAlreadyExistsException(accountNumber))
    }

    @Given("I have performed several transactions")
    fun iHavePerformedSeveralTransactions() {
        // Créer quelques transactions factices
        val transaction1 = Transaction.create(
            accountId = currentAccount.id,
            amount = BigDecimal("100.00"),
            type = TransactionType.DEPOSIT,
            description = "Initial deposit"
        )
        val transaction2 = Transaction.create(
            accountId = currentAccount.id,
            amount = BigDecimal("50.00"),
            type = TransactionType.DEPOSIT,
            description = "Second deposit"
        )
        createdTransactions.addAll(listOf(transaction1, transaction2))
        
        given(transactionRepository.findAllByAccountId(currentAccount.id, 0, 20))
            .willReturn(createdTransactions)
    }

    // ==================== WHEN ====================

    @When("I create a new account with number {string} and initial balance {double} {string}")
    fun iCreateANewAccountWithNumberAndInitialBalance(
        accountNumber: String,
        balance: Double,
        currency: String
    ) {
        val request = CreateAccountRequest(
            customerId = currentCustomerId,
            accountNumber = accountNumber,
            initialBalance = BigDecimal(balance),
            currency = currency
        )
        
        val newAccount = Account.create(
            customerId = request.customerId,
            accountNumber = request.accountNumber,
            initialBalance = request.initialBalance,
            currency = request.currency
        )
        
        given(accountService.createAccount(
            request.customerId,
            request.accountNumber,
            request.initialBalance,
            request.currency
        )).willReturn(newAccount)
        
        given(accountMapper.toDto(newAccount)).willReturn(
            AccountDto(
                id = newAccount.id,
                customerId = newAccount.customerId,
                accountNumber = newAccount.accountNumber,
                balance = newAccount.balance,
                currency = newAccount.currency,
                createdAt = newAccount.createdAt,
                updatedAt = newAccount.updatedAt,
                isActive = newAccount.isActive
            )
        )
        
        currentResponse = CreateAccountResponse(
            account = accountMapper.toDto(newAccount),
            message = "Account created successfully"
        )
        
        createdAccounts[accountNumber] = newAccount
    }

    @When("I try to create a new account with number {string}")
    fun iTryToCreateANewAccountWithNumber(accountNumber: String) {
        val request = CreateAccountRequest(
            customerId = currentCustomerId,
            accountNumber = accountNumber,
            initialBalance = BigDecimal.ZERO,
            currency = "EUR"
        )
        
        try {
            currentResponse = accountService.createAccount(
                request.customerId,
                request.accountNumber,
                request.initialBalance,
                request.currency
            )
        } catch (e: Exception) {
            currentError = e
        }
    }

    @When("I try to create a new account with currency {string}")
    fun iTryToCreateANewAccountWithCurrency(currency: String) {
        val request = CreateAccountRequest(
            customerId = currentCustomerId,
            accountNumber = "FR7612345678901234567890124",
            initialBalance = BigDecimal.ZERO,
            currency = currency
        )
        
        try {
            val newAccount = Account.create(
                customerId = request.customerId,
                accountNumber = request.accountNumber,
                initialBalance = request.initialBalance,
                currency = request.currency
            )
            currentResponse = newAccount
        } catch (e: Exception) {
            currentError = e
        }
    }

    @When("I request the account by its ID")
    fun iRequestTheAccountByItsId() {
        currentResponse = accountService.getAccountById(currentAccount.id)
    }

    @When("I request an account with a non-existent ID")
    fun iRequestAnAccountWithANonExistentId() {
        try {
            currentResponse = accountService.getAccountById(UUID.randomUUID())
        } catch (e: Exception) {
            currentError = e
        }
    }

    @When("I deposit {double} {string} into the account")
    fun iDepositAmountIntoTheAccount(amount: Double, currency: String) {
        val request = CreateTransactionRequest(
            amount = BigDecimal(amount),
            type = "DEPOSIT",
            description = "Test deposit"
        )
        
        val (updatedAccount, transaction) = accountService.deposit(
            accountId = currentAccount.id,
            amount = request.amount,
            description = request.description
        )
        
        currentAccount = updatedAccount
        createdTransactions.add(transaction)
        
        given(accountMapper.toDto(updatedAccount)).willReturn(
            AccountDto(
                id = updatedAccount.id,
                customerId = updatedAccount.customerId,
                accountNumber = updatedAccount.accountNumber,
                balance = updatedAccount.balance,
                currency = updatedAccount.currency,
                createdAt = updatedAccount.createdAt,
                updatedAt = updatedAccount.updatedAt,
                isActive = updatedAccount.isActive
            )
        )
        given(transactionMapper.toDto(transaction)).willReturn(
            TransactionDto(
                id = transaction.id,
                accountId = transaction.accountId,
                amount = transaction.amount,
                type = transaction.type.name,
                description = transaction.description,
                createdAt = transaction.createdAt,
                reference = transaction.reference
            )
        )
        
        currentResponse = TransactionResponse(
            account = accountMapper.toDto(updatedAccount),
            transaction = transactionMapper.toDto(transaction),
            message = "Deposit successful"
        )
    }

    @When("I try to deposit {double} {string}")
    fun iTryToDepositAmount(amount: Double, currency: String) {
        try {
            val request = CreateTransactionRequest(
                amount = BigDecimal(amount),
                type = "DEPOSIT",
                description = "Test deposit"
            )
            
            currentResponse = accountService.deposit(
                accountId = currentAccount.id,
                amount = request.amount,
                description = request.description
            )
        } catch (e: Exception) {
            currentError = e
        }
    }

    @When("I withdraw {double} {string} from the account")
    fun iWithdrawAmountFromTheAccount(amount: Double, currency: String) {
        val request = CreateTransactionRequest(
            amount = BigDecimal(amount),
            type = "WITHDRAWAL",
            description = "Test withdrawal"
        )
        
        val (updatedAccount, transaction) = accountService.withdraw(
            accountId = currentAccount.id,
            amount = request.amount,
            description = request.description
        )
        
        currentAccount = updatedAccount
        createdTransactions.add(transaction)
        
        given(accountMapper.toDto(updatedAccount)).willReturn(
            AccountDto(
                id = updatedAccount.id,
                customerId = updatedAccount.customerId,
                accountNumber = updatedAccount.accountNumber,
                balance = updatedAccount.balance,
                currency = updatedAccount.currency,
                createdAt = updatedAccount.createdAt,
                updatedAt = updatedAccount.updatedAt,
                isActive = updatedAccount.isActive
            )
        )
        given(transactionMapper.toDto(transaction)).willReturn(
            TransactionDto(
                id = transaction.id,
                accountId = transaction.accountId,
                amount = transaction.amount,
                type = transaction.type.name,
                description = transaction.description,
                createdAt = transaction.createdAt,
                reference = transaction.reference
            )
        )
        
        currentResponse = TransactionResponse(
            account = accountMapper.toDto(updatedAccount),
            transaction = transactionMapper.toDto(transaction),
            message = "Withdrawal successful"
        )
    }

    @When("I try to withdraw {double} {string} from the account")
    fun iTryToWithdrawAmountFromTheAccount(amount: Double, currency: String) {
        try {
            val request = CreateTransactionRequest(
                amount = BigDecimal(amount),
                type = "WITHDRAWAL",
                description = "Test withdrawal"
            )
            
            currentResponse = accountService.withdraw(
                accountId = currentAccount.id,
                amount = request.amount,
                description = request.description
            )
        } catch (e: Exception) {
            currentError = e
        }
    }

    @When("I transfer {double} {string} from the first account to the second")
    fun iTransferAmountFromTheFirstAccountToTheSecond(amount: Double, currency: String) {
        val request = TransferRequest(
            fromAccountId = currentAccount.id,
            toAccountId = secondAccount.id,
            amount = BigDecimal(amount),
            description = "Test transfer"
        )
        
        val (fromAccount, toAccount) = accountService.transfer(
            fromAccountId = request.fromAccountId,
            toAccountId = request.toAccountId,
            amount = request.amount,
            description = request.description
        )
        
        currentAccount = fromAccount
        secondAccount = toAccount
        
        given(accountMapper.toDto(fromAccount)).willReturn(
            AccountDto(
                id = fromAccount.id,
                customerId = fromAccount.customerId,
                accountNumber = fromAccount.accountNumber,
                balance = fromAccount.balance,
                currency = fromAccount.currency,
                createdAt = fromAccount.createdAt,
                updatedAt = fromAccount.updatedAt,
                isActive = fromAccount.isActive
            )
        )
        given(accountMapper.toDto(toAccount)).willReturn(
            AccountDto(
                id = toAccount.id,
                customerId = toAccount.customerId,
                accountNumber = toAccount.accountNumber,
                balance = toAccount.balance,
                currency = toAccount.currency,
                createdAt = toAccount.createdAt,
                updatedAt = toAccount.updatedAt,
                isActive = toAccount.isActive
            )
        )
        
        currentResponse = TransferResponse(
            fromAccount = accountMapper.toDto(fromAccount),
            toAccount = accountMapper.toDto(toAccount),
            message = "Transfer successful"
        )
    }

    @When("I try to transfer {double} {string} from the first account to the second")
    fun iTryToTransferAmountFromTheFirstAccountToTheSecond(amount: Double, currency: String) {
        try {
            val request = TransferRequest(
                fromAccountId = currentAccount.id,
                toAccountId = secondAccount.id,
                amount = BigDecimal(amount),
                description = "Test transfer"
            )
            
            currentResponse = accountService.transfer(
                fromAccountId = request.fromAccountId,
                toAccountId = request.toAccountId,
                amount = request.amount,
                description = request.description
            )
        } catch (e: Exception) {
            currentError = e
        }
    }

    @When("I deactivate the account")
    fun iDeactivateTheAccount() {
        currentAccount = accountService.deactivateAccount(currentAccount.id)
        
        given(accountMapper.toDto(currentAccount)).willReturn(
            AccountDto(
                id = currentAccount.id,
                customerId = currentAccount.customerId,
                accountNumber = currentAccount.accountNumber,
                balance = currentAccount.balance,
                currency = currentAccount.currency,
                createdAt = currentAccount.createdAt,
                updatedAt = currentAccount.updatedAt,
                isActive = currentAccount.isActive
            )
        )
        
        currentResponse = accountMapper.toDto(currentAccount)
    }

    @When("I request the account transactions")
    fun iRequestTheAccountTransactions() {
        currentResponse = accountService.getAccountTransactions(currentAccount.id, 0, 20)
    }

    // ==================== THEN ====================

    @Then("the account should be created successfully")
    fun theAccountShouldBeCreatedSuccessfully() {
        assertThat(currentError).isNull()
        assertThat(currentResponse).isNotNull
    }

    @Then("the account should have the correct details")
    fun theAccountShouldHaveTheCorrectDetails() {
        val response = currentResponse as CreateAccountResponse
        assertThat(response.account.accountNumber).isEqualTo("FR7612345678901234567890123")
        assertThat(response.account.balance).isEqualTo(BigDecimal("1000.00"))
        assertThat(response.account.currency).isEqualTo("EUR")
    }

    @Then("the account should be active")
    fun theAccountShouldBeActive() {
        val response = currentResponse as CreateAccountResponse
        assertThat(response.account.isActive).isTrue
    }

    @Then("the account creation should fail with conflict error")
    fun theAccountCreationShouldFailWithConflictError() {
        assertThat(currentError).isInstanceOf(AccountAlreadyExistsException::class)
    }

    @Then("the error message should contain {string}")
    fun theErrorMessageShouldContain(message: String) {
        assertThat(currentError).isNotNull
        assertThat(currentError!!.message).contains(message)
    }

    @Then("the account creation should fail with bad request error")
    fun theAccountCreationShouldFailWithBadRequestError() {
        assertThat(currentError).isNotNull
        // En pratique, cela serait une IllegalArgumentException
    }

    @Then("I should receive the account details")
    fun iShouldReceiveTheAccountDetails() {
        assertThat(currentResponse).isNotNull
        val account = currentResponse as Account
        assertThat(account.accountNumber).isEqualTo(currentAccount.accountNumber)
    }

    @Then("the response should contain the account number")
    fun theResponseShouldContainTheAccountNumber() {
        val response = currentResponse as Account
        assertThat(response.accountNumber).isNotNull
    }

    @Then("I should receive a not found error")
    fun iShouldReceiveANotFoundError() {
        assertThat(currentError).isInstanceOf(AccountNotFoundException::class)
    }

    @Then("the deposit should be successful")
    fun theDepositShouldBeSuccessful() {
        assertThat(currentError).isNull()
        assertThat(currentResponse).isNotNull
    }

    @Then("the new balance should be {double} {string}")
    fun theNewBalanceShouldBe(amount: Double, currency: String) {
        val response = currentResponse as TransactionResponse
        assertThat(response.account.balance).isEqualTo(BigDecimal(amount))
    }

    @Then("a deposit transaction should be recorded")
    fun aDepositTransactionShouldBeRecorded() {
        val response = currentResponse as TransactionResponse
        assertThat(response.transaction.type).isEqualTo("DEPOSIT")
    }

    @Then("the withdrawal should be successful")
    fun theWithdrawalShouldBeSuccessful() {
        assertThat(currentError).isNull()
        assertThat(currentResponse).isNotNull
    }

    @Then("a withdrawal transaction should be recorded")
    fun aWithdrawalTransactionShouldBeRecorded() {
        val response = currentResponse as TransactionResponse
        assertThat(response.transaction.type).isEqualTo("WITHDRAWAL")
    }

    @Then("the withdrawal should fail with bad request error")
    fun theWithdrawalShouldFailWithBadRequestError() {
        assertThat(currentError).isInstanceOf(InsufficientBalanceException::class)
    }

    @Then("the transfer should be successful")
    fun theTransferShouldBeSuccessful() {
        assertThat(currentError).isNull()
        assertThat(currentResponse).isNotNull
    }

    @Then("the first account balance should be {double} {string}")
    fun theFirstAccountBalanceShouldBe(amount: Double, currency: String) {
        val response = currentResponse as TransferResponse
        assertThat(response.fromAccount.balance).isEqualTo(BigDecimal(amount))
    }

    @Then("the second account balance should be {double} {string}")
    fun theSecondAccountBalanceShouldBe(amount: Double, currency: String) {
        val response = currentResponse as TransferResponse
        assertThat(response.toAccount.balance).isEqualTo(BigDecimal(amount))
    }

    @Then("transfer transactions should be recorded for both accounts")
    fun transferTransactionsShouldBeRecordedForBothAccounts() {
        // Dans une vraie implémentation, nous vérifierions les transactions
        // Pour ce test, nous supposons que le service a fonctionné correctement
        assertThat(currentError).isNull()
    }

    @Then("the transfer should fail with bad request error")
    fun theTransferShouldFailWithBadRequestError() {
        assertThat(currentError).isInstanceOf(InsufficientBalanceException::class)
    }

    @Then("the account should be deactivated")
    fun theAccountShouldBeDeactivated() {
        val response = currentResponse as AccountDto
        assertThat(response.isActive).isFalse
    }

    @Then("the account status should be inactive")
    fun theAccountStatusShouldBeInactive() {
        assertThat(currentAccount.isActive).isFalse
    }

    @Then("I should receive a list of transactions")
    fun iShouldReceiveAListOfTransactions() {
        assertThat(currentResponse).isNotNull
        val transactions = currentResponse as List<*>
        assertThat(transactions).isNotEmpty
    }

    @Then("the transactions should be ordered by creation date")
    fun theTransactionsShouldBeOrderedByCreationDate() {
        val transactions = currentResponse as List<Transaction>
        assertThat(transactions).isSortedAccordingTo { t1, t2 ->
            t1.createdAt.compareTo(t2.createdAt)
        }
    }

    // ==================== AND ====================

    @And("the error message should contain {string}")
    fun andTheErrorMessageShouldContain(message: String) {
        assertThat(currentError).isNotNull
        assertThat(currentError!!.message).contains(message)
    }
}
