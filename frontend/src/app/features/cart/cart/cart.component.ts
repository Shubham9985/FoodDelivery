import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from '../../../services/cart.service';
import { Cart } from '../../../models/cart.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cart.component.html'
})
export class CartComponent {

  cart: Cart | null = null;
  customerId = 1; // temporary (later from login)

  constructor(private cartService: CartService) {}

  ngOnInit() {
    this.loadCart();
  }

  loadCart() {
    this.cartService.getCart(this.customerId).subscribe(res => {
      this.cart = res;
    });
  }

  remove(itemId: number) {
    this.cartService.removeItem(this.customerId, itemId).subscribe(() => {
      this.loadCart();
    });
  }

  clear() {
    this.cartService.clearCart(this.customerId).subscribe(() => {
      this.loadCart();
    });
  }
}