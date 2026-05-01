import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MenuService } from '../../../services/menu.service';
import { MenuItem } from '../../../models/menu-item.model';

@Component({
  selector: 'app-menu-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './menu-list.component.html'
})
export class MenuListComponent {

  menuItems: MenuItem[] = [];

  constructor(private service: MenuService) {}

  ngOnInit() {
    this.service.getAll().subscribe(res => {
      this.menuItems = res;
    });
  }

  addToCart(item: MenuItem) {
    console.log('Add to cart:', item);
  }
}