package com.bankaccount.application.mapper

import com.bankaccount.application.dto.AccountDto
import com.bankaccount.application.dto.CreateAccountRequest
import com.bankaccount.domain.model.Account
import org.springframework.stereotype.Component

/**
 * Mapper pour convertir entre les entités Domain et les DTOs.
 */
@Component
class AccountMapper {

    fun toDto(account: Account): AccountDto {
        return AccountDto(
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

    fun toEntity(request: CreateAccountRequest): Account {
        return Account.create(
            customerId = request.customerId,
            accountNumber = request.accountNumber,
            initialBalance = request.initialBalance,
            currency = request.currency
        )
    }

    fun toDtoList(accounts: List<Account>): List<AccountDto> {
        return accounts.map { toDto(it) }
    }
}
