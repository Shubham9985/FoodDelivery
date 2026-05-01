import { HttpClient } from "@angular/common/http";
import { MenuItem } from "../models/menu-item.model";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class MenuService {

  private baseUrl = 'http://localhost:8080/api/menu-items';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<MenuItem[]>(this.baseUrl);
  }

  getByRestaurant(restaurantId: number) {
    return this.http.get<MenuItem[]>(`${this.baseUrl}/restaurant/${restaurantId}`);
  }
}