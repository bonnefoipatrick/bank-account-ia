/**
 * Modèles alignés sur les DTOs Kotlin du module `application`
 * (com.bankaccount.application.dto.AccountDto / TransactionDto).
 */

export interface Account {
  id: string;
  customerId: string;
  accountNumber: string;
  balance: number;
  currency: string;
  createdAt: string;
  updatedAt: string;
  isActive: boolean;
}

export interface CreateAccountRequest {
  customerId: string;
  accountNumber: string;
  initialBalance: number;
  currency: string;
}

export interface CreateAccountResponse {
  account: Account;
  message: string;
}

export type TransactionType = 'DEPOSIT' | 'WITHDRAWAL';

export interface Transaction {
  id: string;
  accountId: string;
  amount: number;
  type: string;
  description: string;
  createdAt: string;
  reference?: string | null;
}

export interface CreateTransactionRequest {
  amount: number;
  type: TransactionType;
  description?: string;
  reference?: string | null;
}

export interface TransactionResponse {
  account: Account;
  transaction: Transaction;
  message: string;
}

export interface TransferRequest {
  fromAccountId: string;
  toAccountId: string;
  amount: number;
  description?: string;
}

export interface TransferResponse {
  fromAccount: Account;
  toAccount: Account;
  message: string;
}
