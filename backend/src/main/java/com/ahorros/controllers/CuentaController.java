package com.ahorros.controllers;

import com.ahorros.dto.CuentaDTO;
import com.ahorros.services.CuentaService;
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
import java.util.List;

/**
 * Controlador REST para la gestión de cuentas de ahorros.
 * 
 * Esta clase expone los endpoints de la API REST para operaciones CRUD
 * relacionadas con las cuentas de ahorros. Cada método está mapeado a
 * un endpoint HTTP específico y utiliza los servicios para procesar
 * las solicitudes.
 * 
 * Anotaciones utilizadas:
 * - @RestController: Indica que esta clase es un controlador REST
 * - @RequestMapping: Define la ruta base para todos los endpoints
 * - @GetMapping, @PostMapping, etc.: Define el método HTTP y la ruta específica
 * - @Valid: Valida los datos de entrada usando las anotaciones de validación
 * - @RequestBody: Indica que el parámetro viene del cuerpo de la petición HTTP
 * - @PathVariable: Indica que el parámetro viene de la URL
 * - @RequestParam: Indica que el parámetro viene de los parámetros de consulta
 * 
 * Documentación con Swagger/OpenAPI:
 * - @Tag: Agrupa los endpoints en la documentación
 * - @Operation: Describe la operación del endpoint
 * - @ApiResponses: Define las posibles respuestas del endpoint
 * - @Parameter: Describe los parámetros del endpoint
 */
@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cuentas", description = "API para gestión de cuentas de ahorros")
@CrossOrigin(origins = "http://localhost:4200")
public class CuentaController {

    /**
     * Servicio de cuentas inyectado por Spring.
     */
    private final CuentaService cuentaService;

    /**
     * Crea una nueva cuenta de ahorros.
     * 
     * Endpoint: POST /api/cuentas
     * 
     * @param cuentaDTO Los datos de la cuenta a crear
     * @return La cuenta creada con código de respuesta 201 (Created)
     */
    @PostMapping
    @Operation(summary = "Crear cuenta", description = "Crea una nueva cuenta de ahorros")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cuenta creada exitosamente",
                    content = @Content(schema = @Schema(implementation = CuentaDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Número de cuenta ya existe")
    })
    public ResponseEntity<CuentaDTO> crearCuenta(
            @Parameter(description = "Datos de la cuenta a crear", required = true)
            @Valid @RequestBody CuentaDTO cuentaDTO) {
        
        log.info("Recibida solicitud para crear cuenta: {}", cuentaDTO.getNumeroCuenta());
        
        try {
            CuentaDTO cuentaCreada = cuentaService.crearCuenta(cuentaDTO);
            log.info("Cuenta creada exitosamente con ID: {}", cuentaCreada.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(cuentaCreada);
        } catch (RuntimeException e) {
            log.error("Error al crear cuenta: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene todas las cuentas.
     * 
     * Endpoint: GET /api/cuentas
     * 
     * @return Lista de todas las cuentas con código de respuesta 200 (OK)
     */
    @GetMapping
    @Operation(summary = "Obtener todas las cuentas", description = "Retorna todas las cuentas de ahorros")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de cuentas obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = CuentaDTO.class)))
    })
    public ResponseEntity<List<CuentaDTO>> obtenerTodasLasCuentas() {
        log.info("Recibida solicitud para obtener todas las cuentas");
        
        List<CuentaDTO> cuentas = cuentaService.obtenerTodasLasCuentas();
        log.info("Se retornaron {} cuentas", cuentas.size());
        
        return ResponseEntity.ok(cuentas);
    }

    /**
     * Obtiene una cuenta por su ID.
     * 
     * Endpoint: GET /api/cuentas/{id}
     * 
     * @param id El ID de la cuenta
     * @return La cuenta encontrada con código de respuesta 200 (OK)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta por ID", description = "Retorna una cuenta específica por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta encontrada exitosamente",
                    content = @Content(schema = @Schema(implementation = CuentaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<CuentaDTO> obtenerCuentaPorId(
            @Parameter(description = "ID de la cuenta", required = true)
            @PathVariable Long id) {
        
        log.info("Recibida solicitud para obtener cuenta con ID: {}", id);
        
        try {
            CuentaDTO cuenta = cuentaService.obtenerCuentaPorId(id);
            log.info("Cuenta encontrada: {}", cuenta.getNumeroCuenta());
            return ResponseEntity.ok(cuenta);
        } catch (RuntimeException e) {
            log.error("Error al obtener cuenta: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene una cuenta por su número de cuenta.
     * 
     * Endpoint: GET /api/cuentas/numero/{numeroCuenta}
     * 
     * @param numeroCuenta El número de cuenta
     * @return La cuenta encontrada con código de respuesta 200 (OK)
     */
    @GetMapping("/numero/{numeroCuenta}")
    @Operation(summary = "Obtener cuenta por número", description = "Retorna una cuenta específica por su número")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta encontrada exitosamente",
                    content = @Content(schema = @Schema(implementation = CuentaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<CuentaDTO> obtenerCuentaPorNumero(
            @Parameter(description = "Número de cuenta", required = true)
            @PathVariable String numeroCuenta) {
        
        log.info("Recibida solicitud para obtener cuenta con número: {}", numeroCuenta);
        
        try {
            CuentaDTO cuenta = cuentaService.obtenerCuentaPorNumero(numeroCuenta);
            log.info("Cuenta encontrada con ID: {}", cuenta.getId());
            return ResponseEntity.ok(cuenta);
        } catch (RuntimeException e) {
            log.error("Error al obtener cuenta: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Busca cuentas por nombre del titular.
     * 
     * Endpoint: GET /api/cuentas/buscar?titular={titular}
     * 
     * @param titular El nombre del titular (parcial)
     * @return Lista de cuentas que coinciden con el titular
     */
    @GetMapping("/buscar")
    @Operation(summary = "Buscar cuentas por titular", description = "Busca cuentas por nombre del titular")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente",
                    content = @Content(schema = @Schema(implementation = CuentaDTO.class)))
    })
    public ResponseEntity<List<CuentaDTO>> buscarCuentasPorTitular(
            @Parameter(description = "Nombre del titular (parcial)", required = true)
            @RequestParam String titular) {
        
        log.info("Recibida solicitud para buscar cuentas por titular: {}", titular);
        
        List<CuentaDTO> cuentas = cuentaService.buscarCuentasPorTitular(titular);
        log.info("Se encontraron {} cuentas para el titular: {}", cuentas.size(), titular);
        
        return ResponseEntity.ok(cuentas);
    }

    /**
     * Obtiene todas las cuentas activas.
     * 
     * Endpoint: GET /api/cuentas/activas
     * 
     * @return Lista de cuentas activas
     */
    @GetMapping("/activas")
    @Operation(summary = "Obtener cuentas activas", description = "Retorna todas las cuentas activas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de cuentas activas obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = CuentaDTO.class)))
    })
    public ResponseEntity<List<CuentaDTO>> obtenerCuentasActivas() {
        log.info("Recibida solicitud para obtener cuentas activas");
        
        List<CuentaDTO> cuentas = cuentaService.obtenerCuentasActivas();
        log.info("Se retornaron {} cuentas activas", cuentas.size());
        
        return ResponseEntity.ok(cuentas);
    }

    /**
     * Actualiza una cuenta existente.
     * 
     * Endpoint: PUT /api/cuentas/{id}
     * 
     * @param id El ID de la cuenta a actualizar
     * @param cuentaDTO Los nuevos datos de la cuenta
     * @return La cuenta actualizada
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cuenta", description = "Actualiza una cuenta existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cuenta actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = CuentaDTO.class))),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<CuentaDTO> actualizarCuenta(
            @Parameter(description = "ID de la cuenta", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nuevos datos de la cuenta", required = true)
            @Valid @RequestBody CuentaDTO cuentaDTO) {
        
        log.info("Recibida solicitud para actualizar cuenta con ID: {}", id);
        
        try {
            CuentaDTO cuentaActualizada = cuentaService.actualizarCuenta(id, cuentaDTO);
            log.info("Cuenta actualizada exitosamente: {}", cuentaActualizada.getNumeroCuenta());
            return ResponseEntity.ok(cuentaActualizada);
        } catch (RuntimeException e) {
            log.error("Error al actualizar cuenta: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Elimina una cuenta.
     * 
     * Endpoint: DELETE /api/cuentas/{id}
     * 
     * @param id El ID de la cuenta a eliminar
     * @return Respuesta sin contenido con código 204 (No Content)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cuenta", description = "Elimina una cuenta existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cuenta eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<Void> eliminarCuenta(
            @Parameter(description = "ID de la cuenta", required = true)
            @PathVariable Long id) {
        
        log.info("Recibida solicitud para eliminar cuenta con ID: {}", id);
        
        try {
            cuentaService.eliminarCuenta(id);
            log.info("Cuenta eliminada exitosamente");
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error al eliminar cuenta: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene estadísticas de las cuentas.
     * 
     * Endpoint: GET /api/cuentas/estadisticas
     * 
     * @return Estadísticas de las cuentas
     */
    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas", description = "Retorna estadísticas de las cuentas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    })
    public ResponseEntity<Object[]> obtenerEstadisticas() {
        log.info("Recibida solicitud para obtener estadísticas de cuentas");
        
        Object[] estadisticas = cuentaService.obtenerEstadisticas();
        log.info("Estadísticas obtenidas: {} cuentas totales, {} activas", estadisticas[0], estadisticas[1]);
        
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Obtiene cuentas ordenadas por saldo (mayor a menor).
     * 
     * Endpoint: GET /api/cuentas/ordenadas/saldo
     * 
     * @return Lista de cuentas ordenadas por saldo
     */
    @GetMapping("/ordenadas/saldo")
    @Operation(summary = "Obtener cuentas ordenadas por saldo", description = "Retorna cuentas ordenadas por saldo descendente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de cuentas ordenadas obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = CuentaDTO.class)))
    })
    public ResponseEntity<List<CuentaDTO>> obtenerCuentasOrdenadasPorSaldo() {
        log.info("Recibida solicitud para obtener cuentas ordenadas por saldo");
        
        List<CuentaDTO> cuentas = cuentaService.obtenerCuentasOrdenadasPorSaldo();
        log.info("Se retornaron {} cuentas ordenadas por saldo", cuentas.size());
        
        return ResponseEntity.ok(cuentas);
    }

    /**
     * Obtiene cuentas con saldo superior al promedio.
     * 
     * Endpoint: GET /api/cuentas/superior-promedio
     * 
     * @return Lista de cuentas con saldo superior al promedio
     */
    @GetMapping("/superior-promedio")
    @Operation(summary = "Obtener cuentas con saldo superior al promedio", 
               description = "Retorna cuentas con saldo mayor al promedio de todas las cuentas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de cuentas obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = CuentaDTO.class)))
    })
    public ResponseEntity<List<CuentaDTO>> obtenerCuentasConSaldoSuperiorAlPromedio() {
        log.info("Recibida solicitud para obtener cuentas con saldo superior al promedio");
        
        List<CuentaDTO> cuentas = cuentaService.obtenerCuentasConSaldoSuperiorAlPromedio();
        log.info("Se retornaron {} cuentas con saldo superior al promedio", cuentas.size());
        
        return ResponseEntity.ok(cuentas);
    }
} 