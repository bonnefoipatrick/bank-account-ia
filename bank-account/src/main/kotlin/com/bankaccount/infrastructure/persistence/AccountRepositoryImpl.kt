package com.bankaccount.infrastructure.persistence

import com.bankaccount.domain.model.Account
import com.bankaccount.domain.repository.AccountRepository
import com.bankaccount.infrastructure.persistence.entity.AccountEntity
import com.bankaccount.infrastructure.persistence.mapper.AccountEntityMapper
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Implémentation du AccountRepository utilisant JPA.
 * Adaptateur pour l'architecture Clean Architecture.
 */
@Component
class AccountRepositoryImpl(
    private val jpaRepository: AccountJpaRepository,
    private val mapper: AccountEntityMapper
) : AccountRepository {

    override fun save(account: Account): Account {
        val entity = mapper.toEntity(account)
        val savedEntity = jpaRepository.save(entity)
        return mapper.toDomain(savedEntity)
    }

    override fun findById(id: UUID): Account? {
        return jpaRepository.findById(id)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByAccountNumber(accountNumber: String): Account? {
        return jpaRepository.findByAccountNumber(accountNumber)
            ?.let { mapper.toDomain(it) }
    }

    override fun findAllByCustomerId(customerId: UUID): List<Account> {
        return jpaRepository.findAllByCustomerId(customerId)
            .map { mapper.toDomain(it) }
    }

    override fun deleteById(id: UUID) {
        jpaRepository.deleteById(id)
    }

    override fun existsByAccountNumber(accountNumber: String): Boolean {
        return jpaRepository.existsByAccountNumber(accountNumber)
    }
}
