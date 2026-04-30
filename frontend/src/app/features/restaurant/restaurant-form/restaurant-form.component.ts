import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RestaurantService } from '../../../services/restaurant.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';

@Component({
  selector: 'app-restaurant-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './restaurant-form.component.html'
})
export class RestaurantFormComponent {

  form = {
    restaurantId: 0,
    restaurantName: '',
    restaurantAddress: '',
    restaurantPhone: ''
  };

  constructor(
    private service: RestaurantService,
    private toastr: ToastrService,
    private router: Router
  ) {}

  submit() {

    if (!this.form.restaurantId || this.form.restaurantId <= 0) {
      this.toastr.error('ID must be positive');
      return;
    }

    if (!this.form.restaurantName.trim()) {
      this.toastr.error('Name required');
      return;
    }

    this.service.add(this.form).subscribe({
      next: () => {
        this.toastr.success('Restaurant added');
        this.router.navigate(['/restaurants']);
      },
      error: () => this.toastr.error('Error adding restaurant')
    });
  }
}