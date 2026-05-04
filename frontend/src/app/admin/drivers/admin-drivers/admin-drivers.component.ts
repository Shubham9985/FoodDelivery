import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService } from '../../services/admin-services/admin.service';
@Component({
  selector: 'app-admin-drivers',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-drivers.component.html',
  styleUrls: ['./admin-drivers.component.css']
})
export class AdminDriversComponent implements OnInit {
  drivers: any[] = [];
  loading = false;
  showForm = false;
  editingId: number | null = null;
  form: FormGroup;
  message = '';
  messageType: 'success' | 'error' | '' = '';

  constructor(private fb: FormBuilder, private adminService: AdminService) {
    this.form = this.fb.group({
      driverName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      driverPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      driverVehicle: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.loadDrivers();
  }

  loadDrivers(): void {
    this.loading = true;
    this.adminService.getAllDrivers().subscribe({
      next: (data) => { this.drivers = data; this.loading = false; },
      error: (err) => {
        this.loading = false;
        this.showMessage(err.error?.message || 'Failed to load drivers', 'error');
      }
    });
  }

  openAddForm(): void {
    this.editingId = null;
    this.form.reset();
    this.showForm = true;
  }

  openEditForm(d: any): void {
    this.editingId = d.driverId;
    this.form.patchValue({
      driverName: d.driverName,
      driverPhone: d.driverPhone,
      driverVehicle: d.driverVehicle
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
      this.adminService.updateDriver(this.editingId, payload).subscribe({
        next: () => {
          this.showMessage('Driver updated', 'success');
          this.closeForm();
          this.loadDrivers();
        },
        error: (err) => this.showMessage(err.error?.message || 'Update failed', 'error')
      });
    } else {
      this.adminService.addDriver(payload).subscribe({
        next: () => {
          this.showMessage('Driver added', 'success');
          this.closeForm();
          this.loadDrivers();
        },
        error: (err) => this.showMessage(err.error?.message || 'Failed to add driver', 'error')
      });
    }
  }

  deleteDriver(id: number): void {
    if (!confirm('Are you sure you want to delete this driver?')) return;
    this.adminService.deleteDriver(id).subscribe({
      next: () => {
        this.showMessage('Driver deleted', 'success');
        this.loadDrivers();
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