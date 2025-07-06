package com.ahorros.controllers;

import com.ahorros.dto.NotificacionDTO;
import com.ahorros.services.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {
    
    @Autowired
    private NotificacionService notificacionService;
    
    /**
     * Obtiene todas las notificaciones de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de notificaciones
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        List<NotificacionDTO> notificaciones = notificacionService.obtenerNotificacionesPorUsuario(usuarioId);
        return ResponseEntity.ok(notificaciones);
    }
    
    /**
     * Obtiene las notificaciones no leídas de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de notificaciones no leídas
     */
    @GetMapping("/usuario/{usuarioId}/no-leidas")
    public ResponseEntity<List<NotificacionDTO>> obtenerNoLeidas(@PathVariable Long usuarioId) {
        List<NotificacionDTO> notificaciones = notificacionService.obtenerNotificacionesNoLeidas(usuarioId);
        return ResponseEntity.ok(notificaciones);
    }
    
    /**
     * Cuenta las notificaciones no leídas de un usuario
     * @param usuarioId ID del usuario
     * @return Número de notificaciones no leídas
     */
    @GetMapping("/usuario/{usuarioId}/contar-no-leidas")
    public ResponseEntity<Map<String, Long>> contarNoLeidas(@PathVariable Long usuarioId) {
        long count = notificacionService.contarNotificacionesNoLeidas(usuarioId);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    /**
     * Obtiene una notificación por ID
     * @param id ID de la notificación
     * @return Notificación encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDTO> obtenerPorId(@PathVariable Long id) {
        return notificacionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Crea una nueva notificación
     * @param request Datos de la notificación
     * @return Notificación creada
     */
    @PostMapping
    public ResponseEntity<?> crearNotificacion(@RequestBody Map<String, String> request) {
        try {
            String mensaje = request.get("mensaje");
            String tipo = request.get("tipo");
            String usuarioIdStr = request.get("usuarioId");
            
            if (mensaje == null || tipo == null || usuarioIdStr == null) {
                return ResponseEntity.badRequest().body("Mensaje, tipo y usuarioId son obligatorios");
            }
            
            Long usuarioId = Long.parseLong(usuarioIdStr);
            NotificacionDTO notificacionCreada = notificacionService.crearNotificacion(mensaje, tipo, usuarioId);
            return ResponseEntity.ok(notificacionCreada);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("UsuarioId debe ser un número válido");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Marca una notificación como leída
     * @param id ID de la notificación
     * @return Respuesta de éxito
     */
    @PutMapping("/{id}/leer")
    public ResponseEntity<?> marcarComoLeida(@PathVariable Long id) {
        try {
            notificacionService.marcarComoLeida(id);
            return ResponseEntity.ok().body("Notificación marcada como leída");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Marca todas las notificaciones de un usuario como leídas
     * @param usuarioId ID del usuario
     * @return Respuesta de éxito
     */
    @PutMapping("/usuario/{usuarioId}/leer-todas")
    public ResponseEntity<?> marcarTodasComoLeidas(@PathVariable Long usuarioId) {
        try {
            notificacionService.marcarTodasComoLeidas(usuarioId);
            return ResponseEntity.ok().body("Todas las notificaciones marcadas como leídas");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Elimina una notificación
     * @param id ID de la notificación
     * @return Respuesta de éxito
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarNotificacion(@PathVariable Long id) {
        try {
            notificacionService.eliminarNotificacion(id);
            return ResponseEntity.ok().body("Notificación eliminada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Crea una notificación automática para transacciones
     * @param request Datos de la transacción
     * @return Respuesta de éxito
     */
    @PostMapping("/transaccion")
    public ResponseEntity<?> crearNotificacionTransaccion(@RequestBody Map<String, String> request) {
        try {
            String usuarioIdStr = request.get("usuarioId");
            String tipoTransaccion = request.get("tipoTransaccion");
            String monto = request.get("monto");
            String numeroCuenta = request.get("numeroCuenta");
            
            if (usuarioIdStr == null || tipoTransaccion == null || monto == null || numeroCuenta == null) {
                return ResponseEntity.badRequest().body("Todos los campos son obligatorios");
            }
            
            Long usuarioId = Long.parseLong(usuarioIdStr);
            notificacionService.crearNotificacionTransaccion(usuarioId, tipoTransaccion, monto, numeroCuenta);
            return ResponseEntity.ok().body("Notificación de transacción creada");
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("UsuarioId debe ser un número válido");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Crea una notificación de saldo bajo
     * @param request Datos del saldo bajo
     * @return Respuesta de éxito
     */
    @PostMapping("/saldo-bajo")
    public ResponseEntity<?> crearNotificacionSaldoBajo(@RequestBody Map<String, String> request) {
        try {
            String usuarioIdStr = request.get("usuarioId");
            String numeroCuenta = request.get("numeroCuenta");
            String saldo = request.get("saldo");
            
            if (usuarioIdStr == null || numeroCuenta == null || saldo == null) {
                return ResponseEntity.badRequest().body("Todos los campos son obligatorios");
            }
            
            Long usuarioId = Long.parseLong(usuarioIdStr);
            notificacionService.crearNotificacionSaldoBajo(usuarioId, numeroCuenta, saldo);
            return ResponseEntity.ok().body("Notificación de saldo bajo creada");
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("UsuarioId debe ser un número válido");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 