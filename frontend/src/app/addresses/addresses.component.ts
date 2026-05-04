import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-addresses',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './addresses.component.html',
  styleUrls: ['./addresses.component.css']
})
export class AddressesComponent implements OnInit {
  addresses: any[] = [];
  showForm = false;
  loading = false;
  addressForm: FormGroup;
  message = '';
  messageType: 'success' | 'error' | '' = '';

  constructor(private fb: FormBuilder, private customerService: CustomerService) {
    this.addressForm = this.fb.group({
      addressLine: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      pincode: ['', [Validators.required, Validators.pattern(/^[0-9]{5,6}$/)]]
    });
  }

  ngOnInit(): void { this.loadAddresses(); }

  loadAddresses(): void {
    this.loading = true;
    const customerId = this.customerService.getCurrentCustomerId();
    this.customerService.getMyAddresses(customerId).subscribe({
      next: (data) => { this.addresses = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    if (!this.showForm) this.addressForm.reset();
  }

  saveAddress(): void {
    if (this.addressForm.invalid) {
      this.addressForm.markAllAsTouched();
      return;
    }
    const customerId = this.customerService.getCurrentCustomerId();
    const payload = { ...this.addressForm.value, customerId };
    this.customerService.addAddress(payload).subscribe({
      next: () => {
        this.messageType = 'success';
        this.message = 'Address saved!';
        this.addressForm.reset();
        this.showForm = false;
        this.loadAddresses();
        setTimeout(() => this.message = '', 2000);
      },
      error: (err) => {
        this.messageType = 'error';
        this.message = err.error?.message || 'Failed to save';
        setTimeout(() => this.message = '', 2500);
      }
    });
  }
}