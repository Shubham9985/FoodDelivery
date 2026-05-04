import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {

  showLogin = false;

  isLogin = true;
  username = '';
  password = '';

  openLogin() {
  console.log("clicked");
  this.showLogin = true;
}

  closeLogin() {
    this.showLogin = false;
  }

  toggleMode() {
    this.isLogin = !this.isLogin;
  }
}