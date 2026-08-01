package com.bankaccount.infrastructure.persistence.mapper

import com.bankaccount.domain.model.Account
import com.bankaccount.infrastructure.persistence.entity.AccountEntity
import org.springframework.stereotype.Component

/**
 * Mapper pour convertir entre Account (Domain) et AccountEntity (JPA).
 */
@Component
class AccountEntityMapper {

    fun toEntity(account: Account): AccountEntity {
        return AccountEntity(
            id = account.id,
            customerId = account.customerId,
            accountNumber = account.accountNumber,
            balance = account.balance,
            currency = account.currency,
            createdAt = account.createdAt,
            updatedAt = account.updatedAt,
            isActive = account.isActive
        )
    }

    fun toDomain(entity: AccountEntity): Account {
        return Account(
            id = entity.id!!,
            customerId = entity.customerId,
            accountNumber = entity.accountNumber,
            balance = entity.balance,
            currency = entity.currency,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            isActive = entity.isActive
        )
    }

    fun toDomainList(entities: List<AccountEntity>): List<Account> {
        return entities.map { toDomain(it) }
    }
}
