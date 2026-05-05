import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { isBrowser } from '../utils/browser-storage';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css'],
  // Inline fallback styles — guaranteed to load even if the .css file path
  // is misconfigured or the dev server fails to pick up the external CSS.
  styles: [`
    :host { display: block; min-height: 100vh; }

    .auth-wrapper {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background:
        radial-gradient(ellipse at top left, #ffe8e0 0%, transparent 55%),
        radial-gradient(ellipse at bottom right, #ffd1c1 0%, transparent 55%),
        linear-gradient(135deg, #ff4757 0%, #ff6b35 100%);
      padding: 20px;
      font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
      letter-spacing: -0.01em;
      box-sizing: border-box;
    }
    .auth-card {
      background: #fff;
      width: 100%;
      max-width: 440px;
      border-radius: 20px;
      padding: 40px 36px;
      box-shadow: 0 24px 60px rgba(20, 20, 40, 0.25);
      border: 1px solid #ececf0;
      animation: slideUp 0.4s ease-out;
      box-sizing: border-box;
    }
    @keyframes slideUp {
      from { opacity: 0; transform: translateY(20px); }
      to   { opacity: 1; transform: translateY(0); }
    }
    .brand { text-align: center; margin-bottom: 28px; }
    .brand .logo { font-size: 48px; margin-bottom: 8px; }
    .brand h1 {
      margin: 0;
      font-size: 28px;
      font-weight: 800;
      background: linear-gradient(135deg, #ff4757 0%, #ff6b35 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      letter-spacing: -0.03em;
    }
    .brand p { color: #6b7280; font-size: 14px; margin-top: 6px; }

    .auth-form { display: flex; flex-direction: column; gap: 16px; }
    .field { display: flex; flex-direction: column; }
    .field label {
      font-size: 13px;
      font-weight: 600;
      color: #1a1a2e;
      margin-bottom: 6px;
    }
    .field input {
      padding: 12px 14px;
      border: 1.5px solid #ececf0;
      border-radius: 10px;
      font-size: 14px;
      font-family: inherit;
      outline: none;
      transition: all 0.2s;
      background: #f7f7f9;
      color: #1a1a2e;
      box-sizing: border-box;
    }
    .field input:focus {
      border-color: #ff4757;
      background: #fff;
      box-shadow: 0 0 0 3px rgba(255, 71, 87, 0.15);
    }
    .field small { color: #ff4757; font-size: 12px; margin-top: 4px; }

    .primary-btn {
      margin-top: 8px;
      padding: 13px;
      border: none;
      border-radius: 10px;
      background: linear-gradient(135deg, #ff4757 0%, #ff6b35 100%);
      color: #fff;
      font-size: 15px;
      font-family: inherit;
      font-weight: 600;
      cursor: pointer;
      transition: transform 0.18s ease, box-shadow 0.18s ease;
    }
    .primary-btn:hover:not(:disabled) {
      transform: translateY(-1px);
      box-shadow: 0 8px 20px rgba(255, 71, 87, 0.4);
    }
    .primary-btn:disabled { opacity: 0.6; cursor: not-allowed; }

    .switch { text-align: center; font-size: 14px; color: #6b7280; margin-top: 8px; }
    .switch a {
      color: #ff4757;
      font-weight: 600;
      cursor: pointer;
      text-decoration: none;
    }
    .switch a:hover { text-decoration: underline; }
    .switch.admin-link {
      margin-top: 4px;
      padding-top: 14px;
      border-top: 1px dashed #ececf0;
    }
    .switch.admin-link a {
      color: #1a1a2e;
      font-weight: 700;
    }

    .alert {
      padding: 12px 14px;
      border-radius: 10px;
      font-size: 14px;
      font-weight: 500;
      margin-bottom: 16px;
      text-align: center;
    }
    .alert.success { background: #d1fae5; color: #065f46; }
    .alert.error   { background: #fee2e2; color: #991b1b; }
  `]
})
export class AuthComponent {
  isLoginMode = true;
  adminLoginMode = false;
  loading = false;
  message = '';
  messageType: 'success' | 'error' | '' = '';

  loginForm: FormGroup;
  signupForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });

    // Public signup is always for customers — admin role removed.
    this.signupForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[+]?[0-9]{10,15}$/)]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(64)]]
    });
  }

  toggleMode(): void {
    this.isLoginMode = !this.isLoginMode;
    this.adminLoginMode = false;
    this.message = '';
    this.messageType = '';
  }

  toggleAdminLoginMode(): void {
    this.adminLoginMode = !this.adminLoginMode;
    this.isLoginMode = true;
    this.message = '';
    this.messageType = '';
  }

  onLogin(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.message = '';
    this.messageType = '';

    this.authService.login(this.loginForm.value).subscribe({
      next: (res) => {
        this.loading = false;

        if (this.adminLoginMode && res?.role !== 'ADMIN') {
          this.messageType = 'error';
          this.message = 'These credentials are not for an admin account.';
          return;
        }

        this.messageType = 'success';
        this.message = 'Login successful!';
        if (isBrowser()) {
          window.localStorage.setItem('user', JSON.stringify(res));
        }

        if (res?.role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/home']);
        }
      },
      error: (err) => {
        this.loading = false;
        this.messageType = 'error';
        if (err.status === 401 || err.status === 403) {
          this.message = 'Incorrect email or password. Please try again.';
        } else if (err.status === 0) {
          this.message = 'Cannot reach the server. Please try again later.';
        } else {
          this.message = err.error?.message || 'Incorrect email or password. Please try again.';
        }
      }
    });
  }

  onSignup(): void {
    if (this.signupForm.invalid) {
      this.signupForm.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.message = '';
    this.messageType = '';

    const payload = { ...this.signupForm.value, role: 'CUSTOMER' as const };

    this.authService.register(payload).subscribe({
      next: (res: any) => {
        this.loading = false;
        this.messageType = 'success';
        this.message = res?.message || 'Registration successful! Please sign in.';
        setTimeout(() => {
          this.isLoginMode = true;
          this.message = '';
        }, 1500);
      },
      error: (err) => {
        this.loading = false;
        this.messageType = 'error';
        this.message = err.error?.message || 'Registration failed';
      }
    });
  }
}