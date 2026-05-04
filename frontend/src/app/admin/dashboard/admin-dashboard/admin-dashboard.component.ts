import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, filter } from 'rxjs/operators';
import { AdminService } from '../../services/admin-services/admin.service';
import { getStoredUser, isBrowser } from '../../../utils/browser-storage';
@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  adminName = 'Admin';
  stats = {
    restaurants: 0,
    menuItems: 0,
    drivers: 0,
    coupons: 0,
    orders: 0,
    customers:0
  };
  loading = false;
  isRootPath = true;

  constructor(private adminService: AdminService, public router: Router) {
    this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe(() => {
      this.isRootPath = this.router.url === '/admin' || this.router.url === '/admin/';
    });
  }

  ngOnInit(): void {
    const user = getStoredUser() || {};
    if (user?.role !== 'ADMIN') {
      this.router.navigate(['/auth']);
      return;
    }
    this.adminName = user?.name || 'Admin';
    this.isRootPath = this.router.url === '/admin' || this.router.url === '/admin/';
    this.loadStats();
  }

  loadStats(): void {
    this.loading = true;
    forkJoin({
      restaurants: this.adminService.getAllRestaurants().pipe(catchError(() => of([]))),
      menuItems: this.adminService.getAllMenuItems().pipe(catchError(() => of([]))),
      drivers: this.adminService.getAllDrivers().pipe(catchError(() => of([]))),
      coupons: this.adminService.getAllCoupons().pipe(catchError(() => of([]))),
      orders: this.adminService.getAllOrders().pipe(catchError(() => of([]))),
      customers: this.adminService.getAllCustomers().pipe(catchError(() => of([])))
    }).subscribe(res => {
      this.stats.restaurants = res.restaurants.length;
      this.stats.menuItems = res.menuItems.length;
      this.stats.drivers = res.drivers.length;
      this.stats.coupons = res.coupons.length;
      this.stats.orders = res.orders.length;
      this.stats.customers = res.customers.length;
      this.loading = false;
    });
  }

  logout(): void {
    if (isBrowser()) {
      window.localStorage.removeItem('user');
    }
    this.router.navigate(['/auth']);
  }
}