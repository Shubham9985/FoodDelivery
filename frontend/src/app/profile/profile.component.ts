import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup;
  loading = false;
  saving = false;
  editMode = false;
  profile: any = null;
  message = '';
  messageType: 'success' | 'error' | '' = '';

  constructor(
    private fb: FormBuilder,
    private customerService: CustomerService,
    private router: Router
  ) {
    this.profileForm = this.fb.group({
      customerName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      customerEmail: ['', [Validators.required, Validators.email]],
      customerPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]]
    });
  }

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.loading = true;
    const customerId = this.customerService.getCurrentCustomerId();
    this.customerService.getProfile(customerId).subscribe({
      next: (data) => {
        this.profile = data;
        this.profileForm.patchValue({
          customerName: data.customerName,
          customerEmail: data.customerEmail,
          customerPhone: data.customerPhone
        });
        this.profileForm.disable();
        this.loading = false;
      },
      error: (err) => {
        this.messageType = 'error';
        this.message = err.error?.message || 'Failed to load profile';
        this.loading = false;
      }
    });
  }

  toggleEdit(): void {
    this.editMode = !this.editMode;
    if (this.editMode) {
      this.profileForm.enable();
    } else {
      this.profileForm.disable();
      if (this.profile) {
        this.profileForm.patchValue({
          customerName: this.profile.customerName,
          customerEmail: this.profile.customerEmail,
          customerPhone: this.profile.customerPhone
        });
      }
    }
  }

  saveProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }
    this.saving = true;
    const customerId = this.customerService.getCurrentCustomerId();
    const payload = {
      ...this.profileForm.value,
      userId: this.profile?.userId
    };
    this.customerService.updateProfile(customerId, payload).subscribe({
      next: (data) => {
        this.profile = data;
        this.editMode = false;
        this.profileForm.disable();
        this.saving = false;
        this.messageType = 'success';
        this.message = 'Profile updated successfully';
        setTimeout(() => this.message = '', 2500);
      },
      error: (err) => {
        this.saving = false;
        this.messageType = 'error';
        this.message = err.error?.message || 'Failed to update profile';
        setTimeout(() => this.message = '', 3000);
      }
    });
  }

  getInitials(): string {
    const name = this.profile?.customerName || '';
    return name.split(' ').map((n: string) => n.charAt(0)).join('').slice(0, 2).toUpperCase() || 'U';
  }

  logout(): void {
    localStorage.removeItem('user');
    this.router.navigate(['/auth']);
  }
}