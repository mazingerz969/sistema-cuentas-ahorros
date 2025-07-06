package com.ahorros.repositories;

import com.ahorros.models.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    /**
     * Busca todas las notificaciones de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de notificaciones del usuario
     */
    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
    
    /**
     * Busca notificaciones no leídas de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de notificaciones no leídas
     */
    List<Notificacion> findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(Long usuarioId);
    
    /**
     * Cuenta las notificaciones no leídas de un usuario
     * @param usuarioId ID del usuario
     * @return Número de notificaciones no leídas
     */
    long countByUsuarioIdAndLeidaFalse(Long usuarioId);
    
    /**
     * Marca todas las notificaciones de un usuario como leídas
     * @param usuarioId ID del usuario
     */
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.usuario.id = :usuarioId")
    void marcarTodasComoLeidas(@Param("usuarioId") Long usuarioId);
    
    /**
     * Marca una notificación específica como leída
     * @param notificacionId ID de la notificación
     */
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.id = :notificacionId")
    void marcarComoLeida(@Param("notificacionId") Long notificacionId);
} 