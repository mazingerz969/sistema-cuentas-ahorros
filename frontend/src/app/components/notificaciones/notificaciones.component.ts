import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil, interval } from 'rxjs';
import { Notificacion } from '../../models/notificacion.model';
import { Usuario } from '../../models/usuario.model';
import { NotificacionService } from '../../services/notificacion.service';
import { UsuarioService } from '../../services/usuario.service';

@Component({
  selector: 'app-notificaciones',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notificaciones.component.html',
  styleUrls: ['./notificaciones.component.scss']
})
export class NotificacionesComponent implements OnInit, OnDestroy {
  notificaciones: Notificacion[] = [];
  usuario: Usuario | null = null;
  isLoading: boolean = false;
  errorMessage: string = '';
  private destroy$ = new Subject<void>();

  constructor(
    private notificacionService: NotificacionService,
    private usuarioService: UsuarioService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.usuario = this.usuarioService.getCurrentUser();
    
    if (!this.usuario) {
      this.router.navigate(['/login']);
      return;
    }

    this.cargarNotificaciones();
    
    // Actualizar notificaciones cada 30 segundos
    interval(30000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.cargarNotificaciones();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  cargarNotificaciones(): void {
    if (!this.usuario) return;

    this.isLoading = true;
    this.errorMessage = '';

    this.notificacionService.obtenerPorUsuario(this.usuario.id).subscribe({
      next: (notificaciones) => {
        this.notificaciones = notificaciones;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al cargar notificaciones:', error);
        this.errorMessage = 'Error al cargar las notificaciones';
        this.isLoading = false;
      }
    });
  }

  marcarComoLeida(notificacion: Notificacion): void {
    if (notificacion.leida) return;

    this.notificacionService.marcarComoLeida(notificacion.id).subscribe({
      next: () => {
        notificacion.leida = true;
        // Actualizar contador global
        const currentCount = this.notificacionService.getNotificacionesNoLeidasCount();
        if (currentCount > 0) {
          this.notificacionService.actualizarContadorNoLeidas(currentCount - 1);
        }
      },
      error: (error) => {
        console.error('Error al marcar notificación como leída:', error);
      }
    });
  }

  marcarTodasComoLeidas(): void {
    if (!this.usuario) return;

    this.notificacionService.marcarTodasComoLeidas(this.usuario.id).subscribe({
      next: () => {
        this.notificaciones.forEach(n => n.leida = true);
      },
      error: (error) => {
        console.error('Error al marcar todas como leídas:', error);
      }
    });
  }

  eliminarNotificacion(notificacion: Notificacion): void {
    this.notificacionService.eliminar(notificacion.id).subscribe({
      next: () => {
        this.notificaciones = this.notificaciones.filter(n => n.id !== notificacion.id);
        // Si era no leída, actualizar contador
        if (!notificacion.leida) {
          const currentCount = this.notificacionService.getNotificacionesNoLeidasCount();
          if (currentCount > 0) {
            this.notificacionService.actualizarContadorNoLeidas(currentCount - 1);
          }
        }
      },
      error: (error) => {
        console.error('Error al eliminar notificación:', error);
      }
    });
  }

  getTipoIcono(tipo: string): string {
    switch (tipo) {
      case 'TRANSACCION_DEPOSITO':
        return '💰';
      case 'TRANSACCION_RETIRO':
        return '💸';
      case 'SALDO_BAJO':
        return '⚠️';
      default:
        return '📢';
    }
  }

  getTipoColor(tipo: string): string {
    switch (tipo) {
      case 'TRANSACCION_DEPOSITO':
        return 'success';
      case 'TRANSACCION_RETIRO':
        return 'warning';
      case 'SALDO_BAJO':
        return 'error';
      default:
        return 'info';
    }
  }

  formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    const ahora = new Date();
    const diffMs = ahora.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) {
      return 'Ahora mismo';
    } else if (diffMins < 60) {
      return `Hace ${diffMins} minuto${diffMins > 1 ? 's' : ''}`;
    } else if (diffHours < 24) {
      return `Hace ${diffHours} hora${diffHours > 1 ? 's' : ''}`;
    } else if (diffDays < 7) {
      return `Hace ${diffDays} día${diffDays > 1 ? 's' : ''}`;
    } else {
      return date.toLocaleDateString('es-ES', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      });
    }
  }

  hayNotificacionesNoLeidas(): boolean {
    return this.notificaciones.some(n => !n.leida);
  }
} 