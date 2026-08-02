import { Pipe, PipeTransform } from '@angular/core';

/**
 * Formate un montant avec la devise du compte (EUR, USD, ...)
 * sans dépendre d'une locale Angular enregistrée globalement.
 */
@Pipe({ name: 'accountCurrency' })
export class AccountCurrencyPipe implements PipeTransform {
  transform(value: number | null | undefined, currency = 'EUR'): string {
    if (value === null || value === undefined) {
      return '—';
    }
    return new Intl.NumberFormat('fr-FR', { style: 'currency', currency }).format(value);
  }
}
