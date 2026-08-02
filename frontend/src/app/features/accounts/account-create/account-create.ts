import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AccountService } from '../../../core/services/account.service';
import { CreateAccountRequest } from '../../../core/models/account.model';

@Component({
  selector: 'app-account-create',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './account-create.html',
  styleUrl: './account-create.scss'
})
export class AccountCreateComponent {
  private readonly accountService = inject(AccountService);
  private readonly router = inject(Router);

  form = signal<CreateAccountRequest>({
    customerId: '',
    accountNumber: '',
    initialBalance: 0,
    currency: 'EUR'
  });

  submitting = signal(false);
  error = signal<string | null>(null);

  submit(): void {
    const value = this.form();
    if (!value.customerId.trim() || !value.accountNumber.trim()) {
      this.error.set('Identifiant client et numéro de compte sont obligatoires.');
      return;
    }

    this.submitting.set(true);
    this.error.set(null);

    this.accountService.createAccount(value).subscribe({
      next: (response) => {
        this.submitting.set(false);
        this.router.navigate(['/accounts', response.account.id]);
      },
      error: (err) => {
        this.submitting.set(false);
        this.error.set(err?.error?.message ?? 'La création du compte a échoué.');
      }
    });
  }

  updateField<K extends keyof CreateAccountRequest>(field: K, value: CreateAccountRequest[K]): void {
    this.form.update((current) => ({ ...current, [field]: value }));
  }
}
