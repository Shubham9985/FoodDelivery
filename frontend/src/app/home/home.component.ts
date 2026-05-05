import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
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

  private toastTimer: any = null;

  constructor(
    private customerService: CustomerService,
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
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
      if (this.minPrice != null) filtered = filtered.filter(i => Number(i.itemPrice) >= this.minPrice!);
      if (this.maxPrice != null) filtered = filtered.filter(i => Number(i.itemPrice) <= this.maxPrice!);
      if (this.minRating > 0) filtered = filtered.filter(i => this.getRating(i.restaurantId) >= this.minRating);
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
      if (this.minRating > 0) filtered = filtered.filter(r => this.getRating(r.restaurantId) >= this.minRating);
      filtered = this.sortRestaurants(filtered);
      this.restaurants = filtered;
    }
  }

  sortItems(list: any[]): any[] {
    const arr = [...list];
    switch (this.sortBy) {
      case 'priceAsc': return arr.sort((a, b) => Number(a.itemPrice) - Number(b.itemPrice));
      case 'priceDesc': return arr.sort((a, b) => Number(b.itemPrice) - Number(a.itemPrice));
      case 'nameAsc': return arr.sort((a, b) => (a.itemName || '').localeCompare(b.itemName || ''));
      case 'ratingDesc': return arr.sort((a, b) => this.getRating(b.restaurantId) - this.getRating(a.restaurantId));
      default: return arr;
    }
  }

  sortRestaurants(list: any[]): any[] {
    const arr = [...list];
    switch (this.sortBy) {
      case 'nameAsc': return arr.sort((a, b) => (a.restaurantName || '').localeCompare(b.restaurantName || ''));
      case 'ratingDesc': return arr.sort((a, b) => this.getRating(b.restaurantId) - this.getRating(a.restaurantId));
      default: return arr;
    }
  }

  search(): void {
    this.activeSearchKeyword = this.searchKeyword;
    this.applyFilters();
  }

  onSearchChange(newValue: string): void {
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

  /**
   * Centralized toast helper. Clears any existing timer, sets the message,
   * forces change detection, and auto-dismisses after the given duration.
   */
  private showToast(text: string, type: 'success' | 'error', durationMs = 2500): void {
    if (this.toastTimer) {
      clearTimeout(this.toastTimer);
      this.toastTimer = null;
    }
    // Reset first so a repeated message re-triggers the slide-in animation
    this.message = '';
    this.messageType = '';
    this.cdr.detectChanges();

    this.messageType = type;
    this.message = text;
    this.cdr.detectChanges();

    this.toastTimer = setTimeout(() => {
      this.message = '';
      this.messageType = '';
      this.cdr.detectChanges();
    }, durationMs);
  }

  addToCart(item: any): void {
    // Require login before hitting the cart endpoint (cart is CUSTOMER-only on backend).
    if (!this.authService.isAuthenticated()) {
      this.showToast('Please log in to add items to your cart', 'error');
      return;
    }

    const customerId = this.customerService.getCurrentCustomerId();
    const qty = this.quantities[item.itemId] || 1;

    this.customerService.addToCart(customerId, item.itemId, qty).subscribe({
      next: () => {
        this.showToast(`${qty} × ${item.itemName} added to cart 🛒`, 'success');
      },
      error: (err) => {
        const msg = err?.error?.message || 'Failed to add item to cart';
        this.showToast(msg, 'error');
      }
    });
  }

  viewRestaurant(id: number): void { this.router.navigate(['/restaurant', id]); }
  goCart(): void { this.router.navigate(['/cart']); }
  goOrders(): void { this.router.navigate(['/orders']); }
  goAddresses(): void { this.router.navigate(['/addresses']); }
  goHome(): void { this.showItems(); }
  login(): void { this.router.navigate(['/auth']); }

  logout(): void {
    this.authService.logout();
    this.isLoggedIn = false;
    this.router.navigate(['/auth']);
  }

  goProfile(): void { this.router.navigate(['/profile']); }
}