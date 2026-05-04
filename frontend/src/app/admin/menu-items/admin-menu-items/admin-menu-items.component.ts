import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService } from '../../services/admin-services/admin.service';
@Component({
  selector: 'app-admin-menu-items',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-menu-items.component.html',
  styleUrls: ['./admin-menu-items.component.css']
})
export class AdminMenuItemsComponent implements OnInit {
  menuItems: any[] = [];
  restaurants: any[] = [];
  loading = false;
  showForm = false;
  editingId: number | null = null;
  form: FormGroup;
  message = '';
  messageType: 'success' | 'error' | '' = '';

  constructor(private fb: FormBuilder, private adminService: AdminService) {
    this.form = this.fb.group({
      itemName: ['', [Validators.required, Validators.maxLength(255)]],
      itemDescription: ['', [Validators.required, Validators.maxLength(500)]],
      itemPrice: [null, [Validators.required, Validators.min(0.01)]],
      restaurantId: [null, [Validators.required]],
      itemImageUrl: ['', [Validators.maxLength(500)]]
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.loading = true;
    this.adminService.getAllRestaurants().subscribe({
      next: (rests) => { this.restaurants = rests; },
      error: () => {}
    });
    this.adminService.getAllMenuItems().subscribe({
      next: (data) => { this.menuItems = data; this.loading = false; },
      error: (err) => {
        this.loading = false;
        this.showMessage(err.error?.message || 'Failed to load menu items', 'error');
      }
    });
  }

  openAddForm(): void {
    this.editingId = null;
    this.form.reset();
    this.showForm = true;
  }

  openEditForm(item: any): void {
    this.editingId = item.itemId;
    this.form.patchValue({
      itemName: item.itemName,
      itemDescription: item.itemDescription,
      itemPrice: item.itemPrice,
      restaurantId: item.restaurantId,
      itemImageUrl: item.itemImageUrl || ''
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
      this.adminService.updateMenuItem(this.editingId, payload).subscribe({
        next: () => {
          this.showMessage('Menu item updated', 'success');
          this.closeForm();
          this.loadAll();
        },
        error: (err) => this.showMessage(err.error?.message || 'Update failed', 'error')
      });
    } else {
      this.adminService.addMenuItem(payload).subscribe({
        next: () => {
          this.showMessage('Menu item added', 'success');
          this.closeForm();
          this.loadAll();
        },
        error: (err) => this.showMessage(err.error?.message || 'Failed to add', 'error')
      });
    }
  }

  deleteItem(id: number): void {
    if (!confirm('Are you sure you want to delete this menu item?')) return;
    this.adminService.deleteMenuItem(id).subscribe({
      next: () => {
        this.showMessage('Menu item deleted', 'success');
        this.loadAll();
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
