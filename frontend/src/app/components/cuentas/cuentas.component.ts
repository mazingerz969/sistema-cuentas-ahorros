import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CuentaService } from '../../services/cuenta.service';
import { Cuenta, NuevaCuenta, ActualizarCuenta } from '../../models/cuenta.model';

/**
 * Componente para gestionar cuentas de ahorros.
 * 
 * Este componente permite:
 * - Ver todas las cuentas
 * - Crear nuevas cuentas
 * - Editar cuentas existentes
 * - Activar/desactivar cuentas
 * - Eliminar cuentas
 * - Realizar transacciones
 */
@Component({
  selector: 'app-cuentas',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './cuentas.component.html',
  styleUrls: ['./cuentas.component.scss']
})
export class CuentasComponent implements OnInit {
  
  // Propiedades para almacenar datos
  cuentas: Cuenta[] = [];
  cuentasFiltradas: Cuenta[] = [];
  
  // Estados de carga
  cargando = false;
  guardando = false;
  eliminando = false;
  
  // Mensajes de error y éxito
  error = '';
  mensajeExito = '';
  
  // Filtros y búsqueda
  filtroActivas = false;
  terminoBusqueda = '';
  
  // Formulario de nueva cuenta
  nuevaCuenta: NuevaCuenta = {
    numeroCuenta: '',
    titular: '',
    saldo: ''
  };
  
  // Formulario de edición
  cuentaEditando: Cuenta | null = null;
  cuentaActualizada: ActualizarCuenta = {};
  
  // Estados de UI
  mostrandoFormularioNueva = false;
  mostrandoFormularioEdicion = false;
  cuentaSeleccionadaParaEliminar: Cuenta | null = null;

  constructor(private cuentaService: CuentaService) {}

  /**
   * Método que se ejecuta al inicializar el componente
   */
  ngOnInit(): void {
    this.cargarCuentas();
  }

  /**
   * Carga todas las cuentas desde el backend
   */
  cargarCuentas(): void {
    this.cargando = true;
    this.error = '';

    this.cuentaService.obtenerCuentas().subscribe({
      next: (cuentas) => {
        this.cuentas = cuentas;
        this.aplicarFiltros();
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error al cargar cuentas:', error);
        this.error = 'Error al cargar las cuentas';
        this.cargando = false;
      }
    });
  }

  /**
   * Aplica los filtros de búsqueda y estado
   */
  aplicarFiltros(): void {
    let filtradas = [...this.cuentas];

    // Filtro por estado activo/inactivo
    if (this.filtroActivas) {
      filtradas = filtradas.filter(cuenta => cuenta.activa);
    }

    // Filtro por término de búsqueda
    if (this.terminoBusqueda.trim()) {
      const termino = this.terminoBusqueda.toLowerCase();
      filtradas = filtradas.filter(cuenta => 
        cuenta.numeroCuenta.toLowerCase().includes(termino) ||
        cuenta.titular.toLowerCase().includes(termino)
      );
    }

    this.cuentasFiltradas = filtradas;
  }

  /**
   * Crea una nueva cuenta
   */
  crearCuenta(): void {
    if (!this.validarNuevaCuenta()) {
      return;
    }

    this.guardando = true;
    this.error = '';

    this.cuentaService.crearCuenta(this.nuevaCuenta).subscribe({
      next: (cuenta) => {
        this.cuentas.unshift(cuenta);
        this.aplicarFiltros();
        this.mensajeExito = 'Cuenta creada exitosamente';
        this.resetearFormularioNueva();
        this.guardando = false;
        
        // Limpiar mensaje de éxito después de 3 segundos
        setTimeout(() => {
          this.mensajeExito = '';
        }, 3000);
      },
      error: (error) => {
        console.error('Error al crear cuenta:', error);
        this.error = 'Error al crear la cuenta';
        this.guardando = false;
      }
    });
  }

  /**
   * Inicia el modo de edición para una cuenta
   */
  iniciarEdicion(cuenta: Cuenta): void {
    this.cuentaEditando = cuenta;
    this.cuentaActualizada = {
      titular: cuenta.titular,
      activa: cuenta.activa
    };
    this.mostrandoFormularioEdicion = true;
  }

  /**
   * Guarda los cambios de una cuenta editada
   */
  guardarEdicion(): void {
    if (!this.cuentaEditando || !this.validarCuentaActualizada()) {
      return;
    }

    this.guardando = true;
    this.error = '';

    this.cuentaService.actualizarCuenta(this.cuentaEditando.id, this.cuentaActualizada).subscribe({
      next: (cuentaActualizada) => {
        const index = this.cuentas.findIndex(c => c.id === cuentaActualizada.id);
        if (index !== -1) {
          this.cuentas[index] = cuentaActualizada;
          this.aplicarFiltros();
        }
        this.mensajeExito = 'Cuenta actualizada exitosamente';
        this.cancelarEdicion();
        this.guardando = false;
        
        setTimeout(() => {
          this.mensajeExito = '';
        }, 3000);
      },
      error: (error) => {
        console.error('Error al actualizar cuenta:', error);
        this.error = 'Error al actualizar la cuenta';
        this.guardando = false;
      }
    });
  }

  /**
   * Cancela el modo de edición
   */
  cancelarEdicion(): void {
    this.cuentaEditando = null;
    this.cuentaActualizada = {};
    this.mostrandoFormularioEdicion = false;
  }

  /**
   * Elimina una cuenta
   */
  eliminarCuenta(cuenta: Cuenta): void {
    this.cuentaSeleccionadaParaEliminar = cuenta;
  }

  /**
   * Confirma la eliminación de una cuenta
   */
  confirmarEliminacion(): void {
    if (!this.cuentaSeleccionadaParaEliminar) {
      return;
    }

    this.eliminando = true;
    this.error = '';

    this.cuentaService.eliminarCuenta(this.cuentaSeleccionadaParaEliminar.id).subscribe({
      next: () => {
        this.cuentas = this.cuentas.filter(c => c.id !== this.cuentaSeleccionadaParaEliminar!.id);
        this.aplicarFiltros();
        this.mensajeExito = 'Cuenta eliminada exitosamente';
        this.cuentaSeleccionadaParaEliminar = null;
        this.eliminando = false;
        
        setTimeout(() => {
          this.mensajeExito = '';
        }, 3000);
      },
      error: (error) => {
        console.error('Error al eliminar cuenta:', error);
        this.error = 'Error al eliminar la cuenta';
        this.eliminando = false;
      }
    });
  }

  /**
   * Cancela la eliminación de una cuenta
   */
  cancelarEliminacion(): void {
    this.cuentaSeleccionadaParaEliminar = null;
  }

  /**
   * Realiza un depósito en una cuenta
   */
  realizarDeposito(cuenta: Cuenta): void {
    // Aquí se podría abrir un modal o navegar a la página de transacciones
    // Por ahora, solo navegamos a la página de transacciones
    window.location.href = `/transacciones/nueva?cuentaId=${cuenta.id}&tipo=deposito`;
  }

  /**
   * Realiza un retiro de una cuenta
   */
  realizarRetiro(cuenta: Cuenta): void {
    window.location.href = `/transacciones/nueva?cuentaId=${cuenta.id}&tipo=retiro`;
  }

  /**
   * Valida los datos de una nueva cuenta
   */
  private validarNuevaCuenta(): boolean {
    if (!this.nuevaCuenta.numeroCuenta.trim()) {
      this.error = 'El número de cuenta es obligatorio';
      return false;
    }
    
    if (!this.nuevaCuenta.titular.trim()) {
      this.error = 'El titular es obligatorio';
      return false;
    }
    
    if (!this.nuevaCuenta.saldo || parseFloat(this.nuevaCuenta.saldo) < 0) {
      this.error = 'El saldo debe ser un número positivo';
      return false;
    }
    
    return true;
  }

  /**
   * Valida los datos de una cuenta actualizada
   */
  private validarCuentaActualizada(): boolean {
    if (this.cuentaActualizada.titular !== undefined && !this.cuentaActualizada.titular.trim()) {
      this.error = 'El titular no puede estar vacío';
      return false;
    }
    
    return true;
  }

  /**
   * Resetea el formulario de nueva cuenta
   */
  resetearFormularioNueva(): void {
    this.nuevaCuenta = {
      numeroCuenta: '',
      titular: '',
      saldo: ''
    };
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
      day: 'numeric'
    });
  }

  /**
   * Obtiene la clase CSS para el estado de la cuenta
   */
  getClaseEstado(activa: boolean): string {
    return activa ? 'activa' : 'inactiva';
  }

  /**
   * Maneja el cambio en el filtro de cuentas activas
   */
  onFiltroActivasChange(): void {
    this.aplicarFiltros();
  }

  /**
   * Maneja el cambio en el término de búsqueda
   */
  onBusquedaChange(): void {
    this.aplicarFiltros();
  }
} 