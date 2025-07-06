package com.ahorros.services;

import com.ahorros.dto.TransaccionDTO;
import com.ahorros.models.Cuenta;
import com.ahorros.models.Transaccion;
import com.ahorros.repositories.CuentaRepository;
import com.ahorros.repositories.TransaccionRepository;
import com.ahorros.services.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de transacciones de cuentas de ahorros.
 * 
 * Esta clase contiene toda la lógica de negocio relacionada con las transacciones:
 * - Realizar depósitos
 * - Realizar retiros
 * - Consultar historial de transacciones
 * - Validaciones de negocio
 * - Actualización automática de saldos
 * 
 * Las transacciones son operaciones críticas que afectan el saldo de las cuentas,
 * por lo que se implementan con transacciones de base de datos para garantizar
 * la consistencia de los datos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransaccionService {

    /**
     * Repositorio de transacciones inyectado por Spring.
     */
    private final TransaccionRepository transaccionRepository;

    /**
     * Repositorio de cuentas inyectado por Spring.
     */
    private final CuentaRepository cuentaRepository;
    
    /**
     * Servicio de notificaciones inyectado por Spring.
     */
    private final NotificacionService notificacionService;

    /**
     * Realiza un depósito en una cuenta.
     * 
     * @param transaccionDTO Los datos de la transacción de depósito
     * @return La transacción creada como DTO
     * @throws RuntimeException si la cuenta no existe o el monto es inválido
     */
    public TransaccionDTO realizarDeposito(TransaccionDTO transaccionDTO) {
        log.info("Realizando depósito de {} en cuenta ID: {}", 
                transaccionDTO.getMonto(), transaccionDTO.getCuentaId());

        // Validar que la cuenta existe
        Optional<Cuenta> cuentaOptional = cuentaRepository.findById(transaccionDTO.getCuentaId());
        if (cuentaOptional.isEmpty()) {
            log.error("No se encontró la cuenta con ID: {}", transaccionDTO.getCuentaId());
            throw new RuntimeException("Cuenta no encontrada");
        }

        Cuenta cuenta = cuentaOptional.get();

        // Validar que la cuenta esté activa
        if (!cuenta.getActiva()) {
            log.error("La cuenta {} está inactiva", cuenta.getNumeroCuenta());
            throw new RuntimeException("La cuenta está inactiva");
        }

        // Validar que el monto sea positivo
        if (transaccionDTO.getMonto() == null || transaccionDTO.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("El monto del depósito debe ser positivo");
            throw new RuntimeException("El monto del depósito debe ser positivo");
        }

        // Crear la transacción
        Transaccion transaccion = new Transaccion(
                Transaccion.TipoTransaccion.DEPOSITO,
                transaccionDTO.getMonto(),
                cuenta,
                transaccionDTO.getDescripcion()
        );

        // Realizar el depósito en la cuenta
        cuenta.depositar(transaccionDTO.getMonto());

        // Calcular el saldo resultante
        transaccion.calcularSaldoResultante();

        // Guardar la transacción
        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);

        // Guardar la cuenta actualizada
        cuentaRepository.save(cuenta);

        // Crear notificación si la cuenta tiene usuario asociado
        if (cuenta.getUsuario() != null) {
            try {
                notificacionService.crearNotificacionTransaccion(
                    cuenta.getUsuario().getId(),
                    "DEPOSITO",
                    transaccionDTO.getMonto().toString(),
                    cuenta.getNumeroCuenta()
                );
            } catch (Exception e) {
                log.warn("No se pudo crear la notificación para el depósito: {}", e.getMessage());
            }
        }

        log.info("Depósito realizado exitosamente. Nuevo saldo: {}", cuenta.getSaldo());

        return new TransaccionDTO(transaccionGuardada);
    }

    /**
     * Realiza un retiro de una cuenta.
     * 
     * @param transaccionDTO Los datos de la transacción de retiro
     * @return La transacción creada como DTO
     * @throws RuntimeException si la cuenta no existe, el monto es inválido o saldo insuficiente
     */
    public TransaccionDTO realizarRetiro(TransaccionDTO transaccionDTO) {
        log.info("Realizando retiro de {} de cuenta ID: {}", 
                transaccionDTO.getMonto(), transaccionDTO.getCuentaId());

        // Validar que la cuenta existe
        Optional<Cuenta> cuentaOptional = cuentaRepository.findById(transaccionDTO.getCuentaId());
        if (cuentaOptional.isEmpty()) {
            log.error("No se encontró la cuenta con ID: {}", transaccionDTO.getCuentaId());
            throw new RuntimeException("Cuenta no encontrada");
        }

        Cuenta cuenta = cuentaOptional.get();

        // Validar que la cuenta esté activa
        if (!cuenta.getActiva()) {
            log.error("La cuenta {} está inactiva", cuenta.getNumeroCuenta());
            throw new RuntimeException("La cuenta está inactiva");
        }

        // Validar que el monto sea positivo
        if (transaccionDTO.getMonto() == null || transaccionDTO.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("El monto del retiro debe ser positivo");
            throw new RuntimeException("El monto del retiro debe ser positivo");
        }

        // Validar que haya saldo suficiente
        if (transaccionDTO.getMonto().compareTo(cuenta.getSaldo()) > 0) {
            log.error("Saldo insuficiente. Saldo actual: {}, monto solicitado: {}", 
                    cuenta.getSaldo(), transaccionDTO.getMonto());
            throw new RuntimeException("Saldo insuficiente para realizar el retiro");
        }

        // Crear la transacción
        Transaccion transaccion = new Transaccion(
                Transaccion.TipoTransaccion.RETIRO,
                transaccionDTO.getMonto(),
                cuenta,
                transaccionDTO.getDescripcion()
        );

        // Realizar el retiro de la cuenta
        cuenta.retirar(transaccionDTO.getMonto());

        // Calcular el saldo resultante
        transaccion.calcularSaldoResultante();

        // Guardar la transacción
        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);

        // Guardar la cuenta actualizada
        cuentaRepository.save(cuenta);

        // Crear notificación si la cuenta tiene usuario asociado
        if (cuenta.getUsuario() != null) {
            try {
                notificacionService.crearNotificacionTransaccion(
                    cuenta.getUsuario().getId(),
                    "RETIRO",
                    transaccionDTO.getMonto().toString(),
                    cuenta.getNumeroCuenta()
                );
                
                // Crear notificación de saldo bajo si el saldo es menor a 100
                if (cuenta.getSaldo().compareTo(new BigDecimal("100")) < 0) {
                    notificacionService.crearNotificacionSaldoBajo(
                        cuenta.getUsuario().getId(),
                        cuenta.getNumeroCuenta(),
                        cuenta.getSaldo().toString()
                    );
                }
            } catch (Exception e) {
                log.warn("No se pudo crear la notificación para el retiro: {}", e.getMessage());
            }
        }

        log.info("Retiro realizado exitosamente. Nuevo saldo: {}", cuenta.getSaldo());

        return new TransaccionDTO(transaccionGuardada);
    }

    /**
     * Obtiene todas las transacciones.
     * 
     * @return Lista de todas las transacciones como DTOs
     */
    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerTodasLasTransacciones() {
        log.info("Obteniendo todas las transacciones");

        List<Transaccion> transacciones = transaccionRepository.findAll();

        List<TransaccionDTO> transaccionesDTO = transacciones.stream()
                .map(TransaccionDTO::new)
                .collect(Collectors.toList());

        log.info("Se encontraron {} transacciones", transaccionesDTO.size());

        return transaccionesDTO;
    }

    /**
     * Obtiene las transacciones de una cuenta específica.
     * 
     * @param cuentaId El ID de la cuenta
     * @return Lista de transacciones de la cuenta como DTOs
     * @throws RuntimeException si la cuenta no existe
     */
    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerTransaccionesPorCuenta(Long cuentaId) {
        log.info("Obteniendo transacciones de cuenta ID: {}", cuentaId);

        // Validar que la cuenta existe
        if (!cuentaRepository.existsById(cuentaId)) {
            log.error("No se encontró la cuenta con ID: {}", cuentaId);
            throw new RuntimeException("Cuenta no encontrada");
        }

        List<Transaccion> transacciones = transaccionRepository.findByCuentaIdOrderByFechaTransaccionDesc(cuentaId);

        List<TransaccionDTO> transaccionesDTO = transacciones.stream()
                .map(TransaccionDTO::new)
                .collect(Collectors.toList());

        log.info("Se encontraron {} transacciones para la cuenta ID: {}", transaccionesDTO.size(), cuentaId);

        return transaccionesDTO;
    }

    /**
     * Obtiene una transacción por su ID.
     * 
     * @param id El ID de la transacción
     * @return La transacción como DTO si existe
     * @throws RuntimeException si la transacción no existe
     */
    @Transactional(readOnly = true)
    public TransaccionDTO obtenerTransaccionPorId(Long id) {
        log.info("Buscando transacción con ID: {}", id);

        Optional<Transaccion> transaccionOptional = transaccionRepository.findById(id);

        if (transaccionOptional.isEmpty()) {
            log.error("No se encontró la transacción con ID: {}", id);
            throw new RuntimeException("Transacción no encontrada");
        }

        Transaccion transaccion = transaccionOptional.get();
        log.info("Transacción encontrada: {} - {}", transaccion.getTipo(), transaccion.getMonto());

        return new TransaccionDTO(transaccion);
    }

    /**
     * Obtiene transacciones por tipo.
     * 
     * @param tipo El tipo de transacción (DEPOSITO o RETIRO)
     * @return Lista de transacciones del tipo especificado
     */
    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerTransaccionesPorTipo(String tipo) {
        log.info("Obteniendo transacciones de tipo: {}", tipo);

        Transaccion.TipoTransaccion tipoEnum = Transaccion.TipoTransaccion.valueOf(tipo.toUpperCase());
        List<Transaccion> transacciones = transaccionRepository.findByTipo(tipoEnum);

        List<TransaccionDTO> transaccionesDTO = transacciones.stream()
                .map(TransaccionDTO::new)
                .collect(Collectors.toList());

        log.info("Se encontraron {} transacciones de tipo {}", transaccionesDTO.size(), tipo);

        return transaccionesDTO;
    }

    /**
     * Obtiene transacciones de una cuenta por tipo.
     * 
     * @param cuentaId El ID de la cuenta
     * @param tipo El tipo de transacción
     * @return Lista de transacciones de la cuenta del tipo especificado
     */
    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerTransaccionesPorCuentaYTipo(Long cuentaId, String tipo) {
        log.info("Obteniendo transacciones de cuenta ID: {} y tipo: {}", cuentaId, tipo);

        // Validar que la cuenta existe
        if (!cuentaRepository.existsById(cuentaId)) {
            log.error("No se encontró la cuenta con ID: {}", cuentaId);
            throw new RuntimeException("Cuenta no encontrada");
        }

        Transaccion.TipoTransaccion tipoEnum = Transaccion.TipoTransaccion.valueOf(tipo.toUpperCase());
        List<Transaccion> transacciones = transaccionRepository.findByCuentaIdAndTipo(cuentaId, tipoEnum);

        List<TransaccionDTO> transaccionesDTO = transacciones.stream()
                .map(TransaccionDTO::new)
                .collect(Collectors.toList());

        log.info("Se encontraron {} transacciones de tipo {} para la cuenta ID: {}", 
                transaccionesDTO.size(), tipo, cuentaId);

        return transaccionesDTO;
    }

    /**
     * Obtiene estadísticas de transacciones de una cuenta.
     * 
     * @param cuentaId El ID de la cuenta
     * @return Array con estadísticas [total depósitos, total retiros, número de transacciones]
     */
    @Transactional(readOnly = true)
    public Object[] obtenerEstadisticasTransacciones(Long cuentaId) {
        log.info("Obteniendo estadísticas de transacciones para cuenta ID: {}", cuentaId);

        // Validar que la cuenta existe
        if (!cuentaRepository.existsById(cuentaId)) {
            log.error("No se encontró la cuenta con ID: {}", cuentaId);
            throw new RuntimeException("Cuenta no encontrada");
        }

        Object[] estadisticas = transaccionRepository.getEstadisticasTransaccionesByCuentaId(cuentaId);

        log.info("Estadísticas obtenidas para cuenta ID {}: depósitos: {}, retiros: {}, total transacciones: {}", 
                cuentaId, estadisticas[0], estadisticas[1], estadisticas[2]);

        return estadisticas;
    }

    /**
     * Obtiene el total de depósitos de una cuenta.
     * 
     * @param cuentaId El ID de la cuenta
     * @return El total de depósitos
     */
    @Transactional(readOnly = true)
    public BigDecimal obtenerTotalDepositos(Long cuentaId) {
        log.info("Obteniendo total de depósitos para cuenta ID: {}", cuentaId);

        // Validar que la cuenta existe
        if (!cuentaRepository.existsById(cuentaId)) {
            log.error("No se encontró la cuenta con ID: {}", cuentaId);
            throw new RuntimeException("Cuenta no encontrada");
        }

        BigDecimal totalDepositos = transaccionRepository.getTotalDepositosByCuentaId(cuentaId);

        log.info("Total de depósitos para cuenta ID {}: {}", cuentaId, totalDepositos);

        return totalDepositos;
    }

    /**
     * Obtiene el total de retiros de una cuenta.
     * 
     * @param cuentaId El ID de la cuenta
     * @return El total de retiros
     */
    @Transactional(readOnly = true)
    public BigDecimal obtenerTotalRetiros(Long cuentaId) {
        log.info("Obteniendo total de retiros para cuenta ID: {}", cuentaId);

        // Validar que la cuenta existe
        if (!cuentaRepository.existsById(cuentaId)) {
            log.error("No se encontró la cuenta con ID: {}", cuentaId);
            throw new RuntimeException("Cuenta no encontrada");
        }

        BigDecimal totalRetiros = transaccionRepository.getTotalRetirosByCuentaId(cuentaId);

        log.info("Total de retiros para cuenta ID {}: {}", cuentaId, totalRetiros);

        return totalRetiros;
    }

    /**
     * Obtiene las transacciones más recientes.
     * 
     * @param limit El número máximo de transacciones a retornar
     * @return Lista de las transacciones más recientes
     */
    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerTransaccionesRecientes(int limit) {
        log.info("Obteniendo las {} transacciones más recientes", limit);

        List<Transaccion> transacciones = transaccionRepository.findTopTransaccionesRecientes(limit);

        List<TransaccionDTO> transaccionesDTO = transacciones.stream()
                .map(TransaccionDTO::new)
                .collect(Collectors.toList());

        return transaccionesDTO;
    }

    /**
     * Obtiene estadísticas globales de transacciones.
     * 
     * @return Array con estadísticas [total depósitos, total retiros, total transacciones]
     */
    @Transactional(readOnly = true)
    public Object[] obtenerEstadisticasGlobales() {
        log.info("Obteniendo estadísticas globales de transacciones");

        BigDecimal totalDepositos = transaccionRepository.getTotalDepositos();
        BigDecimal totalRetiros = transaccionRepository.getTotalRetiros();
        long totalTransacciones = transaccionRepository.count();

        Object[] estadisticas = {totalDepositos, totalRetiros, totalTransacciones};

        log.info("Estadísticas globales: depósitos: {}, retiros: {}, total transacciones: {}", 
                totalDepositos, totalRetiros, totalTransacciones);

        return estadisticas;
    }
} 