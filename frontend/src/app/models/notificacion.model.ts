export interface Notificacion {
  id: number;
  mensaje: string;
  tipo: string;
  fechaCreacion: string;
  leida: boolean;
  usuarioId: number;
}

export interface NotificacionCreateRequest {
  mensaje: string;
  tipo: string;
  usuarioId: number;
}

export interface NotificacionTransaccionRequest {
  usuarioId: number;
  tipoTransaccion: string;
  monto: string;
  numeroCuenta: string;
}

export interface NotificacionSaldoBajoRequest {
  usuarioId: number;
  numeroCuenta: string;
  saldo: string;
}

export interface NotificacionCountResponse {
  count: number;
} 