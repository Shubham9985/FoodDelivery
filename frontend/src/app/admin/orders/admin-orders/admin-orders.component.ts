import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin-services/admin.service';
@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-orders.component.html',
  styleUrls: ['./admin-orders.component.css']
})
export class AdminOrdersComponent implements OnInit {
  orders: any[] = [];
  drivers: any[] = [];
  menuItemsMap: { [itemId: number]: string } = {};
  loading = false;
  message = '';
  messageType: 'success' | 'error' | '' = '';
  filterStatus = 'ALL';

  statusOptions = ['PLACED', 'CONFIRMED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED'];

  selectedDriver: { [orderId: number]: number | null } = {};
  selectedStatus: { [orderId: number]: string } = {};

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadMenuItems();
    this.loadOrders();
    this.loadDrivers();
  }

  loadMenuItems(): void {
    this.adminService.getAllMenuItems().subscribe({
      next: (items) => {
        items.forEach(it => { this.menuItemsMap[it.itemId] = it.itemName; });
      },
      error: () => {}
    });
  }

  loadOrders(): void {
    this.loading = true;
    this.adminService.getAllOrders().subscribe({
      next: (data) => {
        this.orders = data;
        data.forEach(o => {
          this.selectedDriver[o.orderId] = o.driverId || null;
          this.selectedStatus[o.orderId] = o.orderStatus || 'PLACED';
        });
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.showMessage(err.error?.message || 'Failed to load orders', 'error');
      }
    });
  }

  loadDrivers(): void {
    this.adminService.getAllDrivers().subscribe({
      next: (data) => { this.drivers = data; },
      error: () => {}
    });
  }

  get filteredOrders(): any[] {
    if (this.filterStatus === 'ALL') return this.orders;
    return this.orders.filter(o => o.orderStatus === this.filterStatus);
  }

  isFinalized(status: string): boolean {
    return status === 'DELIVERED' || status === 'CANCELLED';
  }

  getItemName(itemId: number): string {
    return this.menuItemsMap[itemId] || `Item #${itemId}`;
  }

  updateStatus(orderId: number): void {
    const status = this.selectedStatus[orderId];
    if (!status) return;
    this.adminService.updateOrderStatus(orderId, status).subscribe({
      next: () => {
        this.showMessage('Status updated', 'success');
        this.loadOrders();
      },
      error: (err) => this.showMessage(err.error?.message || 'Update failed', 'error')
    });
  }

  assignDriver(orderId: number): void {
    const driverId = this.selectedDriver[orderId];
    if (!driverId) {
      this.showMessage('Please select a driver', 'error');
      return;
    }
    this.adminService.assignDriver(orderId, driverId).subscribe({
      next: () => {
        this.showMessage('Driver assigned', 'success');
        this.loadOrders();
      },
      error: (err) => this.showMessage(err.error?.message || 'Assignment failed', 'error')
    });
  }

  cancelOrder(orderId: number): void {
    if (!confirm('Cancel this order?')) return;
    this.adminService.cancelOrder(orderId).subscribe({
      next: () => {
        this.showMessage('Order cancelled', 'success');
        this.loadOrders();
      },
      error: (err) => this.showMessage(err.error?.message || 'Cancel failed', 'error')
    });
  }

  getDriverName(driverId: number | null | undefined): string {
    if (!driverId) return 'Unassigned';
    const d = this.drivers.find(x => x.driverId === driverId);
    return d ? d.driverName : `Driver ${driverId}`;
  }

  private showMessage(text: string, type: 'success' | 'error'): void {
    this.message = text;
    this.messageType = type;
    setTimeout(() => { this.message = ''; this.messageType = ''; }, 2500);
  }
}