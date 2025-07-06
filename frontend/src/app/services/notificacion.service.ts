import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { Notificacion, NotificacionCreateRequest, NotificacionCountResponse } from '../models/notificacion.model';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class NotificacionService {
  private apiUrl = 'http://localhost:8080/api/notificaciones';
  private notificacionesNoLeidasSubject = new BehaviorSubject<number>(0);
  public notificacionesNoLeidas$ = this.notificacionesNoLeidasSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Obtiene todas las notificaciones de un usuario
   */
  obtenerPorUsuario(usuarioId: number): Observable<Notificacion[]> {
    return this.http.get<Notificacion[]>(`${this.apiUrl}/usuario/${usuarioId}`);
  }

  /**
   * Obtiene las notificaciones no leídas de un usuario
   */
  obtenerNoLeidas(usuarioId: number): Observable<Notificacion[]> {
    return this.http.get<Notificacion[]>(`${this.apiUrl}/usuario/${usuarioId}/no-leidas`);
  }

  /**
   * Cuenta las notificaciones no leídas de un usuario
   */
  contarNoLeidas(usuarioId: number): Observable<NotificacionCountResponse> {
    return this.http.get<NotificacionCountResponse>(`${this.apiUrl}/usuario/${usuarioId}/contar-no-leidas`)
      .pipe(
        tap(response => {
          this.notificacionesNoLeidasSubject.next(response.count);
        })
      );
  }

  /**
   * Obtiene una notificación por ID
   */
  obtenerPorId(id: number): Observable<Notificacion> {
    return this.http.get<Notificacion>(`${this.apiUrl}/${id}`);
  }

  /**
   * Crea una nueva notificación
   */
  crear(createRequest: NotificacionCreateRequest): Observable<Notificacion> {
    return this.http.post<Notificacion>(this.apiUrl, createRequest);
  }

  /**
   * Marca una notificación como leída
   */
  marcarComoLeida(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/leer`, {});
  }

  /**
   * Marca todas las notificaciones de un usuario como leídas
   */
  marcarTodasComoLeidas(usuarioId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/usuario/${usuarioId}/leer-todas`, {})
      .pipe(
        tap(() => {
          this.notificacionesNoLeidasSubject.next(0);
        })
      );
  }

  /**
   * Elimina una notificación
   */
  eliminar(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  /**
   * Crea una notificación automática para transacciones
   */
  crearNotificacionTransaccion(usuarioId: number, tipoTransaccion: string, monto: string, numeroCuenta: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/transaccion`, {
      usuarioId,
      tipoTransaccion,
      monto,
      numeroCuenta
    });
  }

  /**
   * Crea una notificación de saldo bajo
   */
  crearNotificacionSaldoBajo(usuarioId: number, numeroCuenta: string, saldo: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/saldo-bajo`, {
      usuarioId,
      numeroCuenta,
      saldo
    });
  }

  /**
   * Obtiene el contador actual de notificaciones no leídas
   */
  getNotificacionesNoLeidasCount(): number {
    return this.notificacionesNoLeidasSubject.value;
  }

  /**
   * Actualiza el contador de notificaciones no leídas
   */
  actualizarContadorNoLeidas(count: number): void {
    this.notificacionesNoLeidasSubject.next(count);
  }
} 