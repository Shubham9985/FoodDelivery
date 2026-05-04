import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent {
  isLoginMode = true;
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

    this.signupForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[+]?[0-9]{10,15}$/)]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(64)]],
      role: ['CUSTOMER', Validators.required]
    });
  }

  toggleMode(): void {
    this.isLoginMode = !this.isLoginMode;
    this.message = '';
    this.messageType = '';
  }

  onLogin(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.authService.login(this.loginForm.value).subscribe({
      next: (res) => {
        this.loading = false;
        this.messageType = 'success';
        this.message = 'Login successful!';
        localStorage.setItem('user', JSON.stringify(res));

        // Role-based redirect
        if (res?.role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/home']);
        }
      },
      error: (err) => {
        this.loading = false;
        this.messageType = 'error';
        this.message = err.error?.message || 'Invalid email or password';
      }
    });
  }

  onSignup(): void {
    if (this.signupForm.invalid) {
      this.signupForm.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.authService.register(this.signupForm.value).subscribe({
      next: (res: any) => {
        this.loading = false;
        this.messageType = 'success';
        this.message = res?.message || 'Registration successful!';
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