import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../auth.service';
export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const user = authService.getCurrentUser();

  if (user?.token && user.role === 'ADMIN') {
    return true;
  }

  router.navigate(user?.token ? ['/home'] : ['/auth']);
  return false;
};