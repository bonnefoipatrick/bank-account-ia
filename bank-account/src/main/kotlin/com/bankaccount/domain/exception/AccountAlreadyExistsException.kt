package com.bankaccount.domain.exception

/**
 * Exception levée lorsqu'un compte avec le même numéro existe déjà.
 */
class AccountAlreadyExistsException(accountNumber: String) : RuntimeException(
    "Account with number $accountNumber already exists"
)
