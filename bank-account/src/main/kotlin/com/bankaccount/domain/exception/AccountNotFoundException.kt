package com.bankaccount.domain.exception

import java.util.UUID

/**
 * Exception levée lorsqu'un compte bancaire n'est pas trouvé.
 */
class AccountNotFoundException(message: String) : RuntimeException(message) {
    constructor(accountId: UUID) : this("Account with ID $accountId not found")
    constructor(accountNumber: String) : this("Account with number $accountNumber not found")
}
