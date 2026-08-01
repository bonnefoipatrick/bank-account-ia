package com.bankaccount.infrastructure.config

import com.bankaccount.domain.repository.AccountRepository
import com.bankaccount.domain.repository.TransactionRepository
import com.bankaccount.domain.service.AccountService
import com.bankaccount.infrastructure.persistence.AccountRepositoryImpl
import com.bankaccount.infrastructure.persistence.TransactionRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration des beans Spring pour l'injection de dépendances.
 * Centralise la création des objets du Domain Layer.
 */
@Configuration
class BeanConfig {

    @Bean
    fun accountService(
        accountRepository: AccountRepository,
        transactionRepository: TransactionRepository
    ): AccountService {
        return AccountService(accountRepository, transactionRepository)
    }

    @Bean
    fun accountRepository(
        jpaRepository: com.bankaccount.infrastructure.persistence.AccountJpaRepository,
        mapper: com.bankaccount.infrastructure.persistence.mapper.AccountEntityMapper
    ): AccountRepository {
        return AccountRepositoryImpl(jpaRepository, mapper)
    }

    @Bean
    fun transactionRepository(
        jpaRepository: com.bankaccount.infrastructure.persistence.TransactionJpaRepository,
        mapper: com.bankaccount.infrastructure.persistence.mapper.TransactionEntityMapper
    ): TransactionRepository {
        return TransactionRepositoryImpl(jpaRepository, mapper)
    }
}
