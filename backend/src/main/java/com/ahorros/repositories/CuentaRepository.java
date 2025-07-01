package com.ahorros.repositories;

import com.ahorros.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Cuenta.
 * 
 * Esta interfaz extiende JpaRepository, que proporciona automáticamente
 * métodos CRUD básicos (Create, Read, Update, Delete) para la entidad Cuenta.
 * 
 * Spring Data JPA implementa automáticamente esta interfaz, por lo que no
 * necesitamos escribir código de implementación para los métodos básicos.
 * 
 * También podemos definir métodos personalizados que Spring Data JPA
 * implementará automáticamente basándose en el nombre del método.
 */
@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    /**
     * Busca una cuenta por su número de cuenta.
     * 
     * Spring Data JPA implementa automáticamente este método basándose en
     * el nombre del campo (numeroCuenta) y el tipo de retorno (Optional<Cuenta>).
     * 
     * @param numeroCuenta El número de cuenta a buscar
     * @return Optional que contiene la cuenta si se encuentra, vacío en caso contrario
     */
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

    /**
     * Busca cuentas por el nombre del titular.
     * 
     * @param titular El nombre del titular a buscar
     * @return Lista de cuentas que coinciden con el titular
     */
    List<Cuenta> findByTitularContainingIgnoreCase(String titular);

    /**
     * Busca cuentas activas.
     * 
     * @return Lista de todas las cuentas activas
     */
    List<Cuenta> findByActivaTrue();

    /**
     * Busca cuentas inactivas.
     * 
     * @return Lista de todas las cuentas inactivas
     */
    List<Cuenta> findByActivaFalse();

    /**
     * Busca cuentas con saldo mayor que el especificado.
     * 
     * @param saldoMinimo El saldo mínimo requerido
     * @return Lista de cuentas con saldo mayor al especificado
     */
    List<Cuenta> findBySaldoGreaterThan(BigDecimal saldoMinimo);

    /**
     * Busca cuentas con saldo menor que el especificado.
     * 
     * @param saldoMaximo El saldo máximo permitido
     * @return Lista de cuentas con saldo menor al especificado
     */
    List<Cuenta> findBySaldoLessThan(BigDecimal saldoMaximo);

    /**
     * Busca cuentas con saldo entre dos valores.
     * 
     * @param saldoMinimo El saldo mínimo
     * @param saldoMaximo El saldo máximo
     * @return Lista de cuentas con saldo en el rango especificado
     */
    List<Cuenta> findBySaldoBetween(BigDecimal saldoMinimo, BigDecimal saldoMaximo);

    /**
     * Verifica si existe una cuenta con el número de cuenta especificado.
     * 
     * @param numeroCuenta El número de cuenta a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByNumeroCuenta(String numeroCuenta);

    /**
     * Cuenta el número de cuentas activas.
     * 
     * @return El número total de cuentas activas
     */
    long countByActivaTrue();

    /**
     * Cuenta el número de cuentas inactivas.
     * 
     * @return El número total de cuentas inactivas
     */
    long countByActivaFalse();

    /**
     * Busca cuentas ordenadas por saldo de forma descendente.
     * 
     * @return Lista de cuentas ordenadas por saldo (mayor a menor)
     */
    List<Cuenta> findAllByOrderBySaldoDesc();

    /**
     * Busca cuentas ordenadas por fecha de creación de forma descendente.
     * 
     * @return Lista de cuentas ordenadas por fecha de creación (más recientes primero)
     */
    List<Cuenta> findAllByOrderByFechaCreacionDesc();

    /**
     * Consulta personalizada usando JPQL (Java Persistence Query Language).
     * Busca cuentas con saldo mayor al promedio de todas las cuentas.
     * 
     * @return Lista de cuentas con saldo superior al promedio
     */
    @Query("SELECT c FROM Cuenta c WHERE c.saldo > (SELECT AVG(c2.saldo) FROM Cuenta c2)")
    List<Cuenta> findCuentasConSaldoSuperiorAlPromedio();

    /**
     * Consulta personalizada con parámetros.
     * Busca cuentas por titular y saldo mínimo.
     * 
     * @param titular El nombre del titular (parcial)
     * @param saldoMinimo El saldo mínimo requerido
     * @return Lista de cuentas que cumplen ambos criterios
     */
    @Query("SELECT c FROM Cuenta c WHERE c.titular LIKE %:titular% AND c.saldo >= :saldoMinimo")
    List<Cuenta> findByTitularAndSaldoMinimo(@Param("titular") String titular, @Param("saldoMinimo") BigDecimal saldoMinimo);

    /**
     * Consulta personalizada para obtener estadísticas de saldo.
     * 
     * @return Array con [saldo mínimo, saldo máximo, saldo promedio, total de cuentas]
     */
    @Query("SELECT MIN(c.saldo), MAX(c.saldo), AVG(c.saldo), COUNT(c) FROM Cuenta c")
    Object[] getEstadisticasSaldo();

    /**
     * Consulta personalizada para obtener el saldo total de todas las cuentas.
     * 
     * @return El saldo total de todas las cuentas
     */
    @Query("SELECT SUM(c.saldo) FROM Cuenta c")
    BigDecimal getSaldoTotal();

    /**
     * Consulta personalizada para obtener el saldo total de cuentas activas.
     * 
     * @return El saldo total de las cuentas activas
     */
    @Query("SELECT SUM(c.saldo) FROM Cuenta c WHERE c.activa = true")
    BigDecimal getSaldoTotalActivas();
} 