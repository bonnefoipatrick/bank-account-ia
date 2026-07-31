package com.bankaccount.domain.service

import com.bankaccount.domain.exception.AccountAlreadyExistsException
import com.bankaccount.domain.exception.AccountNotFoundException
import com.bankaccount.domain.exception.InsufficientBalanceException
import com.bankaccount.domain.model.Account
import com.bankaccount.domain.model.Transaction
import com.bankaccount.domain.model.TransactionType
import com.bankaccount.domain.repository.AccountRepository
import com.bankaccount.domain.repository.TransactionRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Tests unitaires pour AccountService.
 * Utilise Mockito pour les mocks et AssertJ pour les assertions.
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("AccountService Tests")
class AccountServiceTest {

    @Mock
    private lateinit var accountRepository: AccountRepository

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    @InjectMocks
    private lateinit var accountService: AccountService

    private val testCustomerId = UUID.fromString("10000000-0000-0000-0000-000000000001")
    private val testAccountId = UUID.fromString("20000000-0000-0000-0000-000000000001")
    private val testAccountNumber = "FR7612345678901234567890123"
    private val initialBalance = BigDecimal("1000.00")

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Nested
    @DisplayName("Create Account Tests")
    inner class CreateAccountTests {

        @Test
        @DisplayName("Should create a new account successfully")
        fun `should create a new account successfully`() {
            // Given
            given(accountRepository.existsByAccountNumber(testAccountNumber)).willReturn(false)
            given(accountRepository.save(any())).willAnswer { invocation ->
                invocation.getArgument<Account>(0)
            }

            // When
            val result = accountService.createAccount(
                customerId = testCustomerId,
                accountNumber = testAccountNumber,
                initialBalance = initialBalance,
                currency = "EUR"
            )

            // Then
            assertThat(result).isNotNull
            assertThat(result.customerId).isEqualTo(testCustomerId)
            assertThat(result.accountNumber).isEqualTo(testAccountNumber)
            assertThat(result.balance).isEqualTo(initialBalance)
            assertThat(result.currency).isEqualTo("EUR")
            assertThat(result.isActive).isTrue
            
            verify(accountRepository).save(any())
        }

        @Test
        @DisplayName("Should throw exception when account number already exists")
        fun `should throw exception when account number already exists`() {
            // Given
            given(accountRepository.existsByAccountNumber(testAccountNumber)).willReturn(true)

            // When & Then
            assertThatThrownBy {
                accountService.createAccount(
                    customerId = testCustomerId,
                    accountNumber = testAccountNumber,
                    initialBalance = initialBalance,
                    currency = "EUR"
                )
            }.isInstanceOf(AccountAlreadyExistsException::class)
                .hasMessageContaining(testAccountNumber)
        }

        @Test
        @DisplayName("Should create account with zero balance by default")
        fun `should create account with zero balance by default`() {
            // Given
            given(accountRepository.existsByAccountNumber(testAccountNumber)).willReturn(false)
            given(accountRepository.save(any())).willAnswer { invocation ->
                invocation.getArgument<Account>(0)
            }

            // When
            val result = accountService.createAccount(
                customerId = testCustomerId,
                accountNumber = testAccountNumber
            )

            // Then
            assertThat(result.balance).isEqualTo(BigDecimal.ZERO)
        }
    }

    @Nested
    @DisplayName("Get Account Tests")
    inner class GetAccountTests {

        @Test
        @DisplayName("Should get account by ID successfully")
        fun `should get account by ID successfully`() {
            // Given
            val testAccount = Account.create(
                customerId = testCustomerId,
                accountNumber = testAccountNumber,
                initialBalance = initialBalance
            )
            given(accountRepository.findById(testAccountId)).willReturn(testAccount)

            // When
            val result = accountService.getAccountById(testAccountId)

            // Then
            assertThat(result).isEqualTo(testAccount)
        }

        @Test
        @DisplayName("Should throw exception when account not found by ID")
        fun `should throw exception when account not found by ID`() {
            // Given
            given(accountRepository.findById(testAccountId)).willReturn(null)

            // When & Then
            assertThatThrownBy {
                accountService.getAccountById(testAccountId)
            }.isInstanceOf(AccountNotFoundException::class)
                .hasMessageContaining(testAccountId.toString())
        }

        @Test
        @DisplayName("Should get account by number successfully")
        fun `should get account by number successfully`() {
            // Given
            val testAccount = Account.create(
                customerId = testCustomerId,
                accountNumber = testAccountNumber,
                initialBalance = initialBalance
            )
            given(accountRepository.findByAccountNumber(testAccountNumber)).willReturn(testAccount)

            // When
            val result = accountService.getAccountByNumber(testAccountNumber)

            // Then
            assertThat(result).isEqualTo(testAccount)
        }

        @Test
        @DisplayName("Should throw exception when account not found by number")
        fun `should throw exception when account not found by number`() {
            // Given
            given(accountRepository.findByAccountNumber(testAccountNumber)).willReturn(null)

            // When & Then
            assertThatThrownBy {
                accountService.getAccountByNumber(testAccountNumber)
            }.isInstanceOf(AccountNotFoundException::class)
                .hasMessageContaining(testAccountNumber)
        }
    }

    @Nested
    @DisplayName("Deposit Tests")
    inner class DepositTests {

        @Test
        @DisplayName("Should deposit money successfully")
        fun `should deposit money successfully`() {
            // Given
            val testAccount = Account.create(
                customerId = testCustomerId,
                accountNumber = testAccountNumber,
                initialBalance = initialBalance
            )
            val depositAmount = BigDecimal("500.00")
            
            given(accountRepository.findById(testAccountId)).willReturn(testAccount)
            given(accountRepository.save(any())).willAnswer { invocation ->
                invocation.getArgument<Account>(0)
            }
            given(transactionRepository.save(any())).willAnswer { invocation ->
                invocation.getArgument<Transaction>(0)
            }

            // When
            val result = accountService.deposit(
                accountId = testAccountId,
                amount = depositAmount,
                description = "Test deposit"
            )

            // Then
            assertThat(result.first.balance).isEqualTo(initialBalance + depositAmount)
            assertThat(result.second.amount).isEqualTo(depositAmount)
            assertThat(result.second.type).isEqualTo(TransactionType.DEPOSIT)
            assertThat(result.second.description).isEqualTo("Test deposit")
        }

        @Test
        @DisplayName("Should throw exception for negative deposit amount")
        fun `should throw exception for negative deposit amount`() {
            // Given
            val testAccount = Account.create(
                customerId = testCustomerId,
                accountNumber = testAccountNumber,
                initialBalance = initialBalance
            )
            given(accountRepository.findById(testAccountId)).willReturn(testAccount)

            // When & Then
            assertThatThrownBy {
                accountService.deposit(
                    accountId = testAccountId,
                    amount = BigDecimal("-100.00"),
                    description = "Invalid deposit"
                )
            }.isInstanceOf(IllegalArgumentException::class)
                .hasMessageContaining("positive")
        }

        @Test
        @DisplayName("Should throw exception for zero deposit amount")
        fun `should throw exception for zero deposit amount`() {
            // Given
            val testAccount = Account.create(
                customerId = testCustomerId,
                accountNumber = testAccountNumber,
                initialBalance = initialBalance
            )
            given(accountRepository.findById(testAccountId)).willReturn(testAccount)

            // When & Then
            assertThatThrownBy {
                accountService.deposit(
                    accountId = testAccountId,
                    amount = BigDecimal.ZERO,
                    description = "Zero deposit"
                )
            }.isInstanceOf(IllegalArgumentException::class)
        }
    }

    @Nested
    @DisplayName("Withdraw Tests")
    inner class WithdrawTests {

        @Test
        @DisplayName("Should withdraw money successfully")
        fun `should withdraw money successfully`() {
            // Given
            val testAccount = Account.create(
                customerId = testCustomerId,
                accountNumber = testAccountNumber,
                initialBalance = initialBalance
            )
            val withdrawAmount = BigDecimal("500.00")
            
            given(accountRepository.findById(testAccountId)).willReturn(testAccount)
            given(accountRepository.save(any())).willAnswer { invocation ->
                invocation.getArgument<Account>(0)
            }
            given(transactionRepository.save(any())).willAnswer { invocation ->
                invocation.getArgument<Transaction>(0)
            }

            // When
            val result = accountService.withdraw(
                accountId = testAccountId,
                amount = withdrawAmount,
                description = "Test withdrawal"
            )

            // Then
            assertThat(result.first.balance).isEqualTo(initialBalance - withdrawAmount)
            assertThat(result.second.amount).isEqualTo(withdrawAmount)
            assertThat(result.second.type).isEqualTo(TransactionType.WITHDRAWAL)
        }

        @Test
        @DisplayName("Should throw exception for insufficient balance")
        fun `should throw exception for insufficient balance`() {
            // Given
            val testAccount = Account.create(
                customerId = testCustomerId,
                accountNumber = testAccountNumber,
                initialBalance = BigDecimal("100.00")
            )
            val withdrawAmount = BigDecimal("500.00")
            
            given(accountRepository.findById(testAccountId)).willReturn(testAccount)

            // When & Then
            assertThatThrownBy {
                accountService.withdraw(
                    accountId = testAccountId,
                    amount = withdrawAmount,
                    description = "Insufficient balance withdrawal"
                )
            }.isInstanceOf(InsufficientBalanceException::class)
                .hasMessageContaining("Insufficient balance")
        }

        @Test
        @DisplayName("Should throw exception for negative withdraw amount")
        fun `should throw exception for negative withdraw amount`() {
            // Given
            val testAccount = Account.create(
                customerId = testCustomerId,
                accountNumber = testAccountNumber,
                initialBalance = initialBalance
            )
            given(accountRepository.findById(testAccountId)).willReturn(testAccount)

            // When & Then
            assertThatThrownBy {
                accountService.withdraw(
                    accountId = testAccountId,
                    amount = BigDecimal("-100.00"),
                    description = "Invalid withdrawal"
                )
            }.isInstanceOf(IllegalArgumentException::class)
                .hasMessageContaining("positive")
        }
    }

    @Nested
    @DisplayName("Transfer Tests")
    inner class TransferTests {

        @Test
        @DisplayName("Should transfer money between accounts successfully")
        fun `should transfer money between accounts successfully`() {
            // Given
            val fromAccount = Account.create(
                customerId = testCustomerId,
                accountNumber = "FROM_$testAccountNumber",
                initialBalance = BigDecimal("1000.00")
            )
            val toAccount = Account.create(
                customerId = UUID.fromString("10000000-0000-0000-0000-000000000002"),
                accountNumber = "TO_$testAccountNumber",
                initialBalance = BigDecimal("500.00")
            )
            val transferAmount = BigDecimal("300.00")
            
            given(accountRepository.findById(fromAccount.id)).willReturn(fromAccount)
            given(accountRepository.findById(toAccount.id)).willReturn(toAccount)
            given(accountRepository.save(any())).willAnswer { invocation ->
                invocation.getArgument<Account>(0)
            }
            given(transactionRepository.save(any())).willAnswer { invocation ->
                invocation.getArgument<Transaction>(0)
            }

            // When
            val result = accountService.transfer(
                fromAccountId = fromAccount.id,
                toAccountId = toAccount.id,
                amount = transferAmount,
                description = "Test transfer"
            )

            // Then
            assertThat(result.first.balance).isEqualTo(fromAccount.balance - transferAmount)
            assertThat(result.second.balance).isEqualTo(toAccount.balance + transferAmount)
        }

        @Test
        @DisplayName("Should throw exception for insufficient balance during transfer")
        fun `should throw exception for insufficient balance during transfer`() {
            // Given
            val fromAccount = Account.create(
                customerId = testCustomerId,
                accountNumber = "FROM_$testAccountNumber",
                initialBalance = BigDecimal("100.00")
            )
            val toAccount = Account.create(
                customerId = UUID.fromString("10000000-0000-0000-0000-000000000002"),
                accountNumber = "TO_$testAccountNumber",
                initialBalance = BigDecimal("500.00")
            )
            val transferAmount = BigDecimal("300.00")
            
            given(accountRepository.findById(fromAccount.id)).willReturn(fromAccount)
            given(accountRepository.findById(toAccount.id)).willReturn(toAccount)

            // When & Then
            assertThatThrownBy {
                accountService.transfer(
                    fromAccountId = fromAccount.id,
                    toAccountId = toAccount.id,
                    amount = transferAmount,
                    description = "Insufficient balance transfer"
                )
            }.isInstanceOf(InsufficientBalanceException::class)
        }
    }

    @Nested
    @DisplayName("Deactivate Account Tests")
    inner class DeactivateAccountTests {

        @Test
        @DisplayName("Should deactivate account successfully")
        fun `should deactivate account successfully`() {
            // Given
            val testAccount = Account.create(
                customerId = testCustomerId,
                accountNumber = testAccountNumber,
                initialBalance = initialBalance
            )
            given(accountRepository.findById(testAccountId)).willReturn(testAccount)
            given(accountRepository.save(any())).willAnswer { invocation ->
                invocation.getArgument<Account>(0)
            }

            // When
            val result = accountService.deactivateAccount(testAccountId)

            // Then
            assertThat(result.isActive).isFalse
            assertThat(result.updatedAt).isAfter(testAccount.updatedAt)
        }

        @Test
        @DisplayName("Should throw exception when deactivating non-existent account")
        fun `should throw exception when deactivating non-existent account`() {
            // Given
            given(accountRepository.findById(testAccountId)).willReturn(null)

            // When & Then
            assertThatThrownBy {
                accountService.deactivateAccount(testAccountId)
            }.isInstanceOf(AccountNotFoundException::class)
        }
    }
}
