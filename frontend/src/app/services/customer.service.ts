import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { getStoredUser, isBrowser } from '../utils/browser-storage';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  private authHeaders(): HttpHeaders {
    const user = getStoredUser() || {};
    return new HttpHeaders({ Authorization: `Bearer ${user.token || ''}` });
  }

  getCurrentCustomerId(): number {
    const user = getStoredUser() || {};
    return user.customerId || 1;
  }

  // Restaurants
  getAllRestaurants(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/restaurants`, { headers: this.authHeaders() });
  }

  getRestaurantById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/restaurants/${id}`, { headers: this.authHeaders() });
  }

  searchRestaurantsByMenu(keyword: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/restaurants/search/menu-item-name?keyword=${keyword}`, { headers: this.authHeaders() });
  }

  // Menu Items
  getMenuByRestaurant(restaurantId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/menu-items/restaurant/${restaurantId}`, { headers: this.authHeaders() });
  }

  // Cart
  getCart(customerId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/cart/${customerId}`, { headers: this.authHeaders() });
  }

  addToCart(customerId: number, itemId: number, quantity: number): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/cart/${customerId}/items`, { itemId, quantity }, { headers: this.authHeaders() });
  }

  updateCartItem(customerId: number, itemId: number, quantity: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/cart/${customerId}/items`, { itemId, quantity }, { headers: this.authHeaders() });
  }

  removeFromCart(customerId: number, itemId: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/cart/${customerId}/items/${itemId}`, { headers: this.authHeaders() });
  }

  clearCart(customerId: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/cart/${customerId}`, { headers: this.authHeaders() });
  }

  // Orders
  placeOrder(customerId: number): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/orders/place/${customerId}`, {}, { headers: this.authHeaders() });
  }

  getMyOrders(customerId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/orders/customer/${customerId}`, { headers: this.authHeaders() });
  }

  cancelOrder(orderId: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/orders/${orderId}/cancel`, {}, { headers: this.authHeaders() });
  }

 // Coupons
  applyCoupon(orderId: number, code: string): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/orders/${orderId}/coupon/${code}`, {}, { headers: this.authHeaders() });
  }

  getCouponByCode(code: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/coupons/code/${code}`, { headers: this.authHeaders() });
  }

  // Addresses
  getMyAddresses(customerId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/addresses/customer/${customerId}`, { headers: this.authHeaders() });
  }

  addAddress(payload: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/addresses`, payload, { headers: this.authHeaders() });
  }

  getAllMenuItems(): Observable<any[]> {
  return this.http.get<any[]>(`${this.baseUrl}/menu-items`, { headers: this.authHeaders() });
}

getAverageRating(restaurantId: number): Observable<number> {
  return this.http.get<number>(`${this.baseUrl}/ratings/restaurant/${restaurantId}/average`, { headers: this.authHeaders() });
}

getProfile(customerId: number): Observable<any> {
  return this.http.get<any>(`${this.baseUrl}/customers/${customerId}`, { headers: this.authHeaders() });
}

updateProfile(customerId: number, payload: any): Observable<any> {
  return this.http.put<any>(`${this.baseUrl}/customers/${customerId}`, payload, { headers: this.authHeaders() });
}
}