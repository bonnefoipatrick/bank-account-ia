import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AccountService } from '../../../core/services/account.service';
import { Account, Transaction, TransactionType } from '../../../core/models/account.model';
import { AccountCurrencyPipe } from '../../../shared/pipes/account-currency.pipe';

@Component({
  selector: 'app-account-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, AccountCurrencyPipe],
  templateUrl: './account-detail.html',
  styleUrl: './account-detail.scss'
})
export class AccountDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly accountService = inject(AccountService);

  accountId = '';
  account = signal<Account | null>(null);
  transactions = signal<Transaction[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  actionMessage = signal<string | null>(null);
  actionError = signal<string | null>(null);
  actionPending = signal(false);

  // Formulaire dépôt / retrait
  movementAmount = signal<number>(0);
  movementDescription = signal('');
  movementType = signal<TransactionType>('DEPOSIT');

  // Formulaire virement
  transferToAccountId = signal('');
  transferAmount = signal<number>(0);
  transferDescription = signal('');

  ngOnInit(): void {
    this.accountId = this.route.snapshot.paramMap.get('id') ?? '';
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.accountService.getAccountById(this.accountId).subscribe({
      next: (account) => {
        this.account.set(account);
        this.loading.set(false);
        this.loadTransactions();
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Compte introuvable.');
        this.loading.set(false);
      }
    });
  }

  loadTransactions(): void {
    this.accountService.getAccountTransactions(this.accountId).subscribe({
      next: (transactions) => this.transactions.set(transactions),
      error: () => this.transactions.set([])
    });
  }

  submitMovement(): void {
    const amount = this.movementAmount();
    if (!amount || amount <= 0) {
      this.actionError.set('Le montant doit être supérieur à zéro.');
      return;
    }

    this.actionPending.set(true);
    this.actionError.set(null);
    this.actionMessage.set(null);

    const request = {
      amount,
      type: this.movementType(),
      description: this.movementDescription()
    };

    const call$ =
      this.movementType() === 'DEPOSIT'
        ? this.accountService.deposit(this.accountId, request)
        : this.accountService.withdraw(this.accountId, request);

    call$.subscribe({
      next: (response) => {
        this.account.set(response.account);
        this.actionMessage.set(response.message);
        this.movementAmount.set(0);
        this.movementDescription.set('');
        this.actionPending.set(false);
        this.loadTransactions();
      },
      error: (err) => {
        this.actionError.set(err?.error?.message ?? "L'opération a échoué.");
        this.actionPending.set(false);
      }
    });
  }

  submitTransfer(): void {
    const amount = this.transferAmount();
    const toAccountId = this.transferToAccountId().trim();

    if (!toAccountId || !amount || amount <= 0) {
      this.actionError.set('Compte destinataire et montant valides sont requis.');
      return;
    }

    this.actionPending.set(true);
    this.actionError.set(null);
    this.actionMessage.set(null);

    this.accountService
      .transfer({
        fromAccountId: this.accountId,
        toAccountId,
        amount,
        description: this.transferDescription() || 'Transfer'
      })
      .subscribe({
        next: (response) => {
          this.account.set(response.fromAccount);
          this.actionMessage.set(response.message);
          this.transferAmount.set(0);
          this.transferToAccountId.set('');
          this.transferDescription.set('');
          this.actionPending.set(false);
          this.loadTransactions();
        },
        error: (err) => {
          this.actionError.set(err?.error?.message ?? 'Le virement a échoué.');
          this.actionPending.set(false);
        }
      });
  }

  deactivate(): void {
    this.actionPending.set(true);
    this.actionError.set(null);

    this.accountService.deactivateAccount(this.accountId).subscribe({
      next: (account) => {
        this.account.set(account);
        this.actionMessage.set('Compte désactivé.');
        this.actionPending.set(false);
      },
      error: (err) => {
        this.actionError.set(err?.error?.message ?? 'La désactivation a échoué.');
        this.actionPending.set(false);
      }
    });
  }
}
