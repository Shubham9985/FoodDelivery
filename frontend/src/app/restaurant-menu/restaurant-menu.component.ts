import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-restaurant-menu',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './restaurant-menu.component.html',
  styleUrls: ['./restaurant-menu.component.css']
})
export class RestaurantMenuComponent implements OnInit {
  restaurant: any = null;
  menuItems: any[] = [];
  loading = false;
  message = '';
  messageType: 'success' | 'error' | '' = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private customerService: CustomerService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadRestaurant(id);
    this.loadMenu(id);
  }

  loadRestaurant(id: number): void {
    this.customerService.getRestaurantById(id).subscribe({
      next: (data) => this.restaurant = data,
      error: () => {}
    });
  }

  loadMenu(restaurantId: number): void {
    this.loading = true;
    this.customerService.getMenuByRestaurant(restaurantId).subscribe({
      next: (data) => { this.menuItems = data; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  addToCart(item: any): void {
    const customerId = this.customerService.getCurrentCustomerId();
    this.customerService.addToCart(customerId, item.itemId, 1).subscribe({
      next: () => {
        this.messageType = 'success';
        this.message = `${item.itemName} added to cart!`;
        setTimeout(() => this.message = '', 2000);
      },
      error: (err) => {
        this.messageType = 'error';
        this.message = err.error?.message || 'Failed to add item';
        setTimeout(() => this.message = '', 2500);
      }
    });
  }

  goBack(): void { this.router.navigate(['/home']); }
}