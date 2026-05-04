import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { getStoredUser } from '../../../utils/browser-storage';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  private authHeaders(): HttpHeaders {
    const user = getStoredUser() || {};
    const token = user?.token || '';
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // ─── RESTAURANTS ──────────────────────────────
  getAllRestaurants(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/restaurants`, { headers: this.authHeaders() });
  }
  getRestaurantById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/restaurants/${id}`, { headers: this.authHeaders() });
  }
  addRestaurant(payload: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/restaurants`, payload, { headers: this.authHeaders() });
  }
  updateRestaurant(id: number, payload: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/restaurants/${id}`, payload, { headers: this.authHeaders() });
  }
  deleteRestaurant(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/restaurants/${id}`, { headers: this.authHeaders(), responseType: 'text' });
  }

  // ─── MENU ITEMS ──────────────────────────────
  getAllMenuItems(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/menu-items`, { headers: this.authHeaders() });
  }
  getMenuItemById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/menu-items/${id}`, { headers: this.authHeaders() });
  }
  addMenuItem(payload: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/menu-items`, payload, { headers: this.authHeaders() });
  }
  updateMenuItem(id: number, payload: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/menu-items/${id}`, payload, { headers: this.authHeaders() });
  }
  deleteMenuItem(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/menu-items/${id}`, { headers: this.authHeaders(), responseType: 'text' });
  }

  // ─── DRIVERS ──────────────────────────────
  getAllDrivers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/drivers`, { headers: this.authHeaders() });
  }
  getDriverById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/drivers/${id}`, { headers: this.authHeaders() });
  }
  addDriver(payload: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/drivers`, payload, { headers: this.authHeaders() });
  }
  updateDriver(id: number, payload: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/drivers/${id}`, payload, { headers: this.authHeaders() });
  }
  deleteDriver(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/drivers/${id}`, { headers: this.authHeaders(), responseType: 'text' });
  }

  // ─── COUPONS ──────────────────────────────
  getAllCoupons(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/coupons`, { headers: this.authHeaders() });
  }
  getCouponById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/coupons/${id}`, { headers: this.authHeaders() });
  }
  addCoupon(payload: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/coupons`, payload, { headers: this.authHeaders() });
  }
  updateCoupon(id: number, payload: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/coupons/${id}`, payload, { headers: this.authHeaders() });
  }
  deleteCoupon(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/coupons/${id}`, { headers: this.authHeaders(), responseType: 'text' });
  }

  // ─── ORDERS ──────────────────────────────
  getAllOrders(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/orders`, { headers: this.authHeaders() });
  }
  getOrderById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/orders/${id}`, { headers: this.authHeaders() });
  }
  updateOrderStatus(id: number, status: string): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/orders/${id}/status?status=${status}`, {}, { headers: this.authHeaders() });
  }
  assignDriver(orderId: number, driverId: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/orders/${orderId}/driver/${driverId}`, {}, { headers: this.authHeaders() });
  }
  cancelOrder(id: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/orders/${id}/cancel`, {}, { headers: this.authHeaders() });
  }

  getAllCustomers(): Observable<any[]> {
  return this.http.get<any[]>(`${this.baseUrl}/customers`, { headers: this.authHeaders() });
}
getCustomerById(id: number): Observable<any> {
  return this.http.get<any>(`${this.baseUrl}/customers/${id}`, { headers: this.authHeaders() });
}
deleteCustomer(id: number): Observable<string> {
  return this.http.delete(`${this.baseUrl}/customers/${id}`, { headers: this.authHeaders(), responseType: 'text' });
}

}
