import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { CustomerService } from '../services/customer.service';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  menuItems: any[] = [];
  allMenuItems: any[] = [];
  restaurants: any[] = [];
  allRestaurants: any[] = [];
  view: 'items' | 'restaurants' = 'items';
  loading = false;
  error = '';
  isLoggedIn = false;
  searchKeyword = '';
  activeSearchKeyword = '';
  quantities: { [itemId: number]: number } = {};
  message = '';
  messageType: 'success' | 'error' | '' = '';
  ratingsCache: { [restaurantId: number]: number } = {};

  // Filters
  showFilters = false;
  minPrice: number | null = null;
  maxPrice: number | null = null;
  minRating: number = 0;
  selectedRestaurantId: number | null = null;
  sortBy: 'default' | 'priceAsc' | 'priceDesc' | 'nameAsc' | 'ratingDesc' = 'default';

  constructor(
    private customerService: CustomerService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isAuthenticated();
    this.loadMenuItems();
  }

  loadMenuItems(): void {
    this.loading = true;
    this.error = '';
    this.customerService.getAllMenuItems().subscribe({
      next: (data) => {
        this.allMenuItems = data;
        this.menuItems = [...data];
        data.forEach(item => {
          if (this.quantities[item.itemId] == null) this.quantities[item.itemId] = 1;
        });
        this.fetchRatings(data);
        this.loading = false;
        this.applyFilters();
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load menu items';
        this.loading = false;
      }
    });
  }

  fetchRatings(items: any[]): void {
    const uniqueIds = Array.from(new Set(items.map(i => i.restaurantId).filter(id => id != null)));
    const pending = uniqueIds.filter(id => this.ratingsCache[id] == null);
    if (pending.length === 0) return;
    forkJoin(
      pending.map(id =>
        this.customerService.getAverageRating(id).pipe(
          map(rating => ({ id, rating })),
          catchError(() => of({ id, rating: 0 }))
        )
      )
    ).subscribe(results => {
      results.forEach(r => this.ratingsCache[r.id] = r.rating);
      this.applyFilters();
    });
  }

  getRating(restaurantId: number): number {
    return this.ratingsCache[restaurantId] || 0;
  }

  loadRestaurants(): void {
    this.loading = true;
    this.customerService.getAllRestaurants().subscribe({
      next: (data) => {
        this.allRestaurants = data;
        this.restaurants = [...data];
        const ids = data.map((r: any) => r.restaurantId).filter((id: any) => id != null);
        const pending = ids.filter((id: number) => this.ratingsCache[id] == null);
        if (pending.length > 0) {
          forkJoin(
            pending.map((id: number) =>
              this.customerService.getAverageRating(id).pipe(
                map(rating => ({ id, rating })),
                catchError(() => of({ id, rating: 0 }))
              )
            )
          ).subscribe(results => {
            results.forEach(r => this.ratingsCache[r.id] = r.rating);
            this.applyFilters();
          });
        }
        this.loading = false;
        this.applyFilters();
      },
      error: (err) => { this.error = err.error?.message || 'Failed to load restaurants'; this.loading = false; }
    });
  }

  showItems(): void {
    this.view = 'items';
    this.resetFilters();
    this.searchKeyword = '';
    this.activeSearchKeyword = '';
    if (this.allMenuItems.length === 0) this.loadMenuItems();
    else this.applyFilters();
  }

  showRestaurants(): void {
    this.view = 'restaurants';
    this.resetFilters();
    this.searchKeyword = '';
    this.activeSearchKeyword = '';
    if (this.allRestaurants.length === 0) this.loadRestaurants();
    else this.applyFilters();
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  resetFilters(): void {
    this.minPrice = null;
    this.maxPrice = null;
    this.minRating = 0;
    this.selectedRestaurantId = null;
    this.sortBy = 'default';
    this.applyFilters();
  }

  getRestaurantOptions(): any[] {
    if (this.allRestaurants.length > 0) return this.allRestaurants;
    const map = new Map<number, string>();
    this.allMenuItems.forEach(i => {
      if (i.restaurantId && !map.has(i.restaurantId)) {
        map.set(i.restaurantId, i.restaurantName);
      }
    });
    return Array.from(map.entries()).map(([restaurantId, restaurantName]) => ({ restaurantId, restaurantName }));
  }

  applyFilters(): void {
    if (this.view === 'items') {
      let filtered = [...this.allMenuItems];

      const kw = this.activeSearchKeyword.trim().toLowerCase();
      if (kw) {
        filtered = filtered.filter(i =>
          (i.itemName || '').toLowerCase().includes(kw) ||
          (i.restaurantName || '').toLowerCase().includes(kw)
        );
      }

      if (this.minPrice != null) {
        filtered = filtered.filter(i => Number(i.itemPrice) >= this.minPrice!);
      }
      if (this.maxPrice != null) {
        filtered = filtered.filter(i => Number(i.itemPrice) <= this.maxPrice!);
      }
      if (this.minRating > 0) {
        filtered = filtered.filter(i => this.getRating(i.restaurantId) >= this.minRating);
      }
      if (this.selectedRestaurantId != null) {
        filtered = filtered.filter(i => i.restaurantId === Number(this.selectedRestaurantId));
      }

      filtered = this.sortItems(filtered);
      this.menuItems = filtered;
    } else {
      let filtered = [...this.allRestaurants];

      const kw = this.activeSearchKeyword.trim().toLowerCase();
      if (kw) {
        filtered = filtered.filter(r =>
          (r.restaurantName || '').toLowerCase().includes(kw) ||
          (r.restaurantAddress || '').toLowerCase().includes(kw)
        );
      }

      if (this.minRating > 0) {
        filtered = filtered.filter(r => this.getRating(r.restaurantId) >= this.minRating);
      }

      filtered = this.sortRestaurants(filtered);
      this.restaurants = filtered;
    }
  }

  sortItems(list: any[]): any[] {
    const arr = [...list];
    switch (this.sortBy) {
      case 'priceAsc':
        return arr.sort((a, b) => Number(a.itemPrice) - Number(b.itemPrice));
      case 'priceDesc':
        return arr.sort((a, b) => Number(b.itemPrice) - Number(a.itemPrice));
      case 'nameAsc':
        return arr.sort((a, b) => (a.itemName || '').localeCompare(b.itemName || ''));
      case 'ratingDesc':
        return arr.sort((a, b) => this.getRating(b.restaurantId) - this.getRating(a.restaurantId));
      default:
        return arr;
    }
  }

  sortRestaurants(list: any[]): any[] {
    const arr = [...list];
    switch (this.sortBy) {
      case 'nameAsc':
        return arr.sort((a, b) => (a.restaurantName || '').localeCompare(b.restaurantName || ''));
      case 'ratingDesc':
        return arr.sort((a, b) => this.getRating(b.restaurantId) - this.getRating(a.restaurantId));
      default:
        return arr;
    }
  }

  search(): void {
    this.activeSearchKeyword = this.searchKeyword;
    this.applyFilters();
  }

  onSearchChange(newValue: string): void {
    // Only react when user is deleting characters (new value is shorter than active)
    if (newValue.length < this.activeSearchKeyword.length) {
      this.activeSearchKeyword = newValue;
      this.applyFilters();
    }
  }

  increase(itemId: number): void {
    this.quantities[itemId] = (this.quantities[itemId] || 1) + 1;
  }

  decrease(itemId: number): void {
    if ((this.quantities[itemId] || 1) > 1) {
      this.quantities[itemId] = this.quantities[itemId] - 1;
    }
  }

  addToCart(item: any): void {
    const customerId = this.customerService.getCurrentCustomerId();
    const qty = this.quantities[item.itemId] || 1;
    this.customerService.addToCart(customerId, item.itemId, qty).subscribe({
      next: () => {
        this.messageType = 'success';
        this.message = `${qty} × ${item.itemName} added to cart`;
        setTimeout(() => this.message = '', 2200);
      },
      error: (err) => {
        this.messageType = 'error';
        this.message = err.error?.message || 'Failed to add item';
        setTimeout(() => this.message = '', 2500);
      }
    });
  }

  viewRestaurant(id: number): void {
    this.router.navigate(['/restaurant', id]);
  }

  goCart(): void { this.router.navigate(['/cart']); }
  goOrders(): void { this.router.navigate(['/orders']); }
  goAddresses(): void { this.router.navigate(['/addresses']); }
  goHome(): void { this.showItems(); }

  login(): void {
    this.router.navigate(['/auth']);
  }

  logout(): void {
    this.authService.logout();
    this.isLoggedIn = false;
    this.router.navigate(['/auth']);
  }

  goProfile(): void { this.router.navigate(['/profile']); }
}