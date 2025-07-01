package com.ahorros.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una cuenta de ahorros en la base de datos.
 * 
 * Esta clase utiliza anotaciones de JPA para mapear la tabla en la base de datos:
 * - @Entity: Indica que esta clase es una entidad JPA
 * - @Table: Define el nombre de la tabla en la base de datos
 * - @Id: Marca el campo como clave primaria
 * - @GeneratedValue: Configura la generación automática del ID
 * 
 * También utiliza anotaciones de validación para asegurar la integridad de los datos:
 * - @NotBlank: Valida que el campo no esté vacío
 * - @NotNull: Valida que el campo no sea null
 * - @PositiveOrZero: Valida que el valor sea positivo o cero
 * 
 * Lombok se utiliza para generar automáticamente getters, setters, constructores, etc.
 */
@Entity
@Table(name = "cuentas")
@Data                   // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor      // Constructor sin argumentos
@AllArgsConstructor     // Constructor con todos los argumentos
public class Cuenta {

    /**
     * Identificador único de la cuenta (clave primaria).
     * Se genera automáticamente usando estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Número de cuenta único.
     * Debe ser único y no puede estar vacío.
     */
    @Column(unique = true, nullable = false)
    @NotBlank(message = "El número de cuenta es obligatorio")
    private String numeroCuenta;

    /**
     * Nombre del titular de la cuenta.
     * No puede estar vacío.
     */
    @Column(nullable = false)
    @NotBlank(message = "El nombre del titular es obligatorio")
    private String titular;

    /**
     * Saldo actual de la cuenta.
     * Debe ser mayor o igual a cero.
     * Se usa BigDecimal para precisión en cálculos financieros.
     */
    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull(message = "El saldo es obligatorio")
    @PositiveOrZero(message = "El saldo debe ser mayor o igual a cero")
    private BigDecimal saldo;

    /**
     * Estado de la cuenta (activa/inactiva).
     * Por defecto es true (activa).
     */
    @Column(nullable = false)
    private Boolean activa = true;

    /**
     * Fecha y hora de creación de la cuenta.
     * Se establece automáticamente al crear la entidad.
     */
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de la última actualización de la cuenta.
     * Se actualiza automáticamente al modificar la entidad.
     */
    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /**
     * Lista de transacciones asociadas a esta cuenta.
     * Relación uno a muchos: una cuenta puede tener muchas transacciones.
     * mappedBy indica que la relación se mapea desde el campo 'cuenta' en Transaccion.
     * cascade = CascadeType.ALL permite que las operaciones se propaguen a las transacciones.
     * fetch = FetchType.LAZY carga las transacciones solo cuando se soliciten.
     */
    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaccion> transacciones = new ArrayList<>();

    /**
     * Método para agregar una transacción a la cuenta.
     * Mantiene la consistencia de la relación bidireccional.
     * 
     * @param transaccion La transacción a agregar
     */
    public void agregarTransaccion(Transaccion transaccion) {
        transacciones.add(transaccion);
        transaccion.setCuenta(this);
    }

    /**
     * Método para remover una transacción de la cuenta.
     * Mantiene la consistencia de la relación bidireccional.
     * 
     * @param transaccion La transacción a remover
     */
    public void removerTransaccion(Transaccion transaccion) {
        transacciones.remove(transaccion);
        transaccion.setCuenta(null);
    }

    /**
     * Método para realizar un depósito en la cuenta.
     * 
     * @param monto El monto a depositar
     * @throws IllegalArgumentException si el monto es negativo
     */
    public void depositar(BigDecimal monto) {
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del depósito debe ser positivo");
        }
        this.saldo = this.saldo.add(monto);
    }

    /**
     * Método para realizar un retiro de la cuenta.
     * 
     * @param monto El monto a retirar
     * @throws IllegalArgumentException si el monto es negativo o excede el saldo
     */
    public void retirar(BigDecimal monto) {
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del retiro debe ser positivo");
        }
        if (monto.compareTo(this.saldo) > 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar el retiro");
        }
        this.saldo = this.saldo.subtract(monto);
    }
} 