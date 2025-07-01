import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CuentaService } from '../../services/cuenta.service';
import { TransaccionService } from '../../services/transaccion.service';
import { Cuenta } from '../../models/cuenta.model';
import { Transaccion } from '../../models/transaccion.model';
import { EstadisticasCuentas } from '../../models/cuenta.model';

/**
 * Componente principal del dashboard que muestra:
 * - Resumen de cuentas de ahorros
 * - Estadísticas generales
 * - Transacciones recientes
 * - Acciones rápidas
 */
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  
  // Propiedades para almacenar datos
  cuentas: Cuenta[] = [];
  transaccionesRecientes: Transaccion[] = [];
  estadisticas: EstadisticasCuentas | null = null;
  
  // Estados de carga
  cargandoCuentas = false;
  cargandoTransacciones = false;
  cargandoEstadisticas = false;
  
  // Mensajes de error
  errorCuentas = '';
  errorTransacciones = '';
  errorEstadisticas = '';

  constructor(
    private cuentaService: CuentaService,
    private transaccionService: TransaccionService
  ) {}

  /**
   * Método que se ejecuta al inicializar el componente
   * Carga todos los datos necesarios para el dashboard
   */
  ngOnInit(): void {
    this.cargarDashboard();
  }

  /**
   * Carga todos los datos del dashboard de forma paralela
   */
  private cargarDashboard(): void {
    this.cargarCuentas();
    this.cargarTransaccionesRecientes();
    this.cargarEstadisticas();
  }

  /**
   * Carga la lista de cuentas de ahorros
   */
  private cargarCuentas(): void {
    this.cargandoCuentas = true;
    this.errorCuentas = '';

    this.cuentaService.obtenerCuentas().subscribe({
      next: (cuentas) => {
        this.cuentas = cuentas;
        this.cargandoCuentas = false;
      },
      error: (error) => {
        console.error('Error al cargar cuentas:', error);
        this.errorCuentas = 'Error al cargar las cuentas';
        this.cargandoCuentas = false;
      }
    });
  }

  /**
   * Carga las transacciones más recientes
   */
  private cargarTransaccionesRecientes(): void {
    this.cargandoTransacciones = true;
    this.errorTransacciones = '';

    this.transaccionService.obtenerTransacciones().subscribe({
      next: (transacciones) => {
        // Ordenar por fecha descendente y tomar las 5 más recientes
        this.transaccionesRecientes = transacciones
          .sort((a, b) => new Date(b.fechaTransaccion).getTime() - new Date(a.fechaTransaccion).getTime())
          .slice(0, 5);
        this.cargandoTransacciones = false;
      },
      error: (error) => {
        console.error('Error al cargar transacciones:', error);
        this.errorTransacciones = 'Error al cargar las transacciones';
        this.cargandoTransacciones = false;
      }
    });
  }

  /**
   * Carga las estadísticas generales de las cuentas
   */
  private cargarEstadisticas(): void {
    this.cargandoEstadisticas = true;
    this.errorEstadisticas = '';

    this.cuentaService.obtenerEstadisticas().subscribe({
      next: (estadisticas) => {
        this.estadisticas = estadisticas;
        this.cargandoEstadisticas = false;
      },
      error: (error) => {
        console.error('Error al cargar estadísticas:', error);
        this.errorEstadisticas = 'Error al cargar las estadísticas';
        this.cargandoEstadisticas = false;
      }
    });
  }

  /**
   * Calcula el saldo total de todas las cuentas
   */
  get saldoTotal(): number {
    return this.cuentas.reduce((total, cuenta) => total + parseFloat(cuenta.saldo), 0);
  }

  /**
   * Obtiene el número total de cuentas
   */
  get totalCuentas(): number {
    return this.cuentas.length;
  }

  /**
   * Formatea un número como moneda
   */
  formatearMoneda(valor: number): string {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR'
    }).format(valor);
  }

  /**
   * Formatea una fecha para mostrar
   */
  formatearFecha(fecha: string): string {
    return new Date(fecha).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  /**
   * Obtiene la clase CSS para el tipo de transacción
   */
  getClaseTransaccion(tipo: string): string {
    return tipo === 'DEPOSITO' ? 'deposito' : 'retiro';
  }

  /**
   * Recarga todos los datos del dashboard
   */
  recargarDashboard(): void {
    this.cargarDashboard();
  }

  /**
   * Método helper para parsear strings a números
   */
  parseFloat(valor: string): number {
    return parseFloat(valor);
  }

  /**
   * Métodos públicos para reintentar carga desde el template
   */
  reintentarCargarCuentas(): void {
    this.cargarCuentas();
  }

  reintentarCargarTransacciones(): void {
    this.cargarTransaccionesRecientes();
  }
} 