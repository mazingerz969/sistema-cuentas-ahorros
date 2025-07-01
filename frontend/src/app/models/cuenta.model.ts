/**
 * Interfaz que representa una cuenta de ahorros en el frontend.
 * 
 * Esta interfaz define la estructura de datos que se recibe del backend
 * y se utiliza en toda la aplicación Angular para tipar los datos
 * relacionados con las cuentas de ahorros.
 * 
 * Los campos coinciden con el DTO CuentaDTO del backend para garantizar
 * la consistencia en la comunicación entre frontend y backend.
 */
export interface Cuenta {
  /**
   * Identificador único de la cuenta.
   * Se genera automáticamente en el backend.
   */
  id: number;

  /**
   * Número de cuenta único.
   * Identificador público de la cuenta.
   */
  numeroCuenta: string;

  /**
   * Nombre del titular de la cuenta.
   * Información del propietario de la cuenta.
   */
  titular: string;

  /**
   * Saldo actual de la cuenta.
   * Se usa string para evitar problemas de precisión con números decimales.
   */
  saldo: string;

  /**
   * Estado de la cuenta (activa/inactiva).
   * Indica si la cuenta está disponible para transacciones.
   */
  activa: boolean;

  /**
   * Fecha de creación de la cuenta.
   * Se recibe como string desde el backend.
   */
  fechaCreacion: string;

  /**
   * Fecha de la última actualización de la cuenta.
   * Se recibe como string desde el backend.
   */
  fechaActualizacion: string;
}

/**
 * Interfaz para crear una nueva cuenta.
 * 
 * Esta interfaz se usa en los formularios de creación de cuentas
 * y no incluye los campos que se generan automáticamente en el backend.
 */
export interface NuevaCuenta {
  /**
   * Número de cuenta único.
   * Debe ser proporcionado por el usuario.
   */
  numeroCuenta: string;

  /**
   * Nombre del titular de la cuenta.
   * Debe ser proporcionado por el usuario.
   */
  titular: string;

  /**
   * Saldo inicial de la cuenta.
   * Se usa string para evitar problemas de precisión.
   */
  saldo: string;
}

/**
 * Interfaz para actualizar una cuenta existente.
 * 
 * Esta interfaz se usa en los formularios de edición de cuentas
 * y solo incluye los campos que se pueden modificar.
 */
export interface ActualizarCuenta {
  /**
   * Nombre del titular de la cuenta.
   * Campo opcional para actualización.
   */
  titular?: string;

  /**
   * Estado de la cuenta.
   * Campo opcional para actualización.
   */
  activa?: boolean;
}

/**
 * Interfaz para las estadísticas de cuentas.
 * 
 * Se usa para mostrar información resumida sobre todas las cuentas.
 */
export interface EstadisticasCuentas {
  /**
   * Número total de cuentas.
   */
  totalCuentas: number;

  /**
   * Número de cuentas activas.
   */
  cuentasActivas: number;

  /**
   * Saldo total de todas las cuentas.
   */
  saldoTotal: string;
}

/**
 * Tipo para las respuestas de la API de cuentas.
 * 
 * Se usa para tipar las respuestas de los servicios HTTP.
 */
export type CuentasResponse = Cuenta[];

/**
 * Tipo para las respuestas de estadísticas.
 * 
 * Se usa para tipar las respuestas de estadísticas de la API.
 */
export type EstadisticasResponse = [number, number, string]; 