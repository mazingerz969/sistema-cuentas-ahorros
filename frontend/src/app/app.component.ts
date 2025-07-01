import { Component } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

/**
 * Componente principal de la aplicación Angular.
 * 
 * Este es el componente raíz que contiene:
 * - La barra de navegación superior
 * - El menú lateral (sidenav)
 * - El área principal donde se cargan los componentes de las rutas
 * 
 * En Angular 17+, los componentes son standalone por defecto,
 * lo que significa que no necesitan estar declarados en un módulo.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterModule
  ],
  template: `
    <div class="app-container">
      <!-- Barra de navegación superior -->
      <header class="toolbar">
        <div class="toolbar-left">
          <button class="menu-button" (click)="toggleSidenav()" type="button">
            <span class="menu-icon">☰</span>
          </button>
          <h1 class="app-title">🏦 Gestión de Cuentas de Ahorros</h1>
        </div>
        
        <div class="toolbar-center">
          <div class="breadcrumb">
            <span class="breadcrumb-text">{{ getCurrentPageTitle() }}</span>
          </div>
        </div>
        
        <div class="toolbar-right">
          <button class="notification-button" (click)="showNotifications()" type="button">
            <span class="notification-icon">🔔</span>
          </button>
          <button class="user-button" (click)="showUserMenu()" type="button">
            <span class="user-icon">👤</span>
          </button>
        </div>
      </header>

      <!-- Contenedor principal -->
      <div class="main-container">
        <!-- Menú lateral -->
        <nav class="sidenav" [class.sidenav-open]="sidenavOpen">
          <div class="nav-list">
            <a class="nav-item" routerLink="/dashboard" routerLinkActive="active" (click)="closeSidenav()">
              <span class="nav-icon">📊</span>
              <span class="nav-text">Dashboard</span>
            </a>
            <a class="nav-item" routerLink="/cuentas" routerLinkActive="active" (click)="closeSidenav()">
              <span class="nav-icon">🏦</span>
              <span class="nav-text">Cuentas</span>
            </a>
            <a class="nav-item" routerLink="/transacciones" routerLinkActive="active" (click)="closeSidenav()">
              <span class="nav-icon">💱</span>
              <span class="nav-text">Transacciones</span>
            </a>
          </div>
        </nav>

        <!-- Contenido principal -->
        <main class="content">
          <div class="content-container">
            <router-outlet></router-outlet>
          </div>
        </main>
      </div>

      <!-- Overlay para cerrar el sidenav en móviles -->
      <div class="sidenav-overlay" 
           [class.overlay-visible]="sidenavOpen" 
           (click)="closeSidenav()"
           *ngIf="sidenavOpen"></div>
    </div>
  `,
  styles: [`
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }

    .app-container {
      height: 100vh;
      display: flex;
      flex-direction: column;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    }

    /* Barra de navegación superior */
    .toolbar {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 0 16px;
      height: 64px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      box-shadow: 0 2px 8px rgba(0,0,0,0.15);
      z-index: 1000;
      position: relative;
    }

    .toolbar-left {
      display: flex;
      align-items: center;
      flex: 1;
    }

    .toolbar-center {
      display: flex;
      align-items: center;
      justify-content: center;
      flex: 1;
    }

    .toolbar-right {
      display: flex;
      align-items: center;
      gap: 8px;
      flex: 1;
      justify-content: flex-end;
    }

    .menu-button {
      background: none;
      border: none;
      color: white;
      font-size: 24px;
      cursor: pointer;
      padding: 8px;
      border-radius: 4px;
      transition: all 0.3s ease;
      display: flex;
      align-items: center;
      justify-content: center;
      min-width: 40px;
      height: 40px;
    }

    .menu-button:hover {
      background-color: rgba(255,255,255,0.2);
      transform: scale(1.05);
    }

    .menu-button:active {
      transform: scale(0.95);
    }

    .app-title {
      margin-left: 16px;
      font-size: 20px;
      font-weight: 600;
      white-space: nowrap;
    }

    .breadcrumb {
      background: rgba(255,255,255,0.1);
      padding: 8px 16px;
      border-radius: 20px;
      backdrop-filter: blur(10px);
    }

    .breadcrumb-text {
      font-size: 14px;
      font-weight: 500;
      color: white;
    }

    .notification-button,
    .user-button {
      background: none;
      border: none;
      color: white;
      font-size: 18px;
      cursor: pointer;
      padding: 8px;
      border-radius: 50%;
      transition: all 0.3s ease;
      display: flex;
      align-items: center;
      justify-content: center;
      min-width: 40px;
      height: 40px;
      position: relative;
    }

    .notification-button:hover,
    .user-button:hover {
      background-color: rgba(255,255,255,0.2);
      transform: scale(1.05);
    }

    .notification-button:active,
    .user-button:active {
      transform: scale(0.95);
    }

    /* Contenedor principal */
    .main-container {
      flex: 1;
      display: flex;
      position: relative;
      overflow: hidden;
    }

    /* Menú lateral */
    .sidenav {
      background: white;
      width: 250px;
      box-shadow: 2px 0 8px rgba(0,0,0,0.1);
      transform: translateX(-100%);
      transition: transform 0.3s ease;
      z-index: 999;
      position: fixed;
      height: calc(100vh - 64px);
      top: 64px;
      overflow-y: auto;
    }

    .sidenav-open {
      transform: translateX(0);
    }

    .nav-list {
      padding: 16px 0;
    }

    .nav-item {
      display: flex;
      align-items: center;
      padding: 16px 24px;
      color: #333;
      text-decoration: none;
      transition: all 0.3s ease;
      border-left: 3px solid transparent;
      font-weight: 500;
    }

    .nav-item:hover {
      background-color: #f8f9fa;
      border-left-color: #667eea;
      transform: translateX(4px);
    }

    .nav-item.active {
      background-color: #e3f2fd;
      border-left-color: #667eea;
      color: #1976d2;
      font-weight: 600;
    }

    .nav-icon {
      font-size: 20px;
      margin-right: 16px;
      width: 24px;
      text-align: center;
    }

    .nav-text {
      font-size: 16px;
    }

    /* Contenido principal */
    .content {
      flex: 1;
      background-color: #f5f5f5;
      margin-left: 0;
      transition: margin-left 0.3s ease;
      overflow-y: auto;
    }

    .content-container {
      padding: 24px;
      max-width: 1200px;
      margin: 0 auto;
    }

    /* Overlay para móviles */
    .sidenav-overlay {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-color: rgba(0,0,0,0.5);
      z-index: 998;
      opacity: 0;
      visibility: hidden;
      transition: all 0.3s ease;
    }

    .overlay-visible {
      opacity: 1;
      visibility: visible;
    }

    /* Responsive */
    @media (min-width: 768px) {
      .sidenav {
        position: relative;
        top: 0;
        transform: translateX(0);
      }

      .content {
        margin-left: 250px;
      }

      .sidenav-overlay {
        display: none;
      }

      .breadcrumb {
        display: block;
      }
    }

    @media (max-width: 767px) {
      .sidenav {
        position: fixed;
        top: 64px;
        height: calc(100vh - 64px);
      }

      .app-title {
        font-size: 16px;
        margin-left: 8px;
      }

      .breadcrumb {
        display: none;
      }

      .toolbar {
        padding: 0 8px;
      }
    }

    @media (max-width: 480px) {
      .app-title {
        display: none;
      }

      .toolbar-left {
        justify-content: center;
      }
    }
  `]
})
export class AppComponent {
  /**
   * Título de la aplicación.
   */
  title = 'Gestión de Cuentas de Ahorros';

  /**
   * Estado del menú lateral (abierto/cerrado).
   */
  sidenavOpen = false;

  /**
   * Alterna el estado del menú lateral.
   */
  toggleSidenav(): void {
    this.sidenavOpen = !this.sidenavOpen;
    console.log('Sidenav toggled:', this.sidenavOpen);
  }

  /**
   * Cierra el menú lateral.
   */
  closeSidenav(): void {
    this.sidenavOpen = false;
    console.log('Sidenav closed');
  }

  /**
   * Obtiene el título de la página actual.
   */
  getCurrentPageTitle(): string {
    const currentPath = window.location.pathname;
    switch (currentPath) {
      case '/dashboard':
        return 'Dashboard';
      case '/cuentas':
        return 'Gestión de Cuentas';
      case '/transacciones':
        return 'Transacciones';
      default:
        return 'Dashboard';
    }
  }

  /**
   * Muestra las notificaciones.
   */
  showNotifications(): void {
    console.log('Mostrar notificaciones');
    alert('🔔 Funcionalidad de notificaciones en desarrollo');
  }

  /**
   * Muestra el menú de usuario.
   */
  showUserMenu(): void {
    console.log('Mostrar menú de usuario');
    alert('👤 Funcionalidad de usuario en desarrollo');
  }
} 