import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TransaccionService } from '../../services/transaccion.service';
import { CuentaService } from '../../services/cuenta.service';
import { Transaccion, NuevaTransaccion, TipoTransaccion } from '../../models/transaccion.model';
import { Cuenta } from '../../models/cuenta.model';

/**
 * Componente para gestionar transacciones de cuentas de ahorros.
 * 
 * Este componente permite:
 * - Ver todas las transacciones
 * - Realizar depósitos y retiros
 * - Filtrar transacciones por cuenta y tipo
 * - Ver estadísticas de transacciones
 */
@Component({
  selector: 'app-transacciones',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './transacciones.component.html',
  styleUrls: ['./transacciones.component.scss']
})
export class TransaccionesComponent implements OnInit {
  // Hacer TipoTransaccion disponible en el template
  TipoTransaccion = TipoTransaccion;
  
  // Propiedades para almacenar datos
  transacciones: Transaccion[] = [];
  transaccionesFiltradas: Transaccion[] = [];
  cuentas: Cuenta[] = [];
  
  // Estados de carga
  cargando = false;
  guardando = false;
  
  // Mensajes de error y éxito
  error = '';
  mensajeExito = '';
  
  // Filtros
  filtroCuenta = '';
  filtroTipo = '';
  terminoBusqueda = '';
  
  // Formulario de nueva transacción
  nuevaTransaccion: NuevaTransaccion = {
    cuentaId: 0,
    monto: '',
    descripcion: ''
  };
  
  // Estados de UI
  mostrandoFormularioNueva = false;
  tipoTransaccionSeleccionada: TipoTransaccion = TipoTransaccion.DEPOSITO;

  constructor(
    private transaccionService: TransaccionService,
    private cuentaService: CuentaService
  ) {}

  /**
   * Método que se ejecuta al inicializar el componente
   */
  ngOnInit(): void {
    this.cargarDatos();
  }

  /**
   * Carga todos los datos necesarios
   */
  cargarDatos(): void {
    this.cargarTransacciones();
    this.cargarCuentas();
  }

  /**
   * Carga todas las transacciones desde el backend
   */
  cargarTransacciones(): void {
    this.cargando = true;
    this.error = '';

    this.transaccionService.obtenerTransacciones().subscribe({
      next: (transacciones) => {
        this.transacciones = transacciones;
        this.aplicarFiltros();
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error al cargar transacciones:', error);
        this.error = 'Error al cargar las transacciones';
        this.cargando = false;
      }
    });
  }

  /**
   * Carga todas las cuentas para el formulario
   */
  cargarCuentas(): void {
    this.cuentaService.obtenerCuentas().subscribe({
      next: (cuentas) => {
        this.cuentas = cuentas.filter(cuenta => cuenta.activa);
      },
      error: (error) => {
        console.error('Error al cargar cuentas:', error);
      }
    });
  }

  /**
   * Aplica los filtros de búsqueda
   */
  aplicarFiltros(): void {
    let filtradas = [...this.transacciones];

    // Filtro por cuenta
    if (this.filtroCuenta) {
      filtradas = filtradas.filter(transaccion => 
        transaccion.cuentaId.toString() === this.filtroCuenta
      );
    }

    // Filtro por tipo
    if (this.filtroTipo) {
      filtradas = filtradas.filter(transaccion => 
        transaccion.tipo === this.filtroTipo
      );
    }

    // Filtro por término de búsqueda
    if (this.terminoBusqueda.trim()) {
      const termino = this.terminoBusqueda.toLowerCase();
      filtradas = filtradas.filter(transaccion => 
        transaccion.numeroCuenta.toLowerCase().includes(termino) ||
        (transaccion.descripcion && transaccion.descripcion.toLowerCase().includes(termino)) ||
        transaccion.tipoDescripcion.toLowerCase().includes(termino)
      );
    }

    // Ordenar por fecha descendente (más recientes primero)
    filtradas.sort((a, b) => 
      new Date(b.fechaTransaccion).getTime() - new Date(a.fechaTransaccion).getTime()
    );

    this.transaccionesFiltradas = filtradas;
  }

  /**
   * Realiza una nueva transacción
   */
  realizarTransaccion(): void {
    if (!this.validarTransaccion()) {
      return;
    }

    this.guardando = true;
    this.error = '';

    if (this.tipoTransaccionSeleccionada === TipoTransaccion.DEPOSITO) {
      this.transaccionService.realizarDeposito(this.nuevaTransaccion).subscribe({
        next: (transaccionCreada) => {
          this.transacciones.unshift(transaccionCreada);
          this.aplicarFiltros();
          this.mensajeExito = 'Depósito realizado exitosamente';
          this.resetearFormulario();
          this.guardando = false;
          
          setTimeout(() => {
            this.mensajeExito = '';
          }, 3000);
        },
        error: (error) => {
          console.error('Error al realizar depósito:', error);
          this.error = 'Error al realizar el depósito';
          this.guardando = false;
        }
      });
    } else {
      this.transaccionService.realizarRetiro(this.nuevaTransaccion).subscribe({
        next: (transaccionCreada) => {
          this.transacciones.unshift(transaccionCreada);
          this.aplicarFiltros();
          this.mensajeExito = 'Retiro realizado exitosamente';
          this.resetearFormulario();
          this.guardando = false;
          
          setTimeout(() => {
            this.mensajeExito = '';
          }, 3000);
        },
        error: (error) => {
          console.error('Error al realizar retiro:', error);
          this.error = 'Error al realizar el retiro';
          this.guardando = false;
        }
      });
    }
  }

  /**
   * Valida los datos de la transacción
   */
  private validarTransaccion(): boolean {
    if (!this.nuevaTransaccion.cuentaId) {
      this.error = 'Debe seleccionar una cuenta';
      return false;
    }
    
    if (!this.nuevaTransaccion.monto || parseFloat(this.nuevaTransaccion.monto) <= 0) {
      this.error = 'El monto debe ser mayor a 0';
      return false;
    }
    
    return true;
  }

  /**
   * Resetea el formulario de nueva transacción
   */
  resetearFormulario(): void {
    this.nuevaTransaccion = {
      cuentaId: 0,
      monto: '',
      descripcion: ''
    };
    this.tipoTransaccionSeleccionada = TipoTransaccion.DEPOSITO;
    this.mostrandoFormularioNueva = false;
  }

  /**
   * Formatea un número como moneda
   */
  formatearMoneda(valor: string): string {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR'
    }).format(parseFloat(valor));
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
    return tipo === TipoTransaccion.DEPOSITO ? 'deposito' : 'retiro';
  }

  /**
   * Obtiene el icono para el tipo de transacción
   */
  getIconoTransaccion(tipo: string): string {
    return tipo === TipoTransaccion.DEPOSITO ? '➕' : '➖';
  }

  /**
   * Obtiene el nombre de la cuenta por ID
   */
  getNombreCuenta(cuentaId: number): string {
    const cuenta = this.cuentas.find(c => c.id === cuentaId);
    return cuenta ? cuenta.numeroCuenta : 'Cuenta no encontrada';
  }

  /**
   * Calcula el total de depósitos
   */
  get totalDepositos(): number {
    return this.transaccionesFiltradas
      .filter(t => t.tipo === TipoTransaccion.DEPOSITO)
      .reduce((total, t) => total + parseFloat(t.monto), 0);
  }

  /**
   * Calcula el total de retiros
   */
  get totalRetiros(): number {
    return this.transaccionesFiltradas
      .filter(t => t.tipo === TipoTransaccion.RETIRO)
      .reduce((total, t) => total + parseFloat(t.monto), 0);
  }

  /**
   * Calcula el balance neto
   */
  get balanceNeto(): number {
    return this.totalDepositos - this.totalRetiros;
  }

  /**
   * Maneja el cambio en los filtros
   */
  onFiltrosChange(): void {
    this.aplicarFiltros();
  }

  /**
   * Maneja el cambio en el término de búsqueda
   */
  onBusquedaChange(): void {
    this.aplicarFiltros();
  }

  /**
   * Cambia el tipo de transacción
   */
  cambiarTipoTransaccion(tipo: TipoTransaccion): void {
    this.tipoTransaccionSeleccionada = tipo;
  }

  /**
   * Obtiene las opciones de cuentas para el select
   */
  get opcionesCuentas(): Cuenta[] {
    return this.cuentas.filter(cuenta => cuenta.activa);
  }
} 