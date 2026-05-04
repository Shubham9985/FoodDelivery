import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-restaurant-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './restaurant-list.component.html',
  styleUrls: ['./restaurant-list.component.css']
})
export class RestaurantListComponent {

  restaurants = [
    { name: 'Pizza Hut', cuisine: 'Italian', rating: 4.2, deliveryTime: 30 },
    { name: 'Burger King', cuisine: 'Fast Food', rating: 4.0, deliveryTime: 25 },
    { name: 'Biryani House', cuisine: 'Indian', rating: 4.5, deliveryTime: 35 }
  ];

}