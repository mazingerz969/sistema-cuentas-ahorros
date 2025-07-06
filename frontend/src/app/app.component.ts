import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterOutlet, RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { UsuarioService } from './services/usuario.service';
import { NotificacionService } from './services/notificacion.service';
import { Usuario } from './models/usuario.model';

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
          <button class="menu-button logo-burger" (click)="toggleSidenav()" type="button" style="display: flex; align-items: center; background: none; border: none;">
            <img src="assets/logo.svg" alt="Logo del Banco" class="app-logo" style="height: 40px;" />
          </button>
        </div>
        
        <div class="toolbar-center">
          <div class="breadcrumb">
            <a class="breadcrumb-link" routerLink="/transacciones">Transacciones</a>
          </div>
        </div>
        
        <div class="toolbar-right">
          <button class="notification-button" (click)="showNotifications()" type="button">
            <span class="notification-icon">🔔</span>
            <span *ngIf="notificacionesNoLeidas > 0" class="notification-badge">{{ notificacionesNoLeidas }}</span>
          </button>
          <button class="user-button" (click)="showUserMenu()" type="button">
            <span class="user-icon">👤</span>
            <span *ngIf="usuario" class="user-name">{{ usuario.nombre }}</span>
          </button>
          <button *ngIf="usuario" class="logout-button" (click)="logout()" type="button">
            <span class="logout-icon">🚪</span>
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
            <a class="nav-item" routerLink="/notificaciones" routerLinkActive="active" (click)="closeSidenav()">
              <span class="nav-icon">🔔</span>
              <span class="nav-text">Notificaciones</span>
              <span *ngIf="notificacionesNoLeidas > 0" class="nav-badge">{{ notificacionesNoLeidas }}</span>
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
    @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap');
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
      font-family: 'Roboto', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    }

    .app-container {
      height: 100vh;
      display: flex;
      flex-direction: column;
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

    .breadcrumb-link {
      background: #764ba2;
      color: #fff;
      padding: 8px 24px;
      border-radius: 20px;
      font-weight: 600;
      font-size: 16px;
      text-decoration: none;
      box-shadow: 0 2px 8px rgba(0,0,0,0.08);
      transition: background 0.2s, color 0.2s;
      display: inline-block;
    }
    .breadcrumb-link:hover {
      background: #fff;
      color: #764ba2;
      border: 1px solid #764ba2;
    }

    .notification-button,
    .user-button,
    .logout-button {
      background: none;
      border: none;
      color: white;
      font-size: 18px;
      cursor: pointer;
      padding: 8px;
      border-radius: 50%;
      transition: all 0.3s ease;
      position: relative;
      display: flex;
      align-items: center;
      gap: 4px;
    }
      display: flex;
      align-items: center;
      justify-content: center;
      min-width: 40px;
      height: 40px;
      position: relative;
    }

    .notification-button:hover,
    .user-button:hover,
    .logout-button:hover {
      background-color: rgba(255,255,255,0.2);
      transform: scale(1.05);
    }

    .notification-button:active,
    .user-button:active,
    .logout-button:active {
      transform: scale(0.95);
    }

    .notification-badge {
      position: absolute;
      top: 0;
      right: 0;
      background: #ff4757;
      color: white;
      border-radius: 50%;
      width: 18px;
      height: 18px;
      font-size: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
    }

    .user-name {
      font-size: 14px;
      font-weight: 500;
      margin-left: 4px;
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
      flex: 1;
    }

    .nav-badge {
      background: #ff4757;
      color: white;
      border-radius: 50%;
      width: 20px;
      height: 20px;
      font-size: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
      margin-left: auto;
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
        position: fixed;
        top: 64px;
        height: calc(100vh - 64px);
        transform: translateX(-100%); /* ← Ahora está oculto por defecto */
      }

      .sidenav.sidenav-open {
        transform: translateX(0); /* ← Solo se muestra cuando está abierto */
      }

      .content {
        margin-left: 0; /* ← Sin margen fijo */
        transition: margin-left 0.3s ease;
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

    .animated-logo {
      font-family: 'Orbitron', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      font-size: 2rem;
      font-weight: 900;
      letter-spacing: 2px;
      background: linear-gradient(270deg, #ff6ec4, #7873f5, #1fd1f9, #ff6ec4);
      background-size: 800% 800%;
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      animation: gradientMove 8s ease-in-out infinite;
      text-shadow: 0 2px 8px rgba(0,0,0,0.25), 0 0 2px #fff, 0 0 8px #764ba2;
      filter: drop-shadow(0 2px 8px #764ba2);
      border-radius: 8px;
      padding: 2px 18px;
      border: 2px solid #fff3;
      box-shadow: 0 2px 12px 0 #764ba233;
      margin-left: 16px;
      transition: transform 0.2s;
    }
    .animated-logo:hover {
      transform: scale(1.05) rotate(-2deg);
      box-shadow: 0 4px 24px 0 #1fd1f955;
    }
    @keyframes gradientMove {
      0% { background-position: 0% 50%; }
      50% { background-position: 100% 50%; }
      100% { background-position: 0% 50%; }
    }

    .logo-burger {
      cursor: pointer;
      background: none;
      border: none;
      padding: 0;
    }
    .logo-burger .app-logo {
      transition: transform 0.15s, filter 0.15s;
    }
    .logo-burger:hover .app-logo,
    .logo-burger:focus .app-logo {
      filter: brightness(0.9);
      transform: scale(1.08);
    }
    .logo-burger:active .app-logo {
      filter: brightness(0.8);
      transform: scale(0.96);
    }
  `]
})
export class AppComponent implements OnInit, OnDestroy {
  /**
   * Título de la aplicación.
   */
  title = 'Gestión de Cuentas de Ahorros';

  /**
   * Estado del menú lateral (abierto/cerrado).
   */
  sidenavOpen = false;
  usuario: Usuario | null = null;
  notificacionesNoLeidas = 0;
  private destroy$ = new Subject<void>();

  constructor(
    private usuarioService: UsuarioService,
    private notificacionService: NotificacionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Suscribirse al usuario actual
    this.usuarioService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(usuario => {
        this.usuario = usuario;
        if (usuario) {
          this.cargarNotificacionesNoLeidas();
        }
      });

    // Suscribirse al contador de notificaciones
    this.notificacionService.notificacionesNoLeidas$
      .pipe(takeUntil(this.destroy$))
      .subscribe(count => {
        this.notificacionesNoLeidas = count;
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  cargarNotificacionesNoLeidas(): void {
    if (this.usuario) {
      this.notificacionService.contarNoLeidas(this.usuario.id).subscribe();
    }
  }

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
    if (this.usuario) {
      this.router.navigate(['/notificaciones']);
    } else {
      this.router.navigate(['/login']);
    }
  }

  /**
   * Muestra el menú de usuario.
   */
  showUserMenu(): void {
    if (this.usuario) {
      // Aquí podrías mostrar un dropdown con opciones del usuario
      console.log('Usuario actual:', this.usuario);
    } else {
      this.router.navigate(['/login']);
    }
  }

  /**
   * Cierra la sesión del usuario.
   */
  logout(): void {
    this.usuarioService.logout();
    this.router.navigate(['/login']);
  }
} 