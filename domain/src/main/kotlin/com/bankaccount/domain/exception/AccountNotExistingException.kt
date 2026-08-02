package com.bankaccount.domain.exception

import java.util.UUID

/**
 * Exception levée lorsqu'un compte bancaire n'est pas trouvé.
 */
class AccountNotExistingException(message: String) : RuntimeException(
    "Account with number $message not found"
)