import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Cart } from '../models/cart.model';

@Injectable({
  providedIn: 'root'
})
export class CartService {

  private baseUrl = 'http://localhost:8080/api/cart';

  constructor(private http: HttpClient) {}

  getCart(customerId: number) {
    return this.http.get<Cart>(`${this.baseUrl}/${customerId}`);
  }

  addItem(customerId: number, itemId: number, quantity: number) {
    return this.http.post<Cart>(`${this.baseUrl}/add`, {
      customerId,
      itemId,
      quantity
    });
  }

  updateItem(customerId: number, itemId: number, quantity: number) {
    return this.http.put<Cart>(`${this.baseUrl}/update`, {
      customerId,
      itemId,
      quantity
    });
  }

  removeItem(customerId: number, itemId: number) {
    return this.http.delete<Cart>(`${this.baseUrl}/remove/${customerId}/${itemId}`);
  }

  clearCart(customerId: number) {
    return this.http.delete<Cart>(`${this.baseUrl}/clear/${customerId}`);
  }
}