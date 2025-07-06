package com.ahorros.services;

import com.ahorros.dto.NotificacionDTO;
import com.ahorros.models.Notificacion;
import com.ahorros.models.Usuario;
import com.ahorros.repositories.NotificacionRepository;
import com.ahorros.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificacionService {
    
    @Autowired
    private NotificacionRepository notificacionRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Obtiene todas las notificaciones de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de DTOs de notificaciones
     */
    public List<NotificacionDTO> obtenerNotificacionesPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene las notificaciones no leídas de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de DTOs de notificaciones no leídas
     */
    public List<NotificacionDTO> obtenerNotificacionesNoLeidas(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(usuarioId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Cuenta las notificaciones no leídas de un usuario
     * @param usuarioId ID del usuario
     * @return Número de notificaciones no leídas
     */
    public long contarNotificacionesNoLeidas(Long usuarioId) {
        return notificacionRepository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }
    
    /**
     * Crea una nueva notificación
     * @param mensaje Mensaje de la notificación
     * @param tipo Tipo de notificación
     * @param usuarioId ID del usuario
     * @return DTO de la notificación creada
     */
    public NotificacionDTO crearNotificacion(String mensaje, String tipo, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Notificacion notificacion = new Notificacion(mensaje, tipo, usuario);
        Notificacion notificacionGuardada = notificacionRepository.save(notificacion);
        return convertirADTO(notificacionGuardada);
    }
    
    /**
     * Marca una notificación como leída
     * @param notificacionId ID de la notificación
     */
    public void marcarComoLeida(Long notificacionId) {
        if (!notificacionRepository.existsById(notificacionId)) {
            throw new RuntimeException("Notificación no encontrada");
        }
        notificacionRepository.marcarComoLeida(notificacionId);
    }
    
    /**
     * Marca todas las notificaciones de un usuario como leídas
     * @param usuarioId ID del usuario
     */
    public void marcarTodasComoLeidas(Long usuarioId) {
        notificacionRepository.marcarTodasComoLeidas(usuarioId);
    }
    
    /**
     * Elimina una notificación
     * @param notificacionId ID de la notificación
     */
    public void eliminarNotificacion(Long notificacionId) {
        if (!notificacionRepository.existsById(notificacionId)) {
            throw new RuntimeException("Notificación no encontrada");
        }
        notificacionRepository.deleteById(notificacionId);
    }
    
    /**
     * Obtiene una notificación por ID
     * @param notificacionId ID de la notificación
     * @return Optional con el DTO de la notificación
     */
    public Optional<NotificacionDTO> obtenerPorId(Long notificacionId) {
        return notificacionRepository.findById(notificacionId)
                .map(this::convertirADTO);
    }
    
    /**
     * Crea notificaciones automáticas para transacciones
     * @param usuarioId ID del usuario
     * @param tipoTransaccion Tipo de transacción (DEPOSITO, RETIRO)
     * @param monto Monto de la transacción
     * @param numeroCuenta Número de cuenta
     */
    public void crearNotificacionTransaccion(Long usuarioId, String tipoTransaccion, String monto, String numeroCuenta) {
        String mensaje;
        String tipo;
        
        if ("DEPOSITO".equals(tipoTransaccion)) {
            mensaje = String.format("Se ha realizado un depósito de $%s en la cuenta %s", monto, numeroCuenta);
            tipo = "TRANSACCION_DEPOSITO";
        } else {
            mensaje = String.format("Se ha realizado un retiro de $%s de la cuenta %s", monto, numeroCuenta);
            tipo = "TRANSACCION_RETIRO";
        }
        
        crearNotificacion(mensaje, tipo, usuarioId);
    }
    
    /**
     * Crea notificación de saldo bajo
     * @param usuarioId ID del usuario
     * @param numeroCuenta Número de cuenta
     * @param saldo Saldo actual
     */
    public void crearNotificacionSaldoBajo(Long usuarioId, String numeroCuenta, String saldo) {
        String mensaje = String.format("¡Atención! El saldo de la cuenta %s es bajo: $%s", numeroCuenta, saldo);
        crearNotificacion(mensaje, "SALDO_BAJO", usuarioId);
    }
    
    /**
     * Convierte un Notificacion a NotificacionDTO
     * @param notificacion Notificación a convertir
     * @return NotificacionDTO
     */
    private NotificacionDTO convertirADTO(Notificacion notificacion) {
        return new NotificacionDTO(
                notificacion.getId(),
                notificacion.getMensaje(),
                notificacion.getTipo(),
                notificacion.getFechaCreacion(),
                notificacion.isLeida(),
                notificacion.getUsuario().getId()
        );
    }
} 