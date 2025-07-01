import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

/**
 * Componente de página no encontrada (404)
 * Se muestra cuando el usuario navega a una ruta que no existe
 */
@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule
  ],
  template: `
    <div class="not-found-container">
      <div class="not-found-content">
        <!-- Ilustración de error -->
        <div class="error-illustration">
          <div class="error-number">404</div>
          <div class="error-icon">
            <mat-icon>search_off</mat-icon>
          </div>
        </div>

        <!-- Mensaje de error -->
        <div class="error-message">
          <h1>¡Página no encontrada!</h1>
          <p>Lo sentimos, la página que buscas no existe o ha sido movida.</p>
          <p class="error-details">
            Es posible que hayas escrito mal la dirección o que la página haya sido eliminada.
          </p>
        </div>

        <!-- Acciones -->
        <div class="error-actions">
          <button mat-raised-button color="primary" routerLink="/dashboard">
            <mat-icon>home</mat-icon>
            Ir al Dashboard
          </button>
          
          <button mat-stroked-button routerLink="/cuentas">
            <mat-icon>account_balance</mat-icon>
            Gestionar Cuentas
          </button>
          
          <button mat-stroked-button routerLink="/transacciones">
            <mat-icon>swap_horiz</mat-icon>
            Ver Transacciones
          </button>
        </div>

        <!-- Información adicional -->
        <div class="error-help">
          <h3>¿Necesitas ayuda?</h3>
          <div class="help-options">
            <div class="help-option">
              <mat-icon>support_agent</mat-icon>
              <span>Contacta con soporte técnico</span>
            </div>
            <div class="help-option">
              <mat-icon>menu_book</mat-icon>
              <span>Consulta la documentación</span>
            </div>
            <div class="help-option">
              <mat-icon>bug_report</mat-icon>
              <span>Reportar un problema</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Decoración de fondo -->
      <div class="background-decoration">
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
        <div class="floating-shape shape-3"></div>
      </div>
    </div>
  `,
  styleUrls: ['./not-found.component.scss']
})
export class NotFoundComponent {
  // Este componente no necesita lógica adicional
  // Solo muestra la página de error 404 con opciones de navegación
} 