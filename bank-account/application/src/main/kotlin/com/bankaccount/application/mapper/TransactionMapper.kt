package com.bankaccount.application.mapper

import com.bankaccount.application.dto.CreateTransactionRequest
import com.bankaccount.application.dto.TransactionDto
import com.bankaccount.domain.model.Transaction
import com.bankaccount.domain.model.TransactionType
import org.springframework.stereotype.Component

/**
 * Mapper pour convertir entre les entités Domain et les DTOs pour les transactions.
 */
@Component
class TransactionMapper {

    fun toDto(transaction: Transaction): TransactionDto {
        return TransactionDto(
            id = transaction.id,
            accountId = transaction.accountId,
            amount = transaction.amount,
            type = transaction.type.name,
            description = transaction.description,
            createdAt = transaction.createdAt,
            reference = transaction.reference
        )
    }

    fun toEntity(
        request: CreateTransactionRequest,
        accountId: java.util.UUID
    ): Transaction {
        val type = try {
            TransactionType.valueOf(request.type)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid transaction type: ${request.type}")
        }

        return Transaction.create(
            accountId = accountId,
            amount = request.amount,
            type = type,
            description = request.description.ifBlank { "Transaction" },
            reference = request.reference
        )
    }

    fun toDtoList(transactions: List<Transaction>): List<TransactionDto> {
        return transactions.map { toDto(it) }
    }
}
