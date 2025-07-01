import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';

import { 
  Cuenta, 
  NuevaCuenta, 
  ActualizarCuenta, 
  CuentasResponse, 
  EstadisticasResponse,
  EstadisticasCuentas 
} from '../models/cuenta.model';

/**
 * Servicio para manejar las operaciones de cuentas de ahorros.
 * 
 * Este servicio proporciona métodos para comunicarse con la API del backend
 * y manejar todas las operaciones relacionadas con las cuentas:
 * - Obtener todas las cuentas
 * - Crear nuevas cuentas
 * - Actualizar cuentas existentes
 * - Eliminar cuentas
 * - Obtener estadísticas
 * 
 * Utiliza RxJS para manejar las respuestas de forma reactiva y
 * BehaviorSubject para mantener el estado de las cuentas en la aplicación.
 */
@Injectable({
  providedIn: 'root'
})
export class CuentaService {

  /**
   * URL base de la API del backend.
   * Se configura para apuntar al servidor Spring Boot.
   */
  private readonly API_URL = 'http://localhost:8080/api';

  /**
   * BehaviorSubject para mantener el estado de las cuentas.
   * Se actualiza automáticamente cuando se realizan operaciones CRUD.
   */
  private cuentasSubject = new BehaviorSubject<Cuenta[]>([]);

  /**
   * Observable público para que los componentes se suscriban
   * y reciban actualizaciones automáticas del estado de las cuentas.
   */
  public cuentas$ = this.cuentasSubject.asObservable();

  /**
   * Constructor del servicio.
   * 
   * @param http Cliente HTTP de Angular para realizar peticiones al backend
   */
  constructor(private http: HttpClient) {
    // Cargar las cuentas al inicializar el servicio
    this.cargarCuentas();
  }

  /**
   * Obtiene todas las cuentas del sistema.
   * 
   * @returns Observable con la lista de cuentas
   */
  obtenerCuentas(): Observable<Cuenta[]> {
    return this.http.get<CuentasResponse>(`${this.API_URL}/cuentas`)
      .pipe(
        tap(cuentas => {
          console.log('Cuentas obtenidas:', cuentas);
          this.cuentasSubject.next(cuentas);
        }),
        catchError(this.manejarError)
      );
  }

  /**
   * Obtiene una cuenta específica por su ID.
   * 
   * @param id ID de la cuenta a obtener
   * @returns Observable con la cuenta encontrada
   */
  obtenerCuentaPorId(id: number): Observable<Cuenta> {
    return this.http.get<Cuenta>(`${this.API_URL}/cuentas/${id}`)
      .pipe(
        tap(cuenta => console.log('Cuenta obtenida:', cuenta)),
        catchError(this.manejarError)
      );
  }

  /**
   * Obtiene una cuenta específica por su número de cuenta.
   * 
   * @param numeroCuenta Número de cuenta a buscar
   * @returns Observable con la cuenta encontrada
   */
  obtenerCuentaPorNumero(numeroCuenta: string): Observable<Cuenta> {
    return this.http.get<Cuenta>(`${this.API_URL}/cuentas/numero/${numeroCuenta}`)
      .pipe(
        tap(cuenta => console.log('Cuenta obtenida por número:', cuenta)),
        catchError(this.manejarError)
      );
  }

  /**
   * Busca cuentas por nombre del titular.
   * 
   * @param titular Nombre del titular (parcial)
   * @returns Observable con la lista de cuentas que coinciden
   */
  buscarCuentasPorTitular(titular: string): Observable<Cuenta[]> {
    return this.http.get<CuentasResponse>(`${this.API_URL}/cuentas/buscar?titular=${titular}`)
      .pipe(
        tap(cuentas => console.log('Cuentas encontradas por titular:', cuentas)),
        catchError(this.manejarError)
      );
  }

  /**
   * Obtiene todas las cuentas activas.
   * 
   * @returns Observable con la lista de cuentas activas
   */
  obtenerCuentasActivas(): Observable<Cuenta[]> {
    return this.http.get<CuentasResponse>(`${this.API_URL}/cuentas/activas`)
      .pipe(
        tap(cuentas => console.log('Cuentas activas obtenidas:', cuentas)),
        catchError(this.manejarError)
      );
  }

  /**
   * Crea una nueva cuenta de ahorros.
   * 
   * @param nuevaCuenta Datos de la nueva cuenta
   * @returns Observable con la cuenta creada
   */
  crearCuenta(nuevaCuenta: NuevaCuenta): Observable<Cuenta> {
    return this.http.post<Cuenta>(`${this.API_URL}/cuentas`, nuevaCuenta)
      .pipe(
        tap(cuenta => {
          console.log('Cuenta creada:', cuenta);
          // Actualizar la lista de cuentas
          const cuentasActuales = this.cuentasSubject.value;
          this.cuentasSubject.next([...cuentasActuales, cuenta]);
        }),
        catchError(this.manejarError)
      );
  }

  /**
   * Actualiza una cuenta existente.
   * 
   * @param id ID de la cuenta a actualizar
   * @param datosActualizacion Datos a actualizar
   * @returns Observable con la cuenta actualizada
   */
  actualizarCuenta(id: number, datosActualizacion: ActualizarCuenta): Observable<Cuenta> {
    return this.http.put<Cuenta>(`${this.API_URL}/cuentas/${id}`, datosActualizacion)
      .pipe(
        tap(cuenta => {
          console.log('Cuenta actualizada:', cuenta);
          // Actualizar la cuenta en la lista
          const cuentasActuales = this.cuentasSubject.value;
          const cuentasActualizadas = cuentasActuales.map(c => 
            c.id === id ? cuenta : c
          );
          this.cuentasSubject.next(cuentasActualizadas);
        }),
        catchError(this.manejarError)
      );
  }

  /**
   * Elimina una cuenta.
   * 
   * @param id ID de la cuenta a eliminar
   * @returns Observable que se completa cuando la cuenta se elimina
   */
  eliminarCuenta(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/cuentas/${id}`)
      .pipe(
        tap(() => {
          console.log('Cuenta eliminada con ID:', id);
          // Remover la cuenta de la lista
          const cuentasActuales = this.cuentasSubject.value;
          const cuentasFiltradas = cuentasActuales.filter(c => c.id !== id);
          this.cuentasSubject.next(cuentasFiltradas);
        }),
        catchError(this.manejarError)
      );
  }

  /**
   * Obtiene estadísticas de las cuentas.
   * 
   * @returns Observable con las estadísticas de cuentas
   */
  obtenerEstadisticas(): Observable<EstadisticasCuentas> {
    return this.http.get<EstadisticasResponse>(`${this.API_URL}/cuentas/estadisticas`)
      .pipe(
        map(response => ({
          totalCuentas: response[0],
          cuentasActivas: response[1],
          saldoTotal: response[2]
        })),
        tap(estadisticas => console.log('Estadísticas obtenidas:', estadisticas)),
        catchError(this.manejarError)
      );
  }

  /**
   * Obtiene cuentas ordenadas por saldo (mayor a menor).
   * 
   * @returns Observable con la lista de cuentas ordenadas
   */
  obtenerCuentasOrdenadasPorSaldo(): Observable<Cuenta[]> {
    return this.http.get<CuentasResponse>(`${this.API_URL}/cuentas/ordenadas/saldo`)
      .pipe(
        tap(cuentas => console.log('Cuentas ordenadas por saldo:', cuentas)),
        catchError(this.manejarError)
      );
  }

  /**
   * Obtiene cuentas con saldo superior al promedio.
   * 
   * @returns Observable con la lista de cuentas con saldo superior al promedio
   */
  obtenerCuentasConSaldoSuperiorAlPromedio(): Observable<Cuenta[]> {
    return this.http.get<CuentasResponse>(`${this.API_URL}/cuentas/superior-promedio`)
      .pipe(
        tap(cuentas => console.log('Cuentas con saldo superior al promedio:', cuentas)),
        catchError(this.manejarError)
      );
  }

  /**
   * Carga las cuentas al inicializar el servicio.
   * Se llama automáticamente en el constructor.
   */
  private cargarCuentas(): void {
    this.obtenerCuentas().subscribe({
      error: (error) => console.error('Error al cargar cuentas:', error)
    });
  }

  /**
   * Maneja los errores de las peticiones HTTP.
   * 
   * @param error Error de la petición HTTP
   * @returns Observable que emite el error
   */
  private manejarError(error: HttpErrorResponse): Observable<never> {
    let mensajeError = 'Ocurrió un error desconocido';

    if (error.error instanceof ErrorEvent) {
      // Error del lado del cliente
      mensajeError = `Error: ${error.error.message}`;
    } else {
      // Error del lado del servidor
      mensajeError = `Código de error: ${error.status}\nMensaje: ${error.message}`;
      
      if (error.error && error.error.message) {
        mensajeError = error.error.message;
      }
    }

    console.error('Error en CuentaService:', mensajeError);
    return throwError(() => new Error(mensajeError));
  }

  /**
   * Obtiene el valor actual de las cuentas.
   * Útil para componentes que necesitan el valor actual sin suscribirse.
   * 
   * @returns Lista actual de cuentas
   */
  getCuentasActuales(): Cuenta[] {
    return this.cuentasSubject.value;
  }

  /**
   * Actualiza manualmente la lista de cuentas.
   * Útil para sincronizar el estado después de operaciones externas.
   * 
   * @param cuentas Nueva lista de cuentas
   */
  actualizarCuentas(cuentas: Cuenta[]): void {
    this.cuentasSubject.next(cuentas);
  }
} 