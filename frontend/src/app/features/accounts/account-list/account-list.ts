import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AccountService } from '../../../core/services/account.service';
import { Account } from '../../../core/models/account.model';
import { AccountCurrencyPipe } from '../../../shared/pipes/account-currency.pipe';

@Component({
  selector: 'app-account-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, AccountCurrencyPipe],
  templateUrl: './account-list.html',
  styleUrl: './account-list.scss'
})
export class AccountListComponent {
  private readonly accountService = inject(AccountService);

  customerId = signal('');
  accounts = signal<Account[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);
  searched = signal(false);

  search(): void {
    const id = this.customerId().trim();
    if (!id) {
      this.error.set('Merci de saisir un identifiant client (UUID).');
      return;
    }

    this.loading.set(true);
    this.error.set(null);
    this.searched.set(true);

    this.accountService.getAccountsByCustomer(id).subscribe({
      next: (accounts) => {
        this.accounts.set(accounts);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Impossible de récupérer les comptes de ce client.');
        this.accounts.set([]);
        this.loading.set(false);
      }
    });
  }
}
