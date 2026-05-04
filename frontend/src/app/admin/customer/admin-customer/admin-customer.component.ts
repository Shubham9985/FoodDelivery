import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AdminService } from '../../services/admin.service';

interface CustomerRow {
  customerId: number;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  userId: number;
  orderCount: number;
  totalSpent: number;
  lastOrderDate: string | null;
  addressCount: number;
}

@Component({
  selector: 'app-admin-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-customer.component.html',
  styleUrls: ['./admin-customer.component.css']
})
export class AdminCustomerComponent implements OnInit {
  customers: CustomerRow[] = [];
  allOrders: any[] = [];
  menuItemsMap: { [itemId: number]: { name: string; price: number } } = {};

  loading = false;
  message = '';
  messageType: 'success' | 'error' | '' = '';

  searchTerm = '';
  sortBy: 'name' | 'orders' | 'spent' | 'recent' = 'orders';

  selectedCustomer: CustomerRow | null = null;
  selectedCustomerOrders: any[] = [];

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.loading = true;
    forkJoin({
      customers: this.adminService.getAllCustomers().pipe(catchError(() => of([]))),
      orders: this.adminService.getAllOrders().pipe(catchError(() => of([]))),
      menuItems: this.adminService.getAllMenuItems().pipe(catchError(() => of([])))
    }).subscribe({
      next: (res) => {
        res.menuItems.forEach((it: any) => {
          this.menuItemsMap[it.itemId] = { name: it.itemName, price: it.itemPrice };
        });
        this.allOrders = res.orders || [];
        this.customers = (res.customers || []).map((c: any) => this.buildCustomerRow(c));
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.showMessage(err.error?.message || 'Failed to load customers', 'error');
      }
    });
  }

  private buildCustomerRow(c: any): CustomerRow {
    const customerOrders = this.allOrders.filter(o => o.customerId === c.customerId);
    let totalSpent = 0;
    let lastOrderDate: string | null = null;

    customerOrders.forEach(o => {
      if (o.orderStatus !== 'CANCELLED' && o.items) {
        o.items.forEach((it: any) => {
          const price = this.menuItemsMap[it.itemId]?.price || 0;
          totalSpent += price * (it.quantity || 0);
        });
      }
      if (!lastOrderDate || (o.orderDate && o.orderDate > lastOrderDate)) {
        lastOrderDate = o.orderDate;
      }
    });

    return {
      customerId: c.customerId,
      customerName: c.customerName,
      customerEmail: c.customerEmail,
      customerPhone: c.customerPhone,
      userId: c.userId,
      orderCount: customerOrders.length,
      totalSpent,
      lastOrderDate,
      addressCount: c.addresses ? c.addresses.length : 0
    };
  }

  get filteredCustomers(): CustomerRow[] {
    const term = this.searchTerm.trim().toLowerCase();
    let list = this.customers;

    if (term) {
      list = list.filter(c =>
        c.customerName.toLowerCase().includes(term) ||
        c.customerEmail.toLowerCase().includes(term) ||
        c.customerPhone.includes(term)
      );
    }

    const sorted = [...list];
    switch (this.sortBy) {
      case 'name':
        sorted.sort((a, b) => a.customerName.localeCompare(b.customerName));
        break;
      case 'orders':
        sorted.sort((a, b) => b.orderCount - a.orderCount);
        break;
      case 'spent':
        sorted.sort((a, b) => b.totalSpent - a.totalSpent);
        break;
      case 'recent':
        sorted.sort((a, b) => {
          if (!a.lastOrderDate) return 1;
          if (!b.lastOrderDate) return -1;
          return b.lastOrderDate.localeCompare(a.lastOrderDate);
        });
        break;
    }
    return sorted;
  }

  get totalCustomers(): number {
    return this.customers.length;
  }

  get activeCustomers(): number {
    return this.customers.filter(c => c.orderCount > 0).length;
  }

  get totalRevenue(): number {
    return this.customers.reduce((sum, c) => sum + c.totalSpent, 0);
  }

  viewDetails(customer: CustomerRow): void {
    this.selectedCustomer = customer;
    this.selectedCustomerOrders = this.allOrders
      .filter(o => o.customerId === customer.customerId)
      .sort((a, b) => (b.orderDate || '').localeCompare(a.orderDate || ''));
  }

  closeDetails(): void {
    this.selectedCustomer = null;
    this.selectedCustomerOrders = [];
  }

  getItemName(itemId: number): string {
    return this.menuItemsMap[itemId]?.name || `Item #${itemId}`;
  }

  getOrderTotal(order: any): number {
    if (!order.items) return 0;
    return order.items.reduce((sum: number, it: any) => {
      const price = this.menuItemsMap[it.itemId]?.price || 0;
      return sum + price * (it.quantity || 0);
    }, 0);
  }

  deleteCustomer(customer: CustomerRow, event: Event): void {
    event.stopPropagation();
    if (!confirm(`Delete customer "${customer.customerName}"? This cannot be undone.`)) return;
    this.adminService.deleteCustomer(customer.customerId).subscribe({
      next: () => {
        this.showMessage('Customer deleted', 'success');
        this.loadAll();
      },
      error: (err) => this.showMessage(err.error?.message || 'Delete failed', 'error')
    });
  }

  private showMessage(text: string, type: 'success' | 'error'): void {
    this.message = text;
    this.messageType = type;
    setTimeout(() => { this.message = ''; this.messageType = ''; }, 2500);
  }
}