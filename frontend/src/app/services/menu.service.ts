import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { MenuItem } from "../models/menu-item.model";
import { environment } from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class MenuService {

  private baseUrl = `${environment.apiUrl}/menu-items`;

  constructor(private http: HttpClient) {}

  // ── CRUD ────────────────────────────────────────────────────────────────────

  addMenuItem(payload: Omit<MenuItem, 'itemId' | 'restaurantName' | 'orderItemIds'>): Observable<MenuItem> {
    return this.http.post<MenuItem>(this.baseUrl, payload);
  }

  getAll(): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(this.baseUrl);
  }

  getById(itemId: number): Observable<MenuItem> {
    return this.http.get<MenuItem>(`${this.baseUrl}/${itemId}`);
  }

  updateMenuItem(itemId: number, payload: Omit<MenuItem, 'itemId' | 'restaurantName' | 'orderItemIds'>): Observable<MenuItem> {
    return this.http.put<MenuItem>(`${this.baseUrl}/${itemId}`, payload);
  }

  deleteMenuItem(itemId: number): Observable<string> {
    return this.http.delete<string>(`${this.baseUrl}/${itemId}`);
  }

  // ── Search by name ──────────────────────────────────────────────────────────

  getByName(itemName: string): Observable<MenuItem> {
    const params = new HttpParams().set('name', itemName);
    return this.http.get<MenuItem>(`${this.baseUrl}/search/name`, { params });
  }

  searchByName(keyword: string): Observable<MenuItem[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<MenuItem[]>(`${this.baseUrl}/search/name/keyword`, { params });
  }

  // ── Search by price ─────────────────────────────────────────────────────────

  getByMaxPrice(price: number): Observable<MenuItem[]> {
    const params = new HttpParams().set('price', price);
    return this.http.get<MenuItem[]>(`${this.baseUrl}/search/price/max`, { params });
  }

  getByMinPrice(price: number): Observable<MenuItem[]> {
    const params = new HttpParams().set('price', price);
    return this.http.get<MenuItem[]>(`${this.baseUrl}/search/price/min`, { params });
  }

  getByPriceRange(min: number, max: number): Observable<MenuItem[]> {
    const params = new HttpParams().set('min', min).set('max', max);
    return this.http.get<MenuItem[]>(`${this.baseUrl}/search/price/range`, { params });
  }

  // ── By Restaurant ───────────────────────────────────────────────────────────

  getByRestaurant(restaurantId: number): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(`${this.baseUrl}/restaurant/${restaurantId}`);
  }

  getByRestaurantName(restaurantName: string): Observable<MenuItem[]> {
    const params = new HttpParams().set('name', restaurantName);
    return this.http.get<MenuItem[]>(`${this.baseUrl}/restaurant/name`, { params });
  }

  searchByNameInRestaurant(restaurantId: number, keyword: string): Observable<MenuItem[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<MenuItem[]>(`${this.baseUrl}/restaurant/${restaurantId}/search`, { params });
  }

  getByPriceRangeInRestaurant(restaurantId: number, min: number, max: number): Observable<MenuItem[]> {
    const params = new HttpParams().set('min', min).set('max', max);
    return this.http.get<MenuItem[]>(`${this.baseUrl}/restaurant/${restaurantId}/price-range`, { params });
  }

  checkExistsInRestaurant(restaurantId: number, itemName: string): Observable<boolean> {
    const params = new HttpParams().set('name', itemName);
    return this.http.get<boolean>(`${this.baseUrl}/restaurant/${restaurantId}/exists`, { params });
  }

  countByRestaurant(restaurantId: number): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/restaurant/${restaurantId}/count`);
  }

  // ── Sorted / JPQL ───────────────────────────────────────────────────────────

  getSortedByPriceAsc(restaurantId: number): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(`${this.baseUrl}/restaurant/${restaurantId}/sorted/price-asc`);
  }

  getSortedByPriceDesc(restaurantId: number): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(`${this.baseUrl}/restaurant/${restaurantId}/sorted/price-desc`);
  }

  getMostExpensive(restaurantId: number): Observable<MenuItem> {
    return this.http.get<MenuItem>(`${this.baseUrl}/restaurant/${restaurantId}/most-expensive`);
  }

  getCheapest(restaurantId: number): Observable<MenuItem> {
    return this.http.get<MenuItem>(`${this.baseUrl}/restaurant/${restaurantId}/cheapest`);
  }

  getItemsWithOrders(): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(`${this.baseUrl}/with-orders`);
  }

  getByOrderItemId(orderItemId: number): Observable<MenuItem> {
    return this.http.get<MenuItem>(`${this.baseUrl}/by-order-item/${orderItemId}`);
  }

  // ── Stats / Native Queries ──────────────────────────────────────────────────

  getStatsWithRestaurant(restaurantId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/stats/with-restaurant/${restaurantId}`);
  }

  getAvgPricePerRestaurant(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/stats/avg-price-per-restaurant`);
  }

  getOrderCount(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/stats/order-count`);
  }

  getMostOrdered(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/stats/most-ordered`);
  }
}