import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CustomerService } from '../services/customer.service';

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

  constructor(private customerService: CustomerService) {}

  ngOnInit(): void {
    this.loadOrders();
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
            this.orders = (data || []).map((o: any) => ({
              ...o,
              enrichedItems: this.enrichOrderItems(o.items || []),
              computedTotal: this.computeTotal(o.items || []),
            }));
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
}
