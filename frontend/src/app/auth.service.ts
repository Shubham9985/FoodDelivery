import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { getStoredUser, isBrowser } from './utils/browser-storage';

export interface RegisterPayload {
  name: string;
  email: string;
  phone: string;
  password: string;
  role: 'CUSTOMER' | 'ADMIN';
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  role: 'CUSTOMER' | 'ADMIN';
  message: string;
  name?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  getCurrentUser(): AuthResponse | null {
    return getStoredUser();
  }

  isAuthenticated(): boolean {
    return !!this.getCurrentUser()?.token;
  }

  logout(): void {
    if (!isBrowser()) {
      return;
    }
    window.localStorage.removeItem('user');
  }

  register(payload: RegisterPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/register`, payload);
  }

  login(payload: LoginPayload): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, payload);
  }
}