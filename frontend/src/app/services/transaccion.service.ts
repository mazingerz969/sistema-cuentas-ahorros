import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';

import { 
  Transaccion, 
  NuevaTransaccion, 
  TransaccionesResponse, 
  EstadisticasTransaccionesResponse,
  EstadisticasGlobalesResponse,
  EstadisticasTransacciones,
  EstadisticasGlobales,
  TipoTransaccion 
} from '../models/transaccion.model';

/**
 * Servicio para manejar las operaciones de transacciones de cuentas de ahorros.
 * 
 * Este servicio proporciona métodos para comunicarse con la API del backend
 * y manejar todas las operaciones relacionadas con las transacciones:
 * - Realizar depósitos
 * - Realizar retiros
 * - Obtener historial de transacciones
 * - Obtener estadísticas de transacciones
 * 
 * Utiliza RxJS para manejar las respuestas de forma reactiva y
 * BehaviorSubject para mantener el estado de las transacciones en la aplicación.
 */
@Injectable({
  providedIn: 'root'
})
export class TransaccionService {

  /**
   * URL base de la API del backend.
   * Se configura para apuntar al servidor Spring Boot.
   */
  private readonly API_URL = 'http://localhost:8080/api';

  /**
   * BehaviorSubject para mantener el estado de las transacciones.
   * Se actualiza automáticamente cuando se realizan operaciones.
   */
  private transaccionesSubject = new BehaviorSubject<Transaccion[]>([]);

  /**
   * Observable público para que los componentes se suscriban
   * y reciban actualizaciones automáticas del estado de las transacciones.
   */
  public transacciones$ = this.transaccionesSubject.asObservable();

  /**
   * Constructor del servicio.
   * 
   * @param http Cliente HTTP de Angular para realizar peticiones al backend
   */
  constructor(private http: HttpClient) {
    // Cargar las transacciones al inicializar el servicio
    this.cargarTransacciones();
  }

  /**
   * Obtiene todas las transacciones del sistema.
   * 
   * @returns Observable con la lista de transacciones
   */
  obtenerTransacciones(): Observable<Transaccion[]> {
    return this.http.get<TransaccionesResponse>(`${this.API_URL}/transacciones`)
      .pipe(
        tap(transacciones => {
          console.log('Transacciones obtenidas:', transacciones);
          this.transaccionesSubject.next(transacciones);
        }),
        catchError(this.manejarError)
      );
  }

  /**
   * Obtiene las transacciones de una cuenta específica.
   * 
   * @param cuentaId ID de la cuenta
   * @returns Observable con la lista de transacciones de la cuenta
   */
  obtenerTransaccionesPorCuenta(cuentaId: number): Observable<Transaccion[]> {
    return this.http.get<TransaccionesResponse>(`${this.API_URL}/transacciones/cuenta/${cuentaId}`)
      .pipe(
        tap(transacciones => console.log('Transacciones de cuenta obtenidas:', transacciones)),
        catchError(this.manejarError)
      );
  }

  /**
   * Obtiene una transacción específica por su ID.
   * 
   * @param id ID de la transacción
   * @returns Observable con la transacción encontrada
   */
  obtenerTransaccionPorId(id: number): Observable<Transaccion> {
    return this.http.get<Transaccion>(`${this.API_URL}/transacciones/${id}`)
      .pipe(
        tap(transaccion => console.log('Transacción obtenida:', transaccion)),
        catchError(this.manejarError)
      );
  }

  /**
   * Obtiene transacciones por tipo.
   * 
   * @param tipo Tipo de transacción (DEPOSITO o RETIRO)
   * @returns Observable con la lista de transacciones del tipo especificado
   */
  obtenerTransaccionesPorTipo(tipo: string): Observable<Transaccion[]> {
    return this.http.get<TransaccionesResponse>(`${this.API_URL}/transacciones/tipo/${tipo}`)
      .pipe(
        tap(transacciones => console.log('Transacciones por tipo obtenidas:', transacciones)),
        catchError(this.manejarError)
      );
  }

  /**
   * Obtiene transacciones de una cuenta por tipo.
   * 
   * @param cuentaId ID de la cuenta
   * @param tipo Tipo de transacción
   * @returns Observable con la lista de transacciones de la cuenta del tipo especificado
   */
  obtenerTransaccionesPorCuentaYTipo(cuentaId: number, tipo: string): Observable<Transaccion[]> {
    return this.http.get<TransaccionesResponse>(`${this.API_URL}/transacciones/cuenta/${cuentaId}/tipo/${tipo}`)
      .pipe(
        tap(transacciones => console.log('Transacciones de cuenta por tipo obtenidas:', transacciones)),
        catchError(this.manejarError)
      );
  }

  /**
   * Realiza un depósito en una cuenta.
   * 
   * @param nuevaTransaccion Datos de la transacción de depósito
   * @returns Observable con la transacción creada
   */
  realizarDeposito(nuevaTransaccion: NuevaTransaccion): Observable<Transaccion> {
    return this.http.post<Transaccion>(`${this.API_URL}/transacciones/deposito`, nuevaTransaccion)
      .pipe(
        tap(transaccion => {
          console.log('Depósito realizado:', transaccion);
          // Actualizar la lista de transacciones
          const transaccionesActuales = this.transaccionesSubject.value;
          this.transaccionesSubject.next([transaccion, ...transaccionesActuales]);
        }),
        catchError(this.manejarError)
      );
  }

  /**
   * Realiza un retiro de una cuenta.
   * 
   * @param nuevaTransaccion Datos de la transacción de retiro
   * @returns Observable con la transacción creada
   */
  realizarRetiro(nuevaTransaccion: NuevaTransaccion): Observable<Transaccion> {
    return this.http.post<Transaccion>(`${this.API_URL}/transacciones/retiro`, nuevaTransaccion)
      .pipe(
        tap(transaccion => {
          console.log('Retiro realizado:', transaccion);
          // Actualizar la lista de transacciones
          const transaccionesActuales = this.transaccionesSubject.value;
          this.transaccionesSubject.next([transaccion, ...transaccionesActuales]);
        }),
        catchError(this.manejarError)
      );
  }

  /**
   * Obtiene estadísticas de transacciones de una cuenta.
   * 
   * @param cuentaId ID de la cuenta
   * @returns Observable con las estadísticas de transacciones de la cuenta
   */
  obtenerEstadisticasTransacciones(cuentaId: number): Observable<EstadisticasTransacciones> {
    return this.http.get<EstadisticasTransaccionesResponse>(`${this.API_URL}/transacciones/estadisticas/cuenta/${cuentaId}`)
      .pipe(
        map(response => ({
          totalDepositos: response[0],
          totalRetiros: response[1],
          totalTransacciones: response[2]
        })),
        tap(estadisticas => console.log('Estadísticas de transacciones obtenidas:', estadisticas)),
        catchError(this.manejarError)
      );
  }

  /**
   * Obtiene el total de depósitos de una cuenta.
   * 
   * @param cuentaId ID de la cuenta
   * @returns Observable con el total de depósitos
   */
  obtenerTotalDepositos(cuentaId: number): Observable<string> {
    return this.http.get<string>(`${this.API_URL}/transacciones/depositos/cuenta/${cuentaId}`)
      .pipe(
        tap(total => console.log('Total de depósitos obtenido:', total)),
        catchError(this.manejarError)
      );
  }

  /**
   * Obtiene el total de retiros de una cuenta.
   * 
   * @param cuentaId ID de la cuenta
   * @returns Observable con el total de retiros
   */
  obtenerTotalRetiros(cuentaId: number): Observable<string> {
    return this.http.get<string>(`${this.API_URL}/transacciones/retiros/cuenta/${cuentaId}`)
      .pipe(
        tap(total => console.log('Total de retiros obtenido:', total)),
        catchError(this.manejarError)
      );
  }

  /**
   * Obtiene las transacciones más recientes.
   * 
   * @param limit Número máximo de transacciones a retornar (por defecto 10)
   * @returns Observable con la lista de transacciones más recientes
   */
  obtenerTransaccionesRecientes(limit: number = 10): Observable<Transaccion[]> {
    return this.http.get<TransaccionesResponse>(`${this.API_URL}/transacciones/recientes?limit=${limit}`)
      .pipe(
        tap(transacciones => console.log('Transacciones recientes obtenidas:', transacciones)),
        catchError(this.manejarError)
      );
  }

  /**
   * Obtiene estadísticas globales de transacciones.
   * 
   * @returns Observable con las estadísticas globales de transacciones
   */
  obtenerEstadisticasGlobales(): Observable<EstadisticasGlobales> {
    return this.http.get<EstadisticasGlobalesResponse>(`${this.API_URL}/transacciones/estadisticas/globales`)
      .pipe(
        map(response => ({
          totalDepositos: response[0],
          totalRetiros: response[1],
          totalTransacciones: response[2]
        })),
        tap(estadisticas => console.log('Estadísticas globales obtenidas:', estadisticas)),
        catchError(this.manejarError)
      );
  }

  /**
   * Carga las transacciones al inicializar el servicio.
   * Se llama automáticamente en el constructor.
   */
  private cargarTransacciones(): void {
    this.obtenerTransacciones().subscribe({
      error: (error) => console.error('Error al cargar transacciones:', error)
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

    console.error('Error en TransaccionService:', mensajeError);
    return throwError(() => new Error(mensajeError));
  }

  /**
   * Obtiene el valor actual de las transacciones.
   * Útil para componentes que necesitan el valor actual sin suscribirse.
   * 
   * @returns Lista actual de transacciones
   */
  getTransaccionesActuales(): Transaccion[] {
    return this.transaccionesSubject.value;
  }

  /**
   * Actualiza manualmente la lista de transacciones.
   * Útil para sincronizar el estado después de operaciones externas.
   * 
   * @param transacciones Nueva lista de transacciones
   */
  actualizarTransacciones(transacciones: Transaccion[]): void {
    this.transaccionesSubject.next(transacciones);
  }

  /**
   * Filtra las transacciones por tipo.
   * 
   * @param transacciones Lista de transacciones a filtrar
   * @param tipo Tipo de transacción a filtrar
   * @returns Lista filtrada de transacciones
   */
  filtrarTransaccionesPorTipo(transacciones: Transaccion[], tipo: TipoTransaccion): Transaccion[] {
    return transacciones.filter(transaccion => transaccion.tipo === tipo);
  }

  /**
   * Filtra las transacciones por cuenta.
   * 
   * @param transacciones Lista de transacciones a filtrar
   * @param cuentaId ID de la cuenta a filtrar
   * @returns Lista filtrada de transacciones
   */
  filtrarTransaccionesPorCuenta(transacciones: Transaccion[], cuentaId: number): Transaccion[] {
    return transacciones.filter(transaccion => transaccion.cuentaId === cuentaId);
  }

  /**
   * Ordena las transacciones por fecha (más recientes primero).
   * 
   * @param transacciones Lista de transacciones a ordenar
   * @returns Lista ordenada de transacciones
   */
  ordenarTransaccionesPorFecha(transacciones: Transaccion[]): Transaccion[] {
    return transacciones.sort((a, b) => 
      new Date(b.fechaTransaccion).getTime() - new Date(a.fechaTransaccion).getTime()
    );
  }
} 