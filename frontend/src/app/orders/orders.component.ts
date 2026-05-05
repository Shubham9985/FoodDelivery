import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CustomerService } from '../services/customer.service';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css'],
})
export class OrdersComponent implements OnInit {
  orders: any[] = [];
  menuMap: { [id: number]: any } = {};
  loading = false;
  message = '';
  messageType: 'success' | 'error' | '' = '';
  isLoggedIn = false;

  // { [orderId]: { subTotal, discount, couponCode, total } }
  private pricingMap: {
    [orderId: number]: {
      subTotal: number;
      discount: number;
      couponCode: string;
      total: number;
    };
  } = {};

  private readonly PRICING_KEY = 'orderPricingMap';

  constructor(
    private customerService: CustomerService,
    private authService: AuthService,
    private router: Router,
  ) {
    // Merge any pricing info passed from cart navigation with what's persisted.
    const nav = this.router.getCurrentNavigation();
    const incoming = nav?.extras?.state?.['orderPricing'] || {};
    this.pricingMap = {
      ...this.loadPersistedPricing(),
      ...incoming,
    };
    this.persistPricing();
  }

  private loadPersistedPricing(): any {
    try {
      if (typeof sessionStorage === 'undefined') return {};
      const raw = sessionStorage.getItem(this.PRICING_KEY);
      return raw ? JSON.parse(raw) : {};
    } catch {
      return {};
    }
  }

  private persistPricing(): void {
    try {
      if (typeof sessionStorage === 'undefined') return;
      sessionStorage.setItem(this.PRICING_KEY, JSON.stringify(this.pricingMap));
    } catch {
      /* ignore */
    }
  }

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isAuthenticated();
    if (this.isLoggedIn) {
      this.loadOrders();
    }
  }

  loadOrders(): void {
    this.loading = true;
    const customerId = this.customerService.getCurrentCustomerId();

    this.customerService.getAllMenuItems().subscribe({
      next: (allItems: any[]) => {
        this.menuMap = {};
        (allItems || []).forEach((m) => (this.menuMap[m.itemId] = m));

        this.customerService.getMyOrders(customerId).subscribe({
          next: (data) => {
            this.orders = (data || []).map((o: any) => {
              const subTotal = this.computeTotal(o.items || []);
              const pricing = this.pricingMap[o.orderId];
              const discount = pricing?.discount || 0;
              const couponCode = pricing?.couponCode || '';
              const finalTotal = pricing
                ? Math.max(0, subTotal - discount)
                : subTotal;
              return {
                ...o,
                enrichedItems: this.enrichOrderItems(o.items || []),
                subTotal,
                discount,
                couponCode,
                computedTotal: finalTotal,
              };
            });
            this.loading = false;
          },
          error: () => {
            this.loading = false;
          },
        });
      },
      error: () => {
        this.customerService.getMyOrders(customerId).subscribe({
          next: (data) => {
            this.orders = data || [];
            this.loading = false;
          },
          error: () => {
            this.loading = false;
          },
        });
      },
    });
  }

  enrichOrderItems(items: any[]): any[] {
    return items.map((it: any) => {
      const menu = this.menuMap[it.itemId] || {};
      return {
        itemId: it.itemId,
        quantity: it.quantity,
        itemName: menu.itemName || `Item #${it.itemId}`,
        itemPrice: menu.itemPrice || 0,
        itemImageUrl: menu.itemImageUrl || '',
        restaurantName: menu.restaurantName || '',
      };
    });
  }

  computeTotal(items: any[]): number {
    return items.reduce((sum, it) => {
      const menu = this.menuMap[it.itemId];
      const price = menu?.itemPrice || 0;
      return sum + price * (it.quantity || 0);
    }, 0);
  }

  cancelOrder(orderId: number): void {
    this.customerService.cancelOrder(orderId).subscribe({
      next: () => {
        this.messageType = 'success';
        this.message = 'Order cancelled';
        this.loadOrders();
        setTimeout(() => (this.message = ''), 2000);
      },
      error: (err) => {
        this.messageType = 'error';
        this.message = err.error?.message || 'Cancel failed';
        setTimeout(() => (this.message = ''), 2500);
      },
    });
  }

  statusClass(status: string): string {
    if (!status) return '';
    const s = status.toLowerCase();
    if (s.includes('placed')) return 'placed';
    if (s.includes('cancel')) return 'cancelled';
    if (s.includes('deliver')) return 'delivered';
    return 'progress';
  }

  goToLogin(): void {
    this.router.navigate(['/auth']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth']);
  }
}