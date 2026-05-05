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

  // { [orderId]: { addressId, addressLine1, addressLine2, city, state, postalCode } }
  private addressByOrder: { [orderId: number]: any } = {};

  private readonly PRICING_KEY = 'orderPricingMap';
  private readonly ADDRESS_KEY = 'orderAddressMap';
  private incomingToast: { message: string; type: 'success' | 'error' } | null =
    null;

  constructor(
    private customerService: CustomerService,
    private authService: AuthService,
    private router: Router,
  ) {
    const nav = this.router.getCurrentNavigation();
    const state = nav?.extras?.state || {};
    const incoming = state['orderPricing'] || {};
    this.incomingToast = state['toast'] || null;

    this.pricingMap = {
      ...this.loadJson(this.PRICING_KEY),
      ...incoming,
    };
    this.persistJson(this.PRICING_KEY, this.pricingMap);

    this.addressByOrder = this.loadJson(this.ADDRESS_KEY);
  }

  private loadJson(key: string): any {
    try {
      if (typeof sessionStorage === 'undefined') return {};
      const raw = sessionStorage.getItem(key);
      return raw ? JSON.parse(raw) : {};
    } catch {
      return {};
    }
  }

  private persistJson(key: string, value: any): void {
    try {
      if (typeof sessionStorage === 'undefined') return;
      sessionStorage.setItem(key, JSON.stringify(value));
    } catch {
      /* ignore */
    }
  }

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isAuthenticated();
    if (this.isLoggedIn) {
      this.loadOrders();
    }

    if (this.incomingToast?.message) {
      this.messageType = this.incomingToast.type;
      this.message = this.incomingToast.message;
      setTimeout(() => {
        this.message = '';
        this.messageType = '';
      }, 3000);
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
                deliveryAddress: this.formatAddress(
                  this.addressByOrder[o.orderId],
                ),
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

  formatAddress(a: any): string {
    if (!a) return '';
    const parts = [
      a.addressLine1,
      a.addressLine2,
      a.city,
      a.state,
      a.postalCode,
    ].filter((p) => p && String(p).trim().length > 0);
    return parts.join(', ');
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