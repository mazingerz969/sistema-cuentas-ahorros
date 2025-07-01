import { Routes } from '@angular/router';

/**
 * Configuración de rutas de la aplicación Angular.
 * 
 * Este archivo define todas las rutas de la aplicación usando
 * el nuevo sistema de rutas de Angular 17+ con funciones.
 * 
 * Cada ruta se define con:
 * - path: La URL de la ruta
 * - component: El componente que se cargará
 * - title: El título de la página (opcional)
 * 
 * Las rutas se cargan de forma lazy (perezosa) para mejorar
 * el rendimiento de la aplicación.
 */
export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent),
    title: 'Dashboard'
  },
  {
    path: 'cuentas',
    loadComponent: () => import('./components/cuentas/cuentas.component').then(m => m.CuentasComponent),
    title: 'Gestión de Cuentas'
  },
  {
    path: 'transacciones',
    loadComponent: () => import('./components/transacciones/transacciones.component').then(m => m.TransaccionesComponent),
    title: 'Transacciones'
  },
  {
    path: '**',
    redirectTo: '/dashboard'
  }
]; 