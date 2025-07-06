export interface Usuario {
  id: number;
  email: string;
  nombre: string;
  fechaRegistro: string;
  activo: boolean;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegistroRequest {
  email: string;
  nombre: string;
  password: string;
}

export interface UsuarioUpdateRequest {
  nombre: string;
  email: string;
}

export interface PasswordChangeRequest {
  password: string;
} 