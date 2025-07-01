package com.ahorros.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la entidad Cuenta.
 * 
 * Los DTOs se utilizan para transferir datos entre el frontend y el backend.
 * A diferencia de las entidades JPA, los DTOs:
 * - No están mapeados a la base de datos
 * - Pueden tener una estructura diferente a las entidades
 * - Se usan para ocultar información sensible o innecesaria
 * - Facilitan la serialización/deserialización JSON
 * 
 * En este caso, el DTO incluye solo los campos necesarios para la comunicación
 * con el frontend, sin incluir las relaciones complejas como las transacciones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaDTO {

    /**
     * Identificador único de la cuenta.
     * Se envía al frontend para identificar la cuenta.
     */
    private Long id;

    /**
     * Número de cuenta único.
     * Identificador público de la cuenta.
     */
    private String numeroCuenta;

    /**
     * Nombre del titular de la cuenta.
     * Información del propietario de la cuenta.
     */
    private String titular;

    /**
     * Saldo actual de la cuenta.
     * Información financiera importante para el usuario.
     */
    private BigDecimal saldo;

    /**
     * Estado de la cuenta (activa/inactiva).
     * Indica si la cuenta está disponible para transacciones.
     */
    private Boolean activa;

    /**
     * Fecha de creación de la cuenta.
     * Información temporal para auditoría.
     */
    private LocalDateTime fechaCreacion;

    /**
     * Fecha de la última actualización de la cuenta.
     * Información temporal para auditoría.
     */
    private LocalDateTime fechaActualizacion;

    /**
     * Constructor que crea un DTO a partir de una entidad Cuenta.
     * Este método facilita la conversión de entidad a DTO.
     * 
     * @param cuenta La entidad Cuenta de la cual crear el DTO
     */
    public CuentaDTO(com.ahorros.models.Cuenta cuenta) {
        this.id = cuenta.getId();
        this.numeroCuenta = cuenta.getNumeroCuenta();
        this.titular = cuenta.getTitular();
        this.saldo = cuenta.getSaldo();
        this.activa = cuenta.getActiva();
        this.fechaCreacion = cuenta.getFechaCreacion();
        this.fechaActualizacion = cuenta.getFechaActualizacion();
    }

    /**
     * Método para crear una entidad Cuenta a partir del DTO.
     * Útil para convertir datos del frontend a entidad para persistir.
     * 
     * @return Una nueva instancia de Cuenta con los datos del DTO
     */
    public com.ahorros.models.Cuenta toEntity() {
        com.ahorros.models.Cuenta cuenta = new com.ahorros.models.Cuenta();
        cuenta.setNumeroCuenta(this.numeroCuenta);
        cuenta.setTitular(this.titular);
        cuenta.setSaldo(this.saldo != null ? this.saldo : BigDecimal.ZERO);
        cuenta.setActiva(this.activa != null ? this.activa : true);
        return cuenta;
    }
} 