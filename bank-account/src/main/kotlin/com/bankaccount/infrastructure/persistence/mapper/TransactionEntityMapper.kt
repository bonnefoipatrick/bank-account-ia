package com.bankaccount.infrastructure.persistence.mapper

import com.bankaccount.domain.model.Transaction
import com.bankaccount.domain.model.TransactionType
import com.bankaccount.infrastructure.persistence.entity.TransactionEntity
import com.bankaccount.infrastructure.persistence.entity.TransactionTypeEntity
import org.springframework.stereotype.Component

/**
 * Mapper pour convertir entre Transaction (Domain) et TransactionEntity (JPA).
 */
@Component
class TransactionEntityMapper {

    fun toEntity(transaction: Transaction): TransactionEntity {
        return TransactionEntity(
            id = transaction.id,
            accountId = transaction.accountId,
            amount = transaction.amount,
            type = mapTransactionType(transaction.type),
            description = transaction.description,
            createdAt = transaction.createdAt,
            reference = transaction.reference
        )
    }

    fun toDomain(entity: TransactionEntity): Transaction {
        return Transaction(
            id = entity.id!!,
            accountId = entity.accountId,
            amount = entity.amount,
            type = mapTransactionType(entity.type),
            description = entity.description,
            createdAt = entity.createdAt,
            reference = entity.reference
        )
    }

    fun toDomainList(entities: List<TransactionEntity>): List<Transaction> {
        return entities.map { toDomain(it) }
    }

    private fun mapTransactionType(type: TransactionType): TransactionTypeEntity {
        return when (type) {
            TransactionType.DEPOSIT -> TransactionTypeEntity.DEPOSIT
            TransactionType.WITHDRAWAL -> TransactionTypeEntity.WITHDRAWAL
            TransactionType.TRANSFER_IN -> TransactionTypeEntity.TRANSFER_IN
            TransactionType.TRANSFER_OUT -> TransactionTypeEntity.TRANSFER_OUT
        }
    }

    private fun mapTransactionType(type: TransactionTypeEntity): TransactionType {
        return when (type) {
            TransactionTypeEntity.DEPOSIT -> TransactionType.DEPOSIT
            TransactionTypeEntity.WITHDRAWAL -> TransactionType.WITHDRAWAL
            TransactionTypeEntity.TRANSFER_IN -> TransactionType.TRANSFER_IN
            TransactionTypeEntity.TRANSFER_OUT -> TransactionType.TRANSFER_OUT
        }
    }
}
