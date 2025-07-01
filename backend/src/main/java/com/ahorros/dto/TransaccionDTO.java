package com.ahorros.dto;

import com.ahorros.models.Transaccion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la entidad Transaccion.
 * 
 * Este DTO se utiliza para transferir información de transacciones entre
 * el frontend y el backend, incluyendo solo los datos necesarios para
 * la comunicación, sin las relaciones complejas de JPA.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDTO {

    /**
     * Identificador único de la transacción.
     */
    private Long id;

    /**
     * Tipo de transacción (DEPOSITO o RETIRO).
     * Se envía como String para facilitar la serialización JSON.
     */
    private String tipo;

    /**
     * Descripción del tipo de transacción.
     * Texto legible para mostrar en la interfaz de usuario.
     */
    private String tipoDescripcion;

    /**
     * Monto de la transacción.
     */
    private BigDecimal monto;

    /**
     * Saldo de la cuenta después de la transacción.
     */
    private BigDecimal saldoResultante;

    /**
     * Descripción opcional de la transacción.
     */
    private String descripcion;

    /**
     * Fecha y hora de la transacción.
     */
    private LocalDateTime fechaTransaccion;

    /**
     * ID de la cuenta asociada a la transacción.
     * Se envía solo el ID para evitar referencias circulares.
     */
    private Long cuentaId;

    /**
     * Número de cuenta asociada.
     * Información útil para mostrar en la interfaz.
     */
    private String numeroCuenta;

    /**
     * Constructor que crea un DTO a partir de una entidad Transaccion.
     * 
     * @param transaccion La entidad Transaccion de la cual crear el DTO
     */
    public TransaccionDTO(Transaccion transaccion) {
        this.id = transaccion.getId();
        this.tipo = transaccion.getTipo().name();
        this.tipoDescripcion = transaccion.getTipoDescripcion();
        this.monto = transaccion.getMonto();
        this.saldoResultante = transaccion.getSaldoResultante();
        this.descripcion = transaccion.getDescripcion();
        this.fechaTransaccion = transaccion.getFechaTransaccion();
        
        // Extraer información de la cuenta asociada
        if (transaccion.getCuenta() != null) {
            this.cuentaId = transaccion.getCuenta().getId();
            this.numeroCuenta = transaccion.getCuenta().getNumeroCuenta();
        }
    }

    /**
     * Constructor con parámetros básicos.
     * Útil para crear DTOs desde el frontend.
     * 
     * @param tipo El tipo de transacción como String
     * @param monto El monto de la transacción
     * @param cuentaId El ID de la cuenta
     * @param descripcion La descripción opcional
     */
    public TransaccionDTO(String tipo, BigDecimal monto, Long cuentaId, String descripcion) {
        this.tipo = tipo;
        this.monto = monto;
        this.cuentaId = cuentaId;
        this.descripcion = descripcion;
    }

    /**
     * Método para verificar si la transacción es un depósito.
     * 
     * @return true si es un depósito, false en caso contrario
     */
    public boolean esDeposito() {
        return "DEPOSITO".equals(this.tipo);
    }

    /**
     * Método para verificar si la transacción es un retiro.
     * 
     * @return true si es un retiro, false en caso contrario
     */
    public boolean esRetiro() {
        return "RETIRO".equals(this.tipo);
    }

    /**
     * Método para obtener el tipo de transacción como enum.
     * Útil para conversiones internas en el backend.
     * 
     * @return El enum TipoTransaccion correspondiente
     */
    public Transaccion.TipoTransaccion getTipoEnum() {
        return Transaccion.TipoTransaccion.valueOf(this.tipo);
    }
} 