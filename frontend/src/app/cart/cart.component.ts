import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css'],
})
export class CartComponent implements OnInit {
  cart: any = null;
  enrichedItems: any[] = [];
  loading = false;
  message = '';
  messageType: 'success' | 'error' | '' = '';

  couponCode: string = '';
  appliedCouponCode: string = '';
  discountAmount: number = 0;

  constructor(
    private customerService: CustomerService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.loading = true;
    const customerId = this.customerService.getCurrentCustomerId();
    this.customerService.getCart(customerId).subscribe({
      next: (data) => {
        this.cart = data;
        this.enrichItems();
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  enrichItems(): void {
    const items = this.cart?.items || [];
    if (items.length === 0) {
      this.enrichedItems = [];
      this.loading = false;
      return;
    }

    this.customerService.getAllMenuItems().subscribe({
      next: (allItems: any[]) => {
        this.enrichedItems = items
          .map((ci: any) => {
            const menu =
              allItems.find((m: any) => m.itemId === ci.itemId) || {};
            return {
              itemId: ci.itemId,
              quantity: ci.quantity,
              itemName: menu.itemName || `Item #${ci.itemId}`,
              itemDescription: menu.itemDescription || '',
              itemPrice: menu.itemPrice || 0,
              itemImageUrl: menu.itemImageUrl || '',
              restaurantName: menu.restaurantName || '',
            };
          })
          .sort((a: any, b: any) => a.itemId - b.itemId);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  updateQty(itemId: number, qty: number): void {
    if (qty < 1) return;
    const customerId = this.customerService.getCurrentCustomerId();
    this.customerService.updateCartItem(customerId, itemId, qty).subscribe({
      next: (data) => {
        this.cart = data;
        this.enrichItems();
      },
    });
  }

  removeItem(itemId: number): void {
    const customerId = this.customerService.getCurrentCustomerId();
    this.customerService.removeFromCart(customerId, itemId).subscribe({
      next: (data) => {
        this.cart = data;
        this.enrichItems();
      },
    });
  }

  clearCart(): void {
    const customerId = this.customerService.getCurrentCustomerId();
    this.customerService.clearCart(customerId).subscribe({
      next: (data) => {
        this.cart = data;
        this.enrichItems();
        this.clearCoupon();
      },
    });
  }

  applyCouponCode(): void {
    const code = (this.couponCode || '').trim();
    if (!code) return;

    this.customerService.getCouponByCode(code).subscribe({
      next: (coupon: any) => {
        const discount = Number(coupon?.discountAmount) || 0;
        if (discount <= 0) {
          this.messageType = 'error';
          this.message = 'Invalid coupon';
          setTimeout(() => (this.message = ''), 2500);
          return;
        }
        this.appliedCouponCode = code;
        this.discountAmount = discount;
        this.messageType = 'success';
        this.message = `Coupon "${code}" applied! ₹${discount} off`;
        setTimeout(() => (this.message = ''), 2500);
      },
      error: (err) => {
        this.appliedCouponCode = '';
        this.discountAmount = 0;
        this.messageType = 'error';
        this.message = err.error?.message || 'Invalid coupon';
        setTimeout(() => (this.message = ''), 2500);
      },
    });
  }

  clearCoupon(): void {
    this.couponCode = '';
    this.appliedCouponCode = '';
    this.discountAmount = 0;
  }

  checkout(): void {
    const customerId = this.customerService.getCurrentCustomerId();
    this.customerService.placeOrder(customerId).subscribe({
      next: (order: any) => {
        if (this.appliedCouponCode && order?.orderId) {
          this.customerService
            .applyCoupon(order.orderId, this.appliedCouponCode)
            .subscribe({
              next: () => {
                this.messageType = 'success';
                this.message = 'Order placed & coupon applied!';
                setTimeout(() => this.router.navigate(['/orders']), 1500);
              },
              error: (err) => {
                this.messageType = 'error';
                this.message =
                  'Order placed, but coupon failed: ' +
                  (err.error?.message || 'Invalid coupon');
                setTimeout(() => this.router.navigate(['/orders']), 2000);
              },
            });
        } else {
          this.messageType = 'success';
          this.message = 'Order placed successfully!';
          setTimeout(() => this.router.navigate(['/orders']), 1500);
        }
      },
      error: (err) => {
        this.messageType = 'error';
        this.message = err.error?.message || 'Failed to place order';
      },
    });
  }

  get itemCount(): number {
    return this.enrichedItems?.length || 0;
  }

  get subTotal(): number {
    return this.enrichedItems.reduce(
      (sum, it) => sum + it.itemPrice * it.quantity,
      0,
    );
  }

  get cartTotal(): number {
    return Math.max(0, this.subTotal - this.discountAmount);
  }
}
