package com.bankaccount.infrastructure.kafka.producer

import com.bankaccount.application.event.AccountCreatedEvent
import com.bankaccount.application.event.TransactionEvent
import com.bankaccount.application.event.TransferEvent
import com.bankaccount.infrastructure.kafka.KafkaConfig
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

/**
 * Producteur Kafka pour les événements liés aux comptes.
 */
@Component
class AccountEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    /**
     * Publie un événement de création de compte.
     */
    fun publishAccountCreated(event: AccountCreatedEvent) {
        val message: Message<AccountCreatedEvent> = MessageBuilder
            .withPayload(event)
            .setHeader(KafkaHeaders.TOPIC, KafkaConfig.ACCOUNT_CREATED_TOPIC)
            .build()

        kafkaTemplate.send(message)
    }

    /**
     * Publie un événement de transaction.
     */
    fun publishTransactionEvent(event: TransactionEvent) {
        val message: Message<TransactionEvent> = MessageBuilder
            .withPayload(event)
            .setHeader(KafkaHeaders.TOPIC, KafkaConfig.TRANSACTION_TOPIC)
            .build()

        kafkaTemplate.send(message)
    }

    /**
     * Publie un événement de virement.
     */
    fun publishTransferEvent(event: TransferEvent) {
        val message: Message<TransferEvent> = MessageBuilder
            .withPayload(event)
            .setHeader(KafkaHeaders.TOPIC, KafkaConfig.TRANSFER_TOPIC_EVENT)
            .build()

        kafkaTemplate.send(message)
    }
}
