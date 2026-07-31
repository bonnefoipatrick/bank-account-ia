package com.bankaccount.domain.exception

/**
 * Exception levée lorsqu'une transaction est invalide.
 */
class InvalidTransactionException(message: String) : RuntimeException(message) {
    constructor(reason: String, cause: Throwable) : this("$reason: ${cause.message}")
}
