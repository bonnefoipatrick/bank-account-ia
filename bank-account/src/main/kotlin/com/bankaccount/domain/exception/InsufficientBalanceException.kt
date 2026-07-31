package com.bankaccount.domain.exception

import java.math.BigDecimal

/**
 * Exception levée lorsqu'un compte n'a pas assez de fonds pour une opération.
 */
class InsufficientBalanceException(
    val currentBalance: BigDecimal,
    val requiredAmount: BigDecimal
) : RuntimeException(
    "Insufficient balance: current=$currentBalance, required=$requiredAmount"
)
