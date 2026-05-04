import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MenuService } from '../../../services/menu.service';
import { MenuItem } from '../../../models/menu-item.model';

@Component({
  selector: 'app-menu-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './menu-list.component.html'
})
export class MenuListComponent implements OnInit {

  menuItems: MenuItem[]  = [];
  filteredItems: MenuItem[] = [];

  searchKeyword  = '';
  maxPrice: number | null = null;

  isLoading = false;
  errorMessage = '';

  constructor(private menuService: MenuService) {}

  ngOnInit(): void {
    this.loadAllMenuItems();
  }

  loadAllMenuItems(): void {
    this.isLoading    = true;
    this.errorMessage = '';

    this.menuService.getAll().subscribe({
      next: (items) => {
        this.menuItems     = items;
        this.filteredItems = items;
        this.isLoading     = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load menu items. Please try again.';
        this.isLoading    = false;
        console.error(err);
      }
    });
  }

  onSearch(): void {
    const keyword = this.searchKeyword.trim();

    if (!keyword) {
      this.filteredItems = this.menuItems;
      return;
    }

    this.isLoading = true;
    this.menuService.searchByName(keyword).subscribe({
      next: (items) => {
        this.filteredItems = items;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Search failed. Please try again.';
        this.isLoading = false;
      }
    });
  }

  onMaxPriceFilter(): void {
    if (this.maxPrice == null || this.maxPrice <= 0) {
      this.filteredItems = this.menuItems;
      return;
    }

    this.isLoading = true;
    this.menuService.getByMaxPrice(this.maxPrice).subscribe({
      next: (items) => {
        this.filteredItems = items;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Price filter failed. Please try again.';
        this.isLoading = false;
      }
    });
  }

  clearFilters(): void {
    this.searchKeyword = '';
    this.maxPrice      = null;
    this.filteredItems = this.menuItems;
    this.errorMessage  = '';
  }

  addToCart(item: MenuItem): void {
    // TODO: wire up to CartService
    console.log('Add to cart:', item);
  }
}