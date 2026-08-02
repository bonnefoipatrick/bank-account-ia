import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'accounts' },
  {
    path: 'accounts',
    loadComponent: () =>
      import('./features/accounts/account-list/account-list').then((m) => m.AccountListComponent)
  },
  {
    path: 'accounts/new',
    loadComponent: () =>
      import('./features/accounts/account-create/account-create').then((m) => m.AccountCreateComponent)
  },
  {
    path: 'accounts/:id',
    loadComponent: () =>
      import('./features/accounts/account-detail/account-detail').then((m) => m.AccountDetailComponent)
  },
  { path: '**', redirectTo: 'accounts' }
];
