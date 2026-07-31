package com.bankaccount.infrastructure.persistence

import com.bankaccount.domain.model.Transaction
import com.bankaccount.domain.repository.TransactionRepository
import com.bankaccount.infrastructure.persistence.entity.TransactionEntity
import com.bankaccount.infrastructure.persistence.mapper.TransactionEntityMapper
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Implémentation du TransactionRepository utilisant JPA.
 * Adaptateur pour l'architecture Clean Architecture.
 */
@Component
class TransactionRepositoryImpl(
    private val jpaRepository: TransactionJpaRepository,
    private val mapper: TransactionEntityMapper
) : TransactionRepository {

    override fun save(transaction: Transaction): Transaction {
        val entity = mapper.toEntity(transaction)
        val savedEntity = jpaRepository.save(entity)
        return mapper.toDomain(savedEntity)
    }

    override fun findById(id: UUID): Transaction? {
        return jpaRepository.findById(id)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }

    override fun findAllByAccountId(accountId: UUID): List<Transaction> {
        return jpaRepository.findAllByAccountId(accountId)
            .map { mapper.toDomain(it) }
    }

    override fun findAllByAccountId(accountId: UUID, page: Int, size: Int): List<Transaction> {
        val pageable = PageRequest.of(page, size)
        return jpaRepository.findAllByAccountId(accountId, pageable)
            .map { mapper.toDomain(it) }
    }

    override fun deleteById(id: UUID) {
        jpaRepository.deleteById(id)
    }
}
