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

  addresses: any[] = [];
  selectedAddressId: number | null = null;

  // New-address form state
  showNewAddressForm = false;
  newAddress: any = {
    addressLine1: '',
    addressLine2: '',
    city: '',
    state: '',
    postalCode: '',
  };
  savingAddress = false;

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
      this.loadAddresses();
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

  loadAddresses(): void {
    const customerId = this.customerService.getCurrentCustomerId();
    this.customerService.getMyAddresses(customerId).subscribe({
      next: (data: any[]) => {
        this.addresses = data || [];
        if (this.addresses.length > 0 && this.selectedAddressId == null) {
          this.selectedAddressId = this.addresses[0].addressId;
        }
        if (this.addresses.length === 0) {
          this.showNewAddressForm = true;
        }
      },
      error: () => {
        this.addresses = [];
        this.showNewAddressForm = true;
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

  toggleNewAddressForm(): void {
    this.showNewAddressForm = !this.showNewAddressForm;
  }

  saveNewAddress(): void {
    const a = this.newAddress;
    if (
      !a.addressLine1?.trim() ||
      !a.city?.trim() ||
      !a.state?.trim() ||
      !a.postalCode?.trim()
    ) {
      this.messageType = 'error';
      this.message = 'Please fill in line 1, city, state and postal code';
      setTimeout(() => (this.message = ''), 2500);
      return;
    }

    const customerId = this.customerService.getCurrentCustomerId();
    const payload = {
      addressLine1: a.addressLine1.trim(),
      addressLine2: a.addressLine2?.trim() || '',
      city: a.city.trim(),
      state: a.state.trim(),
      postalCode: a.postalCode.trim(),
      customerId,
    };

    this.savingAddress = true;
    this.customerService.addAddress(payload).subscribe({
      next: (saved: any) => {
        this.savingAddress = false;
        this.addresses = [...this.addresses, saved];
        this.selectedAddressId = saved.addressId;
        this.showNewAddressForm = false;
        this.newAddress = {
          addressLine1: '',
          addressLine2: '',
          city: '',
          state: '',
          postalCode: '',
        };
        this.messageType = 'success';
        this.message = 'Address added';
        setTimeout(() => (this.message = ''), 2000);
      },
      error: (err) => {
        this.savingAddress = false;
        this.messageType = 'error';
        this.message = err.error?.message || 'Failed to save address';
        setTimeout(() => (this.message = ''), 2500);
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

  /**
   * Persist the order -> address association in browser storage.
   * Keyed by orderId so the orders page can pick it up after navigation.
   */
  private persistOrderAddress(orderId: number, address: any): void {
    try {
      if (typeof sessionStorage === 'undefined') return;
      const KEY = 'orderAddressMap';
      const raw = sessionStorage.getItem(KEY);
      const map = raw ? JSON.parse(raw) : {};
      map[orderId] = {
        addressId: address.addressId,
        addressLine1: address.addressLine1,
        addressLine2: address.addressLine2,
        city: address.city,
        state: address.state,
        postalCode: address.postalCode,
      };
      sessionStorage.setItem(KEY, JSON.stringify(map));
    } catch {
      /* ignore */
    }
  }

  checkout(): void {
    if (this.addresses.length === 0 || !this.selectedAddressId) {
      this.messageType = 'error';
      this.message = 'Please add and select a delivery address';
      setTimeout(() => (this.message = ''), 3000);
      return;
    }

    const customerId = this.customerService.getCurrentCustomerId();
    const subTotalAtCheckout = this.subTotal;
    const discountAtCheckout = this.discountAmount;
    const couponAtCheckout = this.appliedCouponCode;
    const couponIdAtCheckout = this.appliedCouponId;
    const chosenAddress = this.addresses.find(
      (a) => a.addressId === this.selectedAddressId,
    );

    this.customerService.placeOrder(customerId).subscribe({
      next: (order: any) => {
        if (order?.orderId && chosenAddress) {
          this.persistOrderAddress(order.orderId, chosenAddress);
        }

        const navigateWithToast = (
          toastMsg: string,
          toastType: 'success' | 'error',
        ) => {
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
          this.router.navigate(['/orders'], {
            state: {
              orderPricing: orderInfo,
              toast: { message: toastMsg, type: toastType },
            },
          });
        };

        if (couponIdAtCheckout && order?.orderId) {
          this.customerService
            .applyCoupon(order.orderId, couponIdAtCheckout)
            .subscribe({
              next: () =>
                navigateWithToast(
                  'Order placed & coupon applied! 🎉',
                  'success',
                ),
              error: (err) =>
                navigateWithToast(
                  'Order placed, but coupon failed: ' +
                    (err.error?.message || 'Invalid coupon'),
                  'error',
                ),
            });
        } else {
          navigateWithToast('Order placed successfully! 🎉', 'success');
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