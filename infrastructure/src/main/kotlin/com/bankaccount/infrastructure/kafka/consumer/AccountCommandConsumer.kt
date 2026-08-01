package com.bankaccount.infrastructure.kafka.consumer

import com.bankaccount.application.command.CreateAccountCommand
import com.bankaccount.application.command.DepositCommand
import com.bankaccount.application.command.TransferCommand
import com.bankaccount.application.command.WithdrawCommand
import com.bankaccount.application.event.AccountCreatedEvent
import com.bankaccount.application.event.TransactionEvent
import com.bankaccount.application.event.TransferEvent
import com.bankaccount.application.mapper.AccountMapper
import com.bankaccount.application.mapper.TransactionMapper
import com.bankaccount.domain.service.AccountService
import com.bankaccount.infrastructure.kafka.KafkaConfig
import com.bankaccount.infrastructure.kafka.producer.AccountEventProducer
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

/**
 * Consumer Kafka pour les commandes liées aux comptes.
 * Consomme les commandes et déclenche les actions métiers correspondantes.
 */
@Component
class AccountCommandConsumer(
    private val accountService: AccountService,
    private val accountMapper: AccountMapper,
    private val transactionMapper: TransactionMapper,
    private val eventProducer: AccountEventProducer
) {

    /**
     * Écoute les commandes de création de compte.
     */
    @KafkaListener(
        topics = [KafkaConfig.CREATE_ACCOUNT_TOPIC],
        groupId = KafkaConfig.ACCOUNT_GROUP_ID
    )
    fun listenCreateAccount(command: CreateAccountCommand) {
        println("Received CreateAccountCommand: customerId=${command.customerId}, accountNumber=${command.accountNumber}")

        val account = accountService.createAccount(
            customerId = command.customerId,
            accountNumber = command.accountNumber,
            initialBalance = command.initialBalance,
            currency = command.currency
        )

        // Publie l'événement de création
        val event = AccountCreatedEvent(
            accountId = account.id,
            customerId = account.customerId,
            accountNumber = account.accountNumber,
            initialBalance = account.balance,
            currency = account.currency,
            createdAt = account.createdAt
        )
        eventProducer.publishAccountCreated(event)

        println("Account created and event published: ${account.id}")
    }

    /**
     * Écoute les commandes de dépôt.
     */
    @KafkaListener(
        topics = [KafkaConfig.DEPOSIT_TOPIC],
        groupId = KafkaConfig.ACCOUNT_GROUP_ID
    )
    fun listenDeposit(command: DepositCommand) {
        println("Received DepositCommand: accountId=${command.accountId}, amount=${command.amount}")

        val (account, transaction) = accountService.deposit(
            accountId = command.accountId,
            amount = command.amount,
            description = command.description
        )

        // Publie l'événement de transaction
        val event = TransactionEvent(
            transactionId = transaction.id,
            accountId = transaction.accountId,
            amount = transaction.amount,
            type = transaction.type.name,
            description = transaction.description,
            createdAt = transaction.createdAt,
            newBalance = account.balance
        )
        eventProducer.publishTransactionEvent(event)

        println("Deposit processed and event published: ${transaction.id}")
    }

    /**
     * Écoute les commandes de retrait.
     */
    @KafkaListener(
        topics = [KafkaConfig.WITHDRAW_TOPIC],
        groupId = KafkaConfig.ACCOUNT_GROUP_ID
    )
    fun listenWithdraw(command: WithdrawCommand) {
        println("Received WithdrawCommand: accountId=${command.accountId}, amount=${command.amount}")

        val (account, transaction) = accountService.withdraw(
            accountId = command.accountId,
            amount = command.amount,
            description = command.description
        )

        // Publie l'événement de transaction
        val event = TransactionEvent(
            transactionId = transaction.id,
            accountId = transaction.accountId,
            amount = transaction.amount,
            type = transaction.type.name,
            description = transaction.description,
            createdAt = transaction.createdAt,
            newBalance = account.balance
        )
        eventProducer.publishTransactionEvent(event)

        println("Withdrawal processed and event published: ${transaction.id}")
    }

    /**
     * Écoute les commandes de virement.
     */
    @KafkaListener(
        topics = [KafkaConfig.TRANSFER_TOPIC],
        groupId = KafkaConfig.ACCOUNT_GROUP_ID
    )
    fun listenTransfer(command: TransferCommand) {
        println("Received TransferCommand: from=${command.fromAccountId}, to=${command.toAccountId}, amount=${command.amount}")

        val (fromAccount, toAccount) = accountService.transfer(
            fromAccountId = command.fromAccountId,
            toAccountId = command.toAccountId,
            amount = command.amount,
            description = command.description
        )

        // Publie l'événement de virement
        val event = TransferEvent(
            transferId = UUID.randomUUID(),
            fromAccountId = fromAccount.id,
            toAccountId = toAccount.id,
            amount = command.amount,
            description = command.description,
            createdAt = LocalDateTime.now(),
            fromNewBalance = fromAccount.balance,
            toNewBalance = toAccount.balance
        )
        eventProducer.publishTransferEvent(event)

        println("Transfer processed and event published")
    }
}
