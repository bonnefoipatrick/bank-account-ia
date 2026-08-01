package com.bankaccount.infrastructure.kafka

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

/**
 * Configuration des topics Kafka.
 */
@Configuration
class KafkaConfig {

    companion object {
        // Topics pour les commandes
        const val CREATE_ACCOUNT_TOPIC = "account.create.command"
        const val DEPOSIT_TOPIC = "account.deposit.command"
        const val WITHDRAW_TOPIC = "account.withdraw.command"
        const val TRANSFER_TOPIC = "account.transfer.command"

        // Topics pour les événements
        const val ACCOUNT_CREATED_TOPIC = "account.created.event"
        const val TRANSACTION_TOPIC = "account.transaction.event"
        const val TRANSFER_TOPIC_EVENT = "account.transfer.event"

        // Group IDs pour les consumers
        const val ACCOUNT_GROUP_ID = "account-service-group"
    }

    /**
     * Crée les topics Kafka au démarrage de l'application.
     */
    @Bean
    fun createAccountTopic(): NewTopic {
        return TopicBuilder.name(CREATE_ACCOUNT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build()
    }

    @Bean
    fun depositTopic(): NewTopic {
        return TopicBuilder.name(DEPOSIT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build()
    }

    @Bean
    fun withdrawTopic(): NewTopic {
        return TopicBuilder.name(WITHDRAW_TOPIC)
            .partitions(3)
            .replicas(1)
            .build()
    }

    @Bean
    fun transferTopic(): NewTopic {
        return TopicBuilder.name(TRANSFER_TOPIC)
            .partitions(3)
            .replicas(1)
            .build()
    }

    @Bean
    fun accountCreatedTopic(): NewTopic {
        return TopicBuilder.name(ACCOUNT_CREATED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build()
    }

    @Bean
    fun transactionTopic(): NewTopic {
        return TopicBuilder.name(TRANSACTION_TOPIC)
            .partitions(3)
            .replicas(1)
            .build()
    }

    @Bean
    fun transferEventTopic(): NewTopic {
        return TopicBuilder.name(TRANSFER_TOPIC_EVENT)
            .partitions(3)
            .replicas(1)
            .build()
    }
}
