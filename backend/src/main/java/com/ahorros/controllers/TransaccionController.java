package com.ahorros.controllers;

import com.ahorros.dto.TransaccionDTO;
import com.ahorros.services.TransaccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para la gestión de transacciones de cuentas de ahorros.
 * 
 * Esta clase expone los endpoints de la API REST para operaciones relacionadas
 * con las transacciones: depósitos, retiros y consultas de historial.
 * 
 * Los endpoints principales son:
 * - POST /transacciones/deposito: Realizar un depósito
 * - POST /transacciones/retiro: Realizar un retiro
 * - GET /transacciones: Obtener todas las transacciones
 * - GET /transacciones/cuenta/{cuentaId}: Obtener transacciones de una cuenta
 * - GET /transacciones/estadisticas: Obtener estadísticas de transacciones
 */
@RestController
@RequestMapping("/transacciones")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transacciones", description = "API para gestión de transacciones de cuentas de ahorros")
@CrossOrigin(origins = "http://localhost:4200")
public class TransaccionController {

    /**
     * Servicio de transacciones inyectado por Spring.
     */
    private final TransaccionService transaccionService;

    /**
     * Realiza un depósito en una cuenta.
     * 
     * Endpoint: POST /api/transacciones/deposito
     * 
     * @param transaccionDTO Los datos de la transacción de depósito
     * @return La transacción creada con código de respuesta 201 (Created)
     */
    @PostMapping("/deposito")
    @Operation(summary = "Realizar depósito", description = "Realiza un depósito en una cuenta de ahorros")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Depósito realizado exitosamente",
                    content = @Content(schema = @Schema(implementation = TransaccionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o monto negativo"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "409", description = "Cuenta inactiva")
    })
    public ResponseEntity<TransaccionDTO> realizarDeposito(
            @Parameter(description = "Datos de la transacción de depósito", required = true)
            @Valid @RequestBody TransaccionDTO transaccionDTO) {
        
        log.info("Recibida solicitud para realizar depósito de {} en cuenta ID: {}", 
                transaccionDTO.getMonto(), transaccionDTO.getCuentaId());
        
        try {
            TransaccionDTO transaccionCreada = transaccionService.realizarDeposito(transaccionDTO);
            log.info("Depósito realizado exitosamente. Transacción ID: {}", transaccionCreada.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(transaccionCreada);
        } catch (RuntimeException e) {
            log.error("Error al realizar depósito: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Realiza un retiro de una cuenta.
     * 
     * Endpoint: POST /api/transacciones/retiro
     * 
     * @param transaccionDTO Los datos de la transacción de retiro
     * @return La transacción creada con código de respuesta 201 (Created)
     */
    @PostMapping("/retiro")
    @Operation(summary = "Realizar retiro", description = "Realiza un retiro de una cuenta de ahorros")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Retiro realizado exitosamente",
                    content = @Content(schema = @Schema(implementation = TransaccionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos, monto negativo o saldo insuficiente"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "409", description = "Cuenta inactiva")
    })
    public ResponseEntity<TransaccionDTO> realizarRetiro(
            @Parameter(description = "Datos de la transacción de retiro", required = true)
            @Valid @RequestBody TransaccionDTO transaccionDTO) {
        
        log.info("Recibida solicitud para realizar retiro de {} de cuenta ID: {}", 
                transaccionDTO.getMonto(), transaccionDTO.getCuentaId());
        
        try {
            TransaccionDTO transaccionCreada = transaccionService.realizarRetiro(transaccionDTO);
            log.info("Retiro realizado exitosamente. Transacción ID: {}", transaccionCreada.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(transaccionCreada);
        } catch (RuntimeException e) {
            log.error("Error al realizar retiro: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene todas las transacciones.
     * 
     * Endpoint: GET /api/transacciones
     * 
     * @return Lista de todas las transacciones con código de respuesta 200 (OK)
     */
    @GetMapping
    @Operation(summary = "Obtener todas las transacciones", description = "Retorna todas las transacciones del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de transacciones obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = TransaccionDTO.class)))
    })
    public ResponseEntity<List<TransaccionDTO>> obtenerTodasLasTransacciones() {
        log.info("Recibida solicitud para obtener todas las transacciones");
        
        List<TransaccionDTO> transacciones = transaccionService.obtenerTodasLasTransacciones();
        log.info("Se retornaron {} transacciones", transacciones.size());
        
        return ResponseEntity.ok(transacciones);
    }

    /**
     * Obtiene las transacciones de una cuenta específica.
     * 
     * Endpoint: GET /api/transacciones/cuenta/{cuentaId}
     * 
     * @param cuentaId El ID de la cuenta
     * @return Lista de transacciones de la cuenta
     */
    @GetMapping("/cuenta/{cuentaId}")
    @Operation(summary = "Obtener transacciones por cuenta", description = "Retorna todas las transacciones de una cuenta específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de transacciones obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = TransaccionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<List<TransaccionDTO>> obtenerTransaccionesPorCuenta(
            @Parameter(description = "ID de la cuenta", required = true)
            @PathVariable Long cuentaId) {
        
        log.info("Recibida solicitud para obtener transacciones de cuenta ID: {}", cuentaId);
        
        try {
            List<TransaccionDTO> transacciones = transaccionService.obtenerTransaccionesPorCuenta(cuentaId);
            log.info("Se retornaron {} transacciones para la cuenta ID: {}", transacciones.size(), cuentaId);
            return ResponseEntity.ok(transacciones);
        } catch (RuntimeException e) {
            log.error("Error al obtener transacciones: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene una transacción por su ID.
     * 
     * Endpoint: GET /api/transacciones/{id}
     * 
     * @param id El ID de la transacción
     * @return La transacción encontrada
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener transacción por ID", description = "Retorna una transacción específica por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transacción encontrada exitosamente",
                    content = @Content(schema = @Schema(implementation = TransaccionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    public ResponseEntity<TransaccionDTO> obtenerTransaccionPorId(
            @Parameter(description = "ID de la transacción", required = true)
            @PathVariable Long id) {
        
        log.info("Recibida solicitud para obtener transacción con ID: {}", id);
        
        try {
            TransaccionDTO transaccion = transaccionService.obtenerTransaccionPorId(id);
            log.info("Transacción encontrada: {} - {}", transaccion.getTipo(), transaccion.getMonto());
            return ResponseEntity.ok(transaccion);
        } catch (RuntimeException e) {
            log.error("Error al obtener transacción: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene transacciones por tipo.
     * 
     * Endpoint: GET /api/transacciones/tipo/{tipo}
     * 
     * @param tipo El tipo de transacción (DEPOSITO o RETIRO)
     * @return Lista de transacciones del tipo especificado
     */
    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Obtener transacciones por tipo", description = "Retorna transacciones de un tipo específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de transacciones obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = TransaccionDTO.class)))
    })
    public ResponseEntity<List<TransaccionDTO>> obtenerTransaccionesPorTipo(
            @Parameter(description = "Tipo de transacción (DEPOSITO o RETIRO)", required = true)
            @PathVariable String tipo) {
        
        log.info("Recibida solicitud para obtener transacciones de tipo: {}", tipo);
        
        List<TransaccionDTO> transacciones = transaccionService.obtenerTransaccionesPorTipo(tipo);
        log.info("Se retornaron {} transacciones de tipo {}", transacciones.size(), tipo);
        
        return ResponseEntity.ok(transacciones);
    }

    /**
     * Obtiene transacciones de una cuenta por tipo.
     * 
     * Endpoint: GET /api/transacciones/cuenta/{cuentaId}/tipo/{tipo}
     * 
     * @param cuentaId El ID de la cuenta
     * @param tipo El tipo de transacción
     * @return Lista de transacciones de la cuenta del tipo especificado
     */
    @GetMapping("/cuenta/{cuentaId}/tipo/{tipo}")
    @Operation(summary = "Obtener transacciones por cuenta y tipo", 
               description = "Retorna transacciones de una cuenta específica de un tipo determinado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de transacciones obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = TransaccionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<List<TransaccionDTO>> obtenerTransaccionesPorCuentaYTipo(
            @Parameter(description = "ID de la cuenta", required = true)
            @PathVariable Long cuentaId,
            @Parameter(description = "Tipo de transacción (DEPOSITO o RETIRO)", required = true)
            @PathVariable String tipo) {
        
        log.info("Recibida solicitud para obtener transacciones de cuenta ID: {} y tipo: {}", cuentaId, tipo);
        
        try {
            List<TransaccionDTO> transacciones = transaccionService.obtenerTransaccionesPorCuentaYTipo(cuentaId, tipo);
            log.info("Se retornaron {} transacciones de tipo {} para la cuenta ID: {}", 
                    transacciones.size(), tipo, cuentaId);
            return ResponseEntity.ok(transacciones);
        } catch (RuntimeException e) {
            log.error("Error al obtener transacciones: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene estadísticas de transacciones de una cuenta.
     * 
     * Endpoint: GET /api/transacciones/estadisticas/cuenta/{cuentaId}
     * 
     * @param cuentaId El ID de la cuenta
     * @return Estadísticas de transacciones de la cuenta
     */
    @GetMapping("/estadisticas/cuenta/{cuentaId}")
    @Operation(summary = "Obtener estadísticas por cuenta", 
               description = "Retorna estadísticas de transacciones de una cuenta específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<Object[]> obtenerEstadisticasTransacciones(
            @Parameter(description = "ID de la cuenta", required = true)
            @PathVariable Long cuentaId) {
        
        log.info("Recibida solicitud para obtener estadísticas de transacciones de cuenta ID: {}", cuentaId);
        
        try {
            Object[] estadisticas = transaccionService.obtenerEstadisticasTransacciones(cuentaId);
            log.info("Estadísticas obtenidas para cuenta ID {}: depósitos: {}, retiros: {}, total: {}", 
                    cuentaId, estadisticas[0], estadisticas[1], estadisticas[2]);
            return ResponseEntity.ok(estadisticas);
        } catch (RuntimeException e) {
            log.error("Error al obtener estadísticas: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene el total de depósitos de una cuenta.
     * 
     * Endpoint: GET /api/transacciones/depositos/cuenta/{cuentaId}
     * 
     * @param cuentaId El ID de la cuenta
     * @return El total de depósitos
     */
    @GetMapping("/depositos/cuenta/{cuentaId}")
    @Operation(summary = "Obtener total de depósitos por cuenta", 
               description = "Retorna el total de depósitos de una cuenta específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total de depósitos obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<BigDecimal> obtenerTotalDepositos(
            @Parameter(description = "ID de la cuenta", required = true)
            @PathVariable Long cuentaId) {
        
        log.info("Recibida solicitud para obtener total de depósitos de cuenta ID: {}", cuentaId);
        
        try {
            BigDecimal totalDepositos = transaccionService.obtenerTotalDepositos(cuentaId);
            log.info("Total de depósitos para cuenta ID {}: {}", cuentaId, totalDepositos);
            return ResponseEntity.ok(totalDepositos);
        } catch (RuntimeException e) {
            log.error("Error al obtener total de depósitos: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene el total de retiros de una cuenta.
     * 
     * Endpoint: GET /api/transacciones/retiros/cuenta/{cuentaId}
     * 
     * @param cuentaId El ID de la cuenta
     * @return El total de retiros
     */
    @GetMapping("/retiros/cuenta/{cuentaId}")
    @Operation(summary = "Obtener total de retiros por cuenta", 
               description = "Retorna el total de retiros de una cuenta específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total de retiros obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<BigDecimal> obtenerTotalRetiros(
            @Parameter(description = "ID de la cuenta", required = true)
            @PathVariable Long cuentaId) {
        
        log.info("Recibida solicitud para obtener total de retiros de cuenta ID: {}", cuentaId);
        
        try {
            BigDecimal totalRetiros = transaccionService.obtenerTotalRetiros(cuentaId);
            log.info("Total de retiros para cuenta ID {}: {}", cuentaId, totalRetiros);
            return ResponseEntity.ok(totalRetiros);
        } catch (RuntimeException e) {
            log.error("Error al obtener total de retiros: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene las transacciones más recientes.
     * 
     * Endpoint: GET /api/transacciones/recientes?limit={limit}
     * 
     * @param limit El número máximo de transacciones a retornar (por defecto 10)
     * @return Lista de las transacciones más recientes
     */
    @GetMapping("/recientes")
    @Operation(summary = "Obtener transacciones recientes", 
               description = "Retorna las transacciones más recientes del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de transacciones recientes obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = TransaccionDTO.class)))
    })
    public ResponseEntity<List<TransaccionDTO>> obtenerTransaccionesRecientes(
            @Parameter(description = "Número máximo de transacciones a retornar", required = false)
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Recibida solicitud para obtener las {} transacciones más recientes", limit);
        
        List<TransaccionDTO> transacciones = transaccionService.obtenerTransaccionesRecientes(limit);
        log.info("Se retornaron {} transacciones recientes", transacciones.size());
        
        return ResponseEntity.ok(transacciones);
    }

    /**
     * Obtiene estadísticas globales de transacciones.
     * 
     * Endpoint: GET /api/transacciones/estadisticas/globales
     * 
     * @return Estadísticas globales de transacciones
     */
    @GetMapping("/estadisticas/globales")
    @Operation(summary = "Obtener estadísticas globales", 
               description = "Retorna estadísticas globales de todas las transacciones del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas globales obtenidas exitosamente")
    })
    public ResponseEntity<Object[]> obtenerEstadisticasGlobales() {
        log.info("Recibida solicitud para obtener estadísticas globales de transacciones");
        
        Object[] estadisticas = transaccionService.obtenerEstadisticasGlobales();
        log.info("Estadísticas globales: depósitos: {}, retiros: {}, total transacciones: {}", 
                estadisticas[0], estadisticas[1], estadisticas[2]);
        
        return ResponseEntity.ok(estadisticas);
    }
} 