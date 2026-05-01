import { Component, AfterViewInit, OnInit, inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';
import AOS from 'aos';

import { MenuService } from '../../../services/menu.service';
import { MenuItem } from '../../../models/menu-item.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements AfterViewInit, OnInit {

  platformId = inject(PLATFORM_ID);

  menuItems: MenuItem[] = [];

  constructor(private menuService: MenuService) {}

  // ✅ AOS animation
  ngAfterViewInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      AOS.init({
        duration: 1000,
        once: true
      });
    }
  }

  // ✅ API call
  ngOnInit(): void {
    this.menuService.getAll().subscribe({
      next: (res) => this.menuItems = res,
      error: (err) => console.error('Error loading menu:', err)
    });
  }

  // ✅ Cart handler
  addToCart(item: MenuItem): void {
    console.log('Add to cart:', item);
  }
}