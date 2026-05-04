import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService } from '../../services/admin-services/admin.service';
@Component({
  selector: 'app-admin-coupons',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-coupons.component.html',
  styleUrls: ['./admin-coupons.component.css']
})
export class AdminCouponsComponent implements OnInit {
  coupons: any[] = [];
  loading = false;
  showForm = false;
  editingId: number | null = null;
  form: FormGroup;
  message = '';
  messageType: 'success' | 'error' | '' = '';

  constructor(private fb: FormBuilder, private adminService: AdminService) {
    this.form = this.fb.group({
      couponCode: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20), Validators.pattern(/^[A-Z0-9]+$/)]],
      discountAmount: [null, [Validators.required, Validators.min(0.01)]],
      expiryDate: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.loadCoupons();
  }

  loadCoupons(): void {
    this.loading = true;
    this.adminService.getAllCoupons().subscribe({
      next: (data) => { this.coupons = data; this.loading = false; },
      error: (err) => {
        this.loading = false;
        this.showMessage(err.error?.message || 'Failed to load coupons', 'error');
      }
    });
  }

  openAddForm(): void {
    this.editingId = null;
    this.form.reset();
    this.showForm = true;
  }

  openEditForm(c: any): void {
    this.editingId = c.couponId;
    this.form.patchValue({
      couponCode: c.couponCode,
      discountAmount: c.discountAmount,
      expiryDate: c.expiryDate
    });
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.editingId = null;
    this.form.reset();
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const payload = this.form.value;
    if (this.editingId != null) {
      this.adminService.updateCoupon(this.editingId, payload).subscribe({
        next: () => {
          this.showMessage('Coupon updated', 'success');
          this.closeForm();
          this.loadCoupons();
        },
        error: (err) => this.showMessage(err.error?.message || 'Update failed', 'error')
      });
    } else {
      this.adminService.addCoupon(payload).subscribe({
        next: () => {
          this.showMessage('Coupon added', 'success');
          this.closeForm();
          this.loadCoupons();
        },
        error: (err) => this.showMessage(err.error?.message || 'Failed to add coupon', 'error')
      });
    }
  }

  deleteCoupon(id: number): void {
    if (!confirm('Are you sure you want to delete this coupon?')) return;
    this.adminService.deleteCoupon(id).subscribe({
      next: () => {
        this.showMessage('Coupon deleted', 'success');
        this.loadCoupons();
      },
      error: (err) => this.showMessage(err.error?.message || 'Delete failed', 'error')
    });
  }

  isExpired(date: string): boolean {
    if (!date) return false;
    return new Date(date) < new Date();
  }

  private showMessage(text: string, type: 'success' | 'error'): void {
    this.message = text;
    this.messageType = type;
    setTimeout(() => { this.message = ''; this.messageType = ''; }, 2500);
  }
}
