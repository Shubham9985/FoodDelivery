import { Routes } from '@angular/router';
import { AuthComponent } from './auth/auth.component';
import { HomeComponent } from './home/home.component';
import { RestaurantMenuComponent } from './restaurant-menu/restaurant-menu.component';
import { CartComponent } from './cart/cart.component';
import { OrdersComponent } from './orders/orders.component';
import { AddressesComponent } from './addresses/addresses.component';
import { ProfileComponent } from './profile/profile.component';

import { AdminDashboardComponent } from './admin/dashboard/admin-dashboard/admin-dashboard.component';
import { AdminRestaurantsComponent } from './admin/restaurants/admin-restaurants/admin-restaurants.component';
import { AdminMenuItemsComponent } from './admin/menu-items/admin-menu-items/admin-menu-items.component';
import { AdminDriversComponent } from './admin/drivers/admin-drivers/admin-drivers.component';
import { AdminCouponsComponent } from './admin/coupons/admin-coupons/admin-coupons.component';
import { AdminOrdersComponent } from './admin/orders/admin-orders/admin-orders.component';
import { AdminCustomerComponent } from './admin/customer/admin-customer/admin-customer.component';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'auth', component: AuthComponent },
  { path: 'home', component: HomeComponent },
  { path: 'restaurant/:id', component: RestaurantMenuComponent },
  { path: 'cart', component: CartComponent },
  { path: 'orders', component: OrdersComponent },
  { path: 'addresses', component: AddressesComponent },
  { path: 'profile', component: ProfileComponent },

  {
    path: 'admin',
    component: AdminDashboardComponent,
    children: [
      { path: 'restaurants', component: AdminRestaurantsComponent },
      { path: 'menu-items', component: AdminMenuItemsComponent },
      { path: 'drivers', component: AdminDriversComponent },
      { path: 'coupons', component: AdminCouponsComponent },
      { path: 'orders', component: AdminOrdersComponent },
      { path: 'customers', component: AdminCustomerComponent }
    ]
  },

  { path: '**', redirectTo: 'home' }
];