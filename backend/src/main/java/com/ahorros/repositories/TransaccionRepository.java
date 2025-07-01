package com.ahorros.repositories;

import com.ahorros.models.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Transaccion.
 * 
 * Esta interfaz extiende JpaRepository para proporcionar operaciones CRUD
 * básicas para la entidad Transaccion, así como métodos personalizados
 * para consultas específicas del negocio.
 */
@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    /**
     * Busca todas las transacciones de una cuenta específica.
     * 
     * @param cuentaId El ID de la cuenta
     * @return Lista de transacciones de la cuenta
     */
    List<Transaccion> findByCuentaId(Long cuentaId);

    /**
     * Busca transacciones de una cuenta ordenadas por fecha (más recientes primero).
     * 
     * @param cuentaId El ID de la cuenta
     * @return Lista de transacciones ordenadas por fecha descendente
     */
    List<Transaccion> findByCuentaIdOrderByFechaTransaccionDesc(Long cuentaId);

    /**
     * Busca transacciones por tipo (DEPOSITO o RETIRO).
     * 
     * @param tipo El tipo de transacción
     * @return Lista de transacciones del tipo especificado
     */
    List<Transaccion> findByTipo(Transaccion.TipoTransaccion tipo);

    /**
     * Busca transacciones de una cuenta por tipo.
     * 
     * @param cuentaId El ID de la cuenta
     * @param tipo El tipo de transacción
     * @return Lista de transacciones de la cuenta del tipo especificado
     */
    List<Transaccion> findByCuentaIdAndTipo(Long cuentaId, Transaccion.TipoTransaccion tipo);

    /**
     * Busca transacciones con monto mayor al especificado.
     * 
     * @param montoMinimo El monto mínimo
     * @return Lista de transacciones con monto mayor al especificado
     */
    List<Transaccion> findByMontoGreaterThan(BigDecimal montoMinimo);

    /**
     * Busca transacciones con monto menor al especificado.
     * 
     * @param montoMaximo El monto máximo
     * @return Lista de transacciones con monto menor al especificado
     */
    List<Transaccion> findByMontoLessThan(BigDecimal montoMaximo);

    /**
     * Busca transacciones con monto entre dos valores.
     * 
     * @param montoMinimo El monto mínimo
     * @param montoMaximo El monto máximo
     * @return Lista de transacciones con monto en el rango especificado
     */
    List<Transaccion> findByMontoBetween(BigDecimal montoMinimo, BigDecimal montoMaximo);

    /**
     * Busca transacciones realizadas en una fecha específica.
     * 
     * @param fecha La fecha de las transacciones
     * @return Lista de transacciones de la fecha especificada
     */
    @Query("SELECT t FROM Transaccion t WHERE DATE(t.fechaTransaccion) = DATE(:fecha)")
    List<Transaccion> findByFechaTransaccion(@Param("fecha") LocalDateTime fecha);

    /**
     * Busca transacciones realizadas entre dos fechas.
     * 
     * @param fechaInicio La fecha de inicio
     * @param fechaFin La fecha de fin
     * @return Lista de transacciones en el rango de fechas
     */
    @Query("SELECT t FROM Transaccion t WHERE t.fechaTransaccion BETWEEN :fechaInicio AND :fechaFin")
    List<Transaccion> findByFechaTransaccionBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                                   @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca transacciones de una cuenta entre dos fechas.
     * 
     * @param cuentaId El ID de la cuenta
     * @param fechaInicio La fecha de inicio
     * @param fechaFin La fecha de fin
     * @return Lista de transacciones de la cuenta en el rango de fechas
     */
    @Query("SELECT t FROM Transaccion t WHERE t.cuenta.id = :cuentaId AND t.fechaTransaccion BETWEEN :fechaInicio AND :fechaFin")
    List<Transaccion> findByCuentaIdAndFechaTransaccionBetween(@Param("cuentaId") Long cuentaId,
                                                              @Param("fechaInicio") LocalDateTime fechaInicio,
                                                              @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca transacciones que contengan una descripción específica.
     * 
     * @param descripcion La descripción a buscar (parcial)
     * @return Lista de transacciones que contengan la descripción
     */
    List<Transaccion> findByDescripcionContainingIgnoreCase(String descripcion);

    /**
     * Cuenta el número de transacciones de una cuenta.
     * 
     * @param cuentaId El ID de la cuenta
     * @return El número total de transacciones de la cuenta
     */
    long countByCuentaId(Long cuentaId);

    /**
     * Cuenta el número de transacciones por tipo.
     * 
     * @param tipo El tipo de transacción
     * @return El número total de transacciones del tipo especificado
     */
    long countByTipo(Transaccion.TipoTransaccion tipo);

    /**
     * Cuenta el número de transacciones de una cuenta por tipo.
     * 
     * @param cuentaId El ID de la cuenta
     * @param tipo El tipo de transacción
     * @return El número total de transacciones de la cuenta del tipo especificado
     */
    long countByCuentaIdAndTipo(Long cuentaId, Transaccion.TipoTransaccion tipo);

    /**
     * Obtiene el monto total de depósitos de una cuenta.
     * 
     * @param cuentaId El ID de la cuenta
     * @return El monto total de depósitos
     */
    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t WHERE t.cuenta.id = :cuentaId AND t.tipo = 'DEPOSITO'")
    BigDecimal getTotalDepositosByCuentaId(@Param("cuentaId") Long cuentaId);

    /**
     * Obtiene el monto total de retiros de una cuenta.
     * 
     * @param cuentaId El ID de la cuenta
     * @return El monto total de retiros
     */
    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t WHERE t.cuenta.id = :cuentaId AND t.tipo = 'RETIRO'")
    BigDecimal getTotalRetirosByCuentaId(@Param("cuentaId") Long cuentaId);

    /**
     * Obtiene el monto total de depósitos de todas las cuentas.
     * 
     * @return El monto total de depósitos
     */
    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t WHERE t.tipo = 'DEPOSITO'")
    BigDecimal getTotalDepositos();

    /**
     * Obtiene el monto total de retiros de todas las cuentas.
     * 
     * @return El monto total de retiros
     */
    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM Transaccion t WHERE t.tipo = 'RETIRO'")
    BigDecimal getTotalRetiros();

    /**
     * Obtiene las transacciones más recientes de todas las cuentas.
     * 
     * @param limit El número máximo de transacciones a retornar
     * @return Lista de las transacciones más recientes
     */
    @Query("SELECT t FROM Transaccion t ORDER BY t.fechaTransaccion DESC")
    List<Transaccion> findTopTransaccionesRecientes(@Param("limit") int limit);

    /**
     * Obtiene las transacciones de mayor monto.
     * 
     * @param limit El número máximo de transacciones a retornar
     * @return Lista de las transacciones de mayor monto
     */
    @Query("SELECT t FROM Transaccion t ORDER BY t.monto DESC")
    List<Transaccion> findTopTransaccionesPorMonto(@Param("limit") int limit);

    /**
     * Obtiene estadísticas de transacciones por cuenta.
     * 
     * @param cuentaId El ID de la cuenta
     * @return Array con [total depósitos, total retiros, número de transacciones]
     */
    @Query("SELECT " +
           "COALESCE(SUM(CASE WHEN t.tipo = 'DEPOSITO' THEN t.monto ELSE 0 END), 0), " +
           "COALESCE(SUM(CASE WHEN t.tipo = 'RETIRO' THEN t.monto ELSE 0 END), 0), " +
           "COUNT(t) " +
           "FROM Transaccion t WHERE t.cuenta.id = :cuentaId")
    Object[] getEstadisticasTransaccionesByCuentaId(@Param("cuentaId") Long cuentaId);
} 