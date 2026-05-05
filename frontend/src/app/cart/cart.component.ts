import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CustomerService } from '../services/customer.service';
import { AuthService } from '../auth.service';

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
  isLoggedIn = false;

  couponCode: string = '';
  appliedCouponCode: string = '';
  appliedCouponId: number | null = null;
  discountAmount: number = 0;

  availableCoupons: any[] = [];

  constructor(
    private customerService: CustomerService,
    private router: Router,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isAuthenticated();
    if (this.isLoggedIn) {
      this.loadCart();
      this.loadAvailableCoupons();
    }
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

  loadAvailableCoupons(): void {
    this.customerService.getAllCoupons().subscribe({
      next: (coupons: any[]) => {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        this.availableCoupons = (coupons || []).filter((c) => {
          if (!c?.expiryDate) return true;
          const exp = new Date(c.expiryDate);
          return exp >= today;
        });
      },
      error: () => {
        this.availableCoupons = [];
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

  useCoupon(code: string): void {
    this.couponCode = code;
    this.applyCouponCode();
  }

  applyCouponCode(): void {
    const code = (this.couponCode || '').trim();
    if (!code) return;

    this.customerService.getCouponByCode(code).subscribe({
      next: (coupon: any) => {
        const discount = Number(coupon?.discountAmount) || 0;
        if (discount <= 0 || !coupon?.couponId) {
          this.messageType = 'error';
          this.message = 'Invalid coupon';
          setTimeout(() => (this.message = ''), 2500);
          return;
        }
        this.appliedCouponCode = coupon.couponCode || code;
        this.appliedCouponId = coupon.couponId;
        this.discountAmount = discount;
        this.messageType = 'success';
        this.message = `Coupon "${this.appliedCouponCode}" applied! ₹${discount} off`;
        setTimeout(() => (this.message = ''), 2500);
      },
      error: (err) => {
        this.appliedCouponCode = '';
        this.appliedCouponId = null;
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
    this.appliedCouponId = null;
    this.discountAmount = 0;
  }

  checkout(): void {
    const customerId = this.customerService.getCurrentCustomerId();
    const subTotalAtCheckout = this.subTotal;
    const discountAtCheckout = this.discountAmount;
    const couponAtCheckout = this.appliedCouponCode;
    const couponIdAtCheckout = this.appliedCouponId;

    this.customerService.placeOrder(customerId).subscribe({
      next: (order: any) => {
        const finalize = (msg: string) => {
          this.messageType = 'success';
          this.message = msg;
          const orderInfo = order?.orderId
            ? {
                [order.orderId]: {
                  subTotal: subTotalAtCheckout,
                  discount: discountAtCheckout,
                  couponCode: couponAtCheckout,
                  total: Math.max(0, subTotalAtCheckout - discountAtCheckout),
                },
              }
            : {};
          setTimeout(
            () =>
              this.router.navigate(['/orders'], {
                state: { orderPricing: orderInfo },
              }),
            1800,
          );
        };

        if (couponIdAtCheckout && order?.orderId) {
          this.customerService
            .applyCoupon(order.orderId, couponIdAtCheckout)
            .subscribe({
              next: () => finalize('Order placed & coupon applied! 🎉'),
              error: (err) => {
                this.messageType = 'error';
                this.message =
                  'Order placed, but coupon failed: ' +
                  (err.error?.message || 'Invalid coupon');
                setTimeout(() => this.router.navigate(['/orders']), 2200);
              },
            });
        } else {
          finalize('Order placed successfully! 🎉');
        }
      },
      error: (err) => {
        this.messageType = 'error';
        this.message = err.error?.message || 'Failed to place order';
        setTimeout(() => (this.message = ''), 2500);
      },
    });
  }

  goToLogin(): void {
    this.router.navigate(['/auth']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth']);
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