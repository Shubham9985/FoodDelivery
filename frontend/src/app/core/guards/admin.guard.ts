import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../auth.service';
export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const user = authService.getCurrentUser();
  const role = String(user?.role || '').toUpperCase();

  if (user?.token && role.includes('ADMIN')) {
    return true;
  }

  router.navigate(user?.token ? ['/home'] : ['/auth']);
  return false;
};