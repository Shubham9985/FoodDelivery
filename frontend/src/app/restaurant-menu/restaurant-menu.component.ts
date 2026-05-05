import { CommonModule } from '@angular/common';
import { Component, OnInit, NgZone } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CustomerService } from '../services/customer.service';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-restaurant-menu',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './restaurant-menu.component.html',
  styleUrls: ['./restaurant-menu.component.css'],
  // Inline toast styles so the popup is GUARANTEED to render correctly,
  // regardless of any conflicts in the external CSS file.
  styles: [`
    .rm-toast {
      position: fixed;
      top: 90px;
      right: 2rem;
      padding: 1rem 1.5rem;
      border-radius: 12px;
      color: #fff;
      font-weight: 600;
      font-size: 0.95rem;
      z-index: 9999;
      min-width: 260px;
      max-width: 380px;
      box-shadow: 0 12px 40px rgba(20, 20, 40, 0.28);
      display: flex;
      align-items: center;
      gap: 0.6rem;
      opacity: 0;
      transform: translateX(120%);
      transition: opacity 0.25s ease, transform 0.3s cubic-bezier(0.16, 1, 0.3, 1);
      pointer-events: none;
    }
    .rm-toast.visible {
      opacity: 1;
      transform: translateX(0);
      pointer-events: auto;
    }
    .rm-toast.success { background: linear-gradient(135deg, #10b981 0%, #059669 100%); }
    .rm-toast.success::before { content: '✓'; font-size: 1.15rem; }
    .rm-toast.error   { background: linear-gradient(135deg, #ff4757 0%, #ef4444 100%); }
    .rm-toast.error::before { content: '⚠'; font-size: 1.15rem; }
  `]
})
export class RestaurantMenuComponent implements OnInit {
  restaurant: any = null;
  menuItems: any[] = [];
  loading = false;
  message = '';
  messageType: 'success' | 'error' | '' = '';
  toastVisible = false;
  isLoggedIn = false;

  private toastTimer: any = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private customerService: CustomerService,
    private authService: AuthService,
    private zone: NgZone,
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isAuthenticated();
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadRestaurant(id);
    this.loadMenu(id);
  }

  loadRestaurant(id: number): void {
    this.customerService.getRestaurantById(id).subscribe({
      next: (data) => this.restaurant = data,
      error: () => {}
    });
  }

  loadMenu(restaurantId: number): void {
    this.loading = true;
    this.customerService.getMenuByRestaurant(restaurantId).subscribe({
      next: (data) => { this.menuItems = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  /**
   * Show toast with explicit zone.run() to force change detection,
   * and use a `visible` class toggle (CSS opacity/transform) instead of
   * *ngIf so the DOM element is always mounted.
   */
  private showToast(text: string, type: 'success' | 'error', durationMs = 2500): void {
    this.zone.run(() => {
      if (this.toastTimer) {
        clearTimeout(this.toastTimer);
        this.toastTimer = null;
      }
      this.message = text;
      this.messageType = type;
      // tiny delay so the visible=false -> true transition actually animates
      this.toastVisible = false;
      setTimeout(() => {
        this.zone.run(() => {
          this.toastVisible = true;
        });
      }, 10);

      this.toastTimer = setTimeout(() => {
        this.zone.run(() => {
          this.toastVisible = false;
          this.message = '';
        });
      }, durationMs);
    });
  }

  addToCart(item: any): void {
    if (!this.authService.isAuthenticated()) {
      this.showToast('Please log in to add items to your cart', 'error');
      return;
    }
    const customerId = this.customerService.getCurrentCustomerId();
    this.customerService.addToCart(customerId, item.itemId, 1).subscribe({
      next: () => {
        this.showToast(`${item.itemName} added to cart 🛒`, 'success');
      },
      error: (err) => {
        const msg = err?.error?.message || 'Failed to add item to cart';
        this.showToast(msg, 'error');
      }
    });
  }

  goBack(): void { this.router.navigate(['/home']); }

  login(): void { this.router.navigate(['/auth']); }

  logout(): void {
    this.authService.logout();
    this.isLoggedIn = false;
    this.router.navigate(['/auth']);
  }
}