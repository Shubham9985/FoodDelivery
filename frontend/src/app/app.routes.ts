import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/home/home/home.component.js')
        .then(c => c.HomeComponent)
  },
  {
    path: 'restaurants',
    loadComponent: () =>
      import('./features/restaurant/restaurant-list/restaurant-list.component')
        .then(c => c.RestaurantListComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];