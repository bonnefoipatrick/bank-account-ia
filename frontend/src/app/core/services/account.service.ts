import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Account,
  CreateAccountRequest,
  CreateAccountResponse,
  CreateTransactionRequest,
  Transaction,
  TransactionResponse,
  TransferRequest,
  TransferResponse
} from '../models/account.model';

/**
 * Client HTTP pour l'API `/api/v1/accounts` exposée par
 * com.bankaccount.infrastructure.web.AccountController.
 */
@Injectable({ providedIn: 'root' })
export class AccountService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/accounts`;

  createAccount(request: CreateAccountRequest): Observable<CreateAccountResponse> {
    return this.http.post<CreateAccountResponse>(this.baseUrl, request);
  }

  getAccountById(id: string): Observable<Account> {
    return this.http.get<Account>(`${this.baseUrl}/${id}`);
  }

  getAccountByNumber(accountNumber: string): Observable<Account> {
    return this.http.get<Account>(`${this.baseUrl}/number/${accountNumber}`);
  }

  getAccountsByCustomer(customerId: string): Observable<Account[]> {
    return this.http.get<Account[]>(`${this.baseUrl}/customer/${customerId}`);
  }

  deposit(accountId: string, request: CreateTransactionRequest): Observable<TransactionResponse> {
    return this.http.post<TransactionResponse>(`${this.baseUrl}/${accountId}/deposit`, request);
  }

  withdraw(accountId: string, request: CreateTransactionRequest): Observable<TransactionResponse> {
    return this.http.post<TransactionResponse>(`${this.baseUrl}/${accountId}/withdraw`, request);
  }

  transfer(request: TransferRequest): Observable<TransferResponse> {
    return this.http.post<TransferResponse>(`${this.baseUrl}/transfer`, request);
  }

  deactivateAccount(id: string): Observable<Account> {
    return this.http.patch<Account>(`${this.baseUrl}/${id}/deactivate`, {});
  }

  getAccountTransactions(accountId: string, page = 0, size = 20): Observable<Transaction[]> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Transaction[]>(`${this.baseUrl}/${accountId}/transactions`, { params });
  }
}
