package com.ahorros.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa una transacción en la base de datos.
 * 
 * Una transacción puede ser de dos tipos:
 * - DEPOSITO: Cuando se agrega dinero a la cuenta
 * - RETIRO: Cuando se retira dinero de la cuenta
 * 
 * Cada transacción está asociada a una cuenta específica y registra:
 * - El tipo de transacción
 * - El monto
 * - La fecha y hora
 * - El saldo resultante después de la transacción
 */
@Entity
@Table(name = "transacciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {

    /**
     * Identificador único de la transacción (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tipo de transacción (DEPOSITO o RETIRO).
     * Se usa un enum para garantizar valores válidos.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El tipo de transacción es obligatorio")
    private TipoTransaccion tipo;

    /**
     * Monto de la transacción.
     * Debe ser positivo.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal monto;

    /**
     * Saldo de la cuenta después de realizar la transacción.
     * Se calcula automáticamente al procesar la transacción.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull(message = "El saldo resultante es obligatorio")
    private BigDecimal saldoResultante;

    /**
     * Descripción opcional de la transacción.
     * Puede ser null.
     */
    @Column(length = 500)
    private String descripcion;

    /**
     * Fecha y hora de la transacción.
     * Se establece automáticamente al crear la transacción.
     */
    @CreationTimestamp
    @Column(name = "fecha_transaccion", nullable = false, updatable = false)
    private LocalDateTime fechaTransaccion;

    /**
     * Cuenta asociada a esta transacción.
     * Relación muchos a uno: muchas transacciones pueden pertenecer a una cuenta.
     * fetch = FetchType.LAZY carga la cuenta solo cuando se solicite.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    @NotNull(message = "La cuenta es obligatoria")
    private Cuenta cuenta;

    /**
     * Enum que define los tipos de transacción posibles.
     * Esto garantiza que solo se puedan usar valores válidos.
     */
    public enum TipoTransaccion {
        /**
         * Transacción de depósito (agregar dinero a la cuenta).
         */
        DEPOSITO("Depósito"),
        
        /**
         * Transacción de retiro (retirar dinero de la cuenta).
         */
        RETIRO("Retiro");

        private final String descripcion;

        TipoTransaccion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    /**
     * Constructor con parámetros básicos.
     * Útil para crear transacciones sin necesidad de especificar todos los campos.
     * 
     * @param tipo El tipo de transacción
     * @param monto El monto de la transacción
     * @param cuenta La cuenta asociada
     */
    public Transaccion(TipoTransaccion tipo, BigDecimal monto, Cuenta cuenta) {
        this.tipo = tipo;
        this.monto = monto;
        this.cuenta = cuenta;
        this.fechaTransaccion = LocalDateTime.now();
    }

    /**
     * Constructor con parámetros básicos y descripción.
     * 
     * @param tipo El tipo de transacción
     * @param monto El monto de la transacción
     * @param cuenta La cuenta asociada
     * @param descripcion La descripción de la transacción
     */
    public Transaccion(TipoTransaccion tipo, BigDecimal monto, Cuenta cuenta, String descripcion) {
        this(tipo, monto, cuenta);
        this.descripcion = descripcion;
    }

    /**
     * Método para calcular el saldo resultante después de la transacción.
     * Se llama automáticamente al procesar la transacción.
     */
    public void calcularSaldoResultante() {
        if (tipo == TipoTransaccion.DEPOSITO) {
            this.saldoResultante = cuenta.getSaldo().add(monto);
        } else if (tipo == TipoTransaccion.RETIRO) {
            this.saldoResultante = cuenta.getSaldo().subtract(monto);
        }
    }

    /**
     * Método para obtener una representación en texto del tipo de transacción.
     * 
     * @return La descripción del tipo de transacción
     */
    public String getTipoDescripcion() {
        return tipo.getDescripcion();
    }

    /**
     * Método para verificar si la transacción es un depósito.
     * 
     * @return true si es un depósito, false en caso contrario
     */
    public boolean esDeposito() {
        return tipo == TipoTransaccion.DEPOSITO;
    }

    /**
     * Método para verificar si la transacción es un retiro.
     * 
     * @return true si es un retiro, false en caso contrario
     */
    public boolean esRetiro() {
        return tipo == TipoTransaccion.RETIRO;
    }
} 