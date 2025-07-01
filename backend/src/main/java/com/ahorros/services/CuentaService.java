package com.ahorros.services;

import com.ahorros.dto.CuentaDTO;
import com.ahorros.models.Cuenta;
import com.ahorros.repositories.CuentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de cuentas de ahorros.
 * 
 * Esta clase contiene toda la lógica de negocio relacionada con las cuentas:
 * - Creación de cuentas
 * - Consulta de cuentas
 * - Actualización de cuentas
 * - Eliminación de cuentas
 * - Validaciones de negocio
 * 
 * Los servicios son la capa intermedia entre los controladores (API) y los repositorios (datos).
 * Aquí es donde se implementa la lógica de negocio y las reglas de validación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CuentaService {

    /**
     * Repositorio de cuentas inyectado por Spring.
     * Se usa @RequiredArgsConstructor de Lombok para crear el constructor automáticamente.
     */
    private final CuentaRepository cuentaRepository;

    /**
     * Crea una nueva cuenta de ahorros.
     * 
     * @param cuentaDTO Los datos de la cuenta a crear
     * @return La cuenta creada como DTO
     * @throws RuntimeException si el número de cuenta ya existe
     */
    public CuentaDTO crearCuenta(CuentaDTO cuentaDTO) {
        log.info("Creando nueva cuenta: {}", cuentaDTO.getNumeroCuenta());
        
        // Validar que el número de cuenta no exista
        if (cuentaRepository.existsByNumeroCuenta(cuentaDTO.getNumeroCuenta())) {
            log.error("El número de cuenta {} ya existe", cuentaDTO.getNumeroCuenta());
            throw new RuntimeException("El número de cuenta ya existe");
        }

        // Validar que el saldo inicial sea válido
        if (cuentaDTO.getSaldo() == null || cuentaDTO.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            log.error("El saldo inicial debe ser mayor o igual a cero");
            throw new RuntimeException("El saldo inicial debe ser mayor o igual a cero");
        }

        // Convertir DTO a entidad
        Cuenta cuenta = cuentaDTO.toEntity();
        
        // Guardar la cuenta en la base de datos
        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        
        log.info("Cuenta creada exitosamente con ID: {}", cuentaGuardada.getId());
        
        // Retornar la cuenta como DTO
        return new CuentaDTO(cuentaGuardada);
    }

    /**
     * Obtiene todas las cuentas.
     * 
     * @return Lista de todas las cuentas como DTOs
     */
    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerTodasLasCuentas() {
        log.info("Obteniendo todas las cuentas");
        
        List<Cuenta> cuentas = cuentaRepository.findAll();
        
        // Convertir entidades a DTOs
        List<CuentaDTO> cuentasDTO = cuentas.stream()
                .map(CuentaDTO::new)
                .collect(Collectors.toList());
        
        log.info("Se encontraron {} cuentas", cuentasDTO.size());
        
        return cuentasDTO;
    }

    /**
     * Obtiene una cuenta por su ID.
     * 
     * @param id El ID de la cuenta
     * @return La cuenta como DTO si existe
     * @throws RuntimeException si la cuenta no existe
     */
    @Transactional(readOnly = true)
    public CuentaDTO obtenerCuentaPorId(Long id) {
        log.info("Buscando cuenta con ID: {}", id);
        
        Optional<Cuenta> cuentaOptional = cuentaRepository.findById(id);
        
        if (cuentaOptional.isEmpty()) {
            log.error("No se encontró la cuenta con ID: {}", id);
            throw new RuntimeException("Cuenta no encontrada");
        }
        
        Cuenta cuenta = cuentaOptional.get();
        log.info("Cuenta encontrada: {}", cuenta.getNumeroCuenta());
        
        return new CuentaDTO(cuenta);
    }

    /**
     * Obtiene una cuenta por su número de cuenta.
     * 
     * @param numeroCuenta El número de cuenta
     * @return La cuenta como DTO si existe
     * @throws RuntimeException si la cuenta no existe
     */
    @Transactional(readOnly = true)
    public CuentaDTO obtenerCuentaPorNumero(String numeroCuenta) {
        log.info("Buscando cuenta con número: {}", numeroCuenta);
        
        Optional<Cuenta> cuentaOptional = cuentaRepository.findByNumeroCuenta(numeroCuenta);
        
        if (cuentaOptional.isEmpty()) {
            log.error("No se encontró la cuenta con número: {}", numeroCuenta);
            throw new RuntimeException("Cuenta no encontrada");
        }
        
        Cuenta cuenta = cuentaOptional.get();
        log.info("Cuenta encontrada con ID: {}", cuenta.getId());
        
        return new CuentaDTO(cuenta);
    }

    /**
     * Busca cuentas por nombre del titular.
     * 
     * @param titular El nombre del titular (parcial)
     * @return Lista de cuentas que coinciden con el titular
     */
    @Transactional(readOnly = true)
    public List<CuentaDTO> buscarCuentasPorTitular(String titular) {
        log.info("Buscando cuentas por titular: {}", titular);
        
        List<Cuenta> cuentas = cuentaRepository.findByTitularContainingIgnoreCase(titular);
        
        List<CuentaDTO> cuentasDTO = cuentas.stream()
                .map(CuentaDTO::new)
                .collect(Collectors.toList());
        
        log.info("Se encontraron {} cuentas para el titular: {}", cuentasDTO.size(), titular);
        
        return cuentasDTO;
    }

    /**
     * Obtiene todas las cuentas activas.
     * 
     * @return Lista de cuentas activas como DTOs
     */
    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerCuentasActivas() {
        log.info("Obteniendo cuentas activas");
        
        List<Cuenta> cuentas = cuentaRepository.findByActivaTrue();
        
        List<CuentaDTO> cuentasDTO = cuentas.stream()
                .map(CuentaDTO::new)
                .collect(Collectors.toList());
        
        log.info("Se encontraron {} cuentas activas", cuentasDTO.size());
        
        return cuentasDTO;
    }

    /**
     * Actualiza una cuenta existente.
     * 
     * @param id El ID de la cuenta a actualizar
     * @param cuentaDTO Los nuevos datos de la cuenta
     * @return La cuenta actualizada como DTO
     * @throws RuntimeException si la cuenta no existe
     */
    public CuentaDTO actualizarCuenta(Long id, CuentaDTO cuentaDTO) {
        log.info("Actualizando cuenta con ID: {}", id);
        
        // Verificar que la cuenta existe
        Optional<Cuenta> cuentaOptional = cuentaRepository.findById(id);
        
        if (cuentaOptional.isEmpty()) {
            log.error("No se encontró la cuenta con ID: {}", id);
            throw new RuntimeException("Cuenta no encontrada");
        }
        
        Cuenta cuentaExistente = cuentaOptional.get();
        
        // Actualizar solo los campos permitidos
        if (cuentaDTO.getTitular() != null) {
            cuentaExistente.setTitular(cuentaDTO.getTitular());
        }
        
        if (cuentaDTO.getActiva() != null) {
            cuentaExistente.setActiva(cuentaDTO.getActiva());
        }
        
        // No permitir actualizar el número de cuenta ni el saldo desde aquí
        // El saldo se actualiza solo a través de transacciones
        
        Cuenta cuentaActualizada = cuentaRepository.save(cuentaExistente);
        
        log.info("Cuenta actualizada exitosamente: {}", cuentaActualizada.getNumeroCuenta());
        
        return new CuentaDTO(cuentaActualizada);
    }

    /**
     * Elimina una cuenta.
     * 
     * @param id El ID de la cuenta a eliminar
     * @throws RuntimeException si la cuenta no existe
     */
    public void eliminarCuenta(Long id) {
        log.info("Eliminando cuenta con ID: {}", id);
        
        // Verificar que la cuenta existe
        if (!cuentaRepository.existsById(id)) {
            log.error("No se encontró la cuenta con ID: {}", id);
            throw new RuntimeException("Cuenta no encontrada");
        }
        
        cuentaRepository.deleteById(id);
        
        log.info("Cuenta eliminada exitosamente");
    }

    /**
     * Obtiene estadísticas de las cuentas.
     * 
     * @return Array con estadísticas [total cuentas, cuentas activas, saldo total]
     */
    @Transactional(readOnly = true)
    public Object[] obtenerEstadisticas() {
        log.info("Obteniendo estadísticas de cuentas");
        
        long totalCuentas = cuentaRepository.count();
        long cuentasActivas = cuentaRepository.countByActivaTrue();
        BigDecimal saldoTotal = cuentaRepository.getSaldoTotal();
        
        Object[] estadisticas = {totalCuentas, cuentasActivas, saldoTotal};
        
        log.info("Estadísticas obtenidas: {} cuentas totales, {} activas, saldo total: {}", 
                totalCuentas, cuentasActivas, saldoTotal);
        
        return estadisticas;
    }

    /**
     * Obtiene cuentas ordenadas por saldo (mayor a menor).
     * 
     * @return Lista de cuentas ordenadas por saldo
     */
    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerCuentasOrdenadasPorSaldo() {
        log.info("Obteniendo cuentas ordenadas por saldo");
        
        List<Cuenta> cuentas = cuentaRepository.findAllByOrderBySaldoDesc();
        
        List<CuentaDTO> cuentasDTO = cuentas.stream()
                .map(CuentaDTO::new)
                .collect(Collectors.toList());
        
        return cuentasDTO;
    }

    /**
     * Obtiene cuentas con saldo superior al promedio.
     * 
     * @return Lista de cuentas con saldo superior al promedio
     */
    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerCuentasConSaldoSuperiorAlPromedio() {
        log.info("Obteniendo cuentas con saldo superior al promedio");
        
        List<Cuenta> cuentas = cuentaRepository.findCuentasConSaldoSuperiorAlPromedio();
        
        List<CuentaDTO> cuentasDTO = cuentas.stream()
                .map(CuentaDTO::new)
                .collect(Collectors.toList());
        
        log.info("Se encontraron {} cuentas con saldo superior al promedio", cuentasDTO.size());
        
        return cuentasDTO;
    }

    /**
     * Verifica si una cuenta existe.
     * 
     * @param id El ID de la cuenta
     * @return true si existe, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean existeCuenta(Long id) {
        return cuentaRepository.existsById(id);
    }

    /**
     * Verifica si un número de cuenta existe.
     * 
     * @param numeroCuenta El número de cuenta
     * @return true si existe, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean existeNumeroCuenta(String numeroCuenta) {
        return cuentaRepository.existsByNumeroCuenta(numeroCuenta);
    }
} 