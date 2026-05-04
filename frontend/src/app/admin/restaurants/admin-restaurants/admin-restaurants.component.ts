import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService } from '../../services/admin-services/admin.service';


@Component({
  selector: 'app-admin-restaurants',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-restaurants.component.html',
  styleUrls: ['./admin-restaurants.component.css']
})
export class AdminRestaurantsComponent implements OnInit {
  restaurants: any[] = [];
  loading = false;
  showForm = false;
  editingId: number | null = null;
  form: FormGroup;
  message = '';
  messageType: 'success' | 'error' | '' = '';

  constructor(private fb: FormBuilder, private adminService: AdminService) {
    this.form = this.fb.group({
      restaurantName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      restaurantAddress: ['', [Validators.required, Validators.minLength(5)]],
      restaurantPhone: ['', [Validators.required, Validators.pattern(/^[+]?[0-9][0-9\-\s]{6,18}[0-9]$/)]]
    });
  }

  ngOnInit(): void {
    this.loadRestaurants();
  }

  loadRestaurants(): void {
    this.loading = true;
    this.adminService.getAllRestaurants().subscribe({
      next: (data) => { this.restaurants = data; this.loading = false; },
      error: (err) => {
        this.loading = false;
        this.showMessage(err.error?.message || 'Failed to load restaurants', 'error');
      }
    });
  }

  openAddForm(): void {
    this.editingId = null;
    this.form.reset();
    this.showForm = true;
  }

  openEditForm(r: any): void {
    this.editingId = r.restaurantId;
    this.form.patchValue({
      restaurantName: r.restaurantName,
      restaurantAddress: r.restaurantAddress,
      restaurantPhone: r.restaurantPhone
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
      this.adminService.updateRestaurant(this.editingId, payload).subscribe({
        next: () => {
          this.showMessage('Restaurant updated successfully', 'success');
          this.closeForm();
          this.loadRestaurants();
        },
        error: (err) => this.showMessage(err.error?.message || 'Update failed', 'error')
      });
    } else {
      this.adminService.addRestaurant(payload).subscribe({
        next: () => {
          this.showMessage('Restaurant added successfully', 'success');
          this.closeForm();
          this.loadRestaurants();
        },
        error: (err) => this.showMessage(err.error?.message || 'Failed to add restaurant', 'error')
      });
    }
  }

  deleteRestaurant(id: number): void {
    if (!confirm('Are you sure you want to delete this restaurant?')) return;
    this.adminService.deleteRestaurant(id).subscribe({
      next: () => {
        this.showMessage('Restaurant deleted', 'success');
        this.loadRestaurants();
      },
      error: (err) => this.showMessage(err.error?.message || 'Delete failed', 'error')
    });
  }

  private showMessage(text: string, type: 'success' | 'error'): void {
    this.message = text;
    this.messageType = type;
    setTimeout(() => { this.message = ''; this.messageType = ''; }, 2500);
  }
}