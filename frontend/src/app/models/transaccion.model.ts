/**
 * Interfaz que representa una transacción en el frontend.
 * 
 * Esta interfaz define la estructura de datos que se recibe del backend
 * y se utiliza en toda la aplicación Angular para tipar los datos
 * relacionados con las transacciones de cuentas de ahorros.
 * 
 * Los campos coinciden con el DTO TransaccionDTO del backend para garantizar
 * la consistencia en la comunicación entre frontend y backend.
 */
export interface Transaccion {
  /**
   * Identificador único de la transacción.
   * Se genera automáticamente en el backend.
   */
  id: number;

  /**
   * Tipo de transacción (DEPOSITO o RETIRO).
   * Se recibe como string desde el backend.
   */
  tipo: string;

  /**
   * Descripción del tipo de transacción.
   * Texto legible para mostrar en la interfaz.
   */
  tipoDescripcion: string;

  /**
   * Monto de la transacción.
   * Se usa string para evitar problemas de precisión con números decimales.
   */
  monto: string;

  /**
   * Saldo de la cuenta después de la transacción.
   * Se usa string para evitar problemas de precisión.
   */
  saldoResultante: string;

  /**
   * Descripción opcional de la transacción.
   * Puede ser null si no se proporcionó descripción.
   */
  descripcion: string | null;

  /**
   * Fecha y hora de la transacción.
   * Se recibe como string desde el backend.
   */
  fechaTransaccion: string;

  /**
   * ID de la cuenta asociada a la transacción.
   * Referencia a la cuenta que realizó la transacción.
   */
  cuentaId: number;

  /**
   * Número de cuenta asociada.
   * Información útil para mostrar en la interfaz.
   */
  numeroCuenta: string;
}

/**
 * Interfaz para crear una nueva transacción.
 * 
 * Esta interfaz se usa en los formularios de creación de transacciones
 * y no incluye los campos que se generan automáticamente en el backend.
 */
export interface NuevaTransaccion {
  /**
   * ID de la cuenta donde se realizará la transacción.
   * Debe ser proporcionado por el usuario.
   */
  cuentaId: number;

  /**
   * Monto de la transacción.
   * Debe ser proporcionado por el usuario.
   */
  monto: string;

  /**
   * Descripción opcional de la transacción.
   * Campo opcional para proporcionar contexto.
   */
  descripcion?: string;
}

/**
 * Interfaz para las estadísticas de transacciones.
 * 
 * Se usa para mostrar información resumida sobre las transacciones de una cuenta.
 */
export interface EstadisticasTransacciones {
  /**
   * Total de depósitos de la cuenta.
   */
  totalDepositos: string;

  /**
   * Total de retiros de la cuenta.
   */
  totalRetiros: string;

  /**
   * Número total de transacciones de la cuenta.
   */
  totalTransacciones: number;
}

/**
 * Interfaz para las estadísticas globales de transacciones.
 * 
 * Se usa para mostrar información resumida sobre todas las transacciones del sistema.
 */
export interface EstadisticasGlobales {
  /**
   * Total de depósitos de todas las cuentas.
   */
  totalDepositos: string;

  /**
   * Total de retiros de todas las cuentas.
   */
  totalRetiros: string;

  /**
   * Número total de transacciones del sistema.
   */
  totalTransacciones: number;
}

/**
 * Enum para los tipos de transacción.
 * 
 * Se usa para tipar los valores de tipo de transacción
 * y evitar errores de tipeo en el código.
 */
export enum TipoTransaccion {
  DEPOSITO = 'DEPOSITO',
  RETIRO = 'RETIRO'
}

/**
 * Tipo para las respuestas de la API de transacciones.
 * 
 * Se usa para tipar las respuestas de los servicios HTTP.
 */
export type TransaccionesResponse = Transaccion[];

/**
 * Tipo para las respuestas de estadísticas de transacciones.
 * 
 * Se usa para tipar las respuestas de estadísticas de la API.
 */
export type EstadisticasTransaccionesResponse = [string, string, number];

/**
 * Tipo para las respuestas de estadísticas globales.
 * 
 * Se usa para tipar las respuestas de estadísticas globales de la API.
 */
export type EstadisticasGlobalesResponse = [string, string, number]; 