package com.ahorros.dto;

import java.time.LocalDateTime;

public class NotificacionDTO {
    
    private Long id;
    private String mensaje;
    private String tipo;
    private LocalDateTime fechaCreacion;
    private boolean leida;
    private Long usuarioId;
    
    // Constructor vacío
    public NotificacionDTO() {}
    
    // Constructor con campos
    public NotificacionDTO(Long id, String mensaje, String tipo, LocalDateTime fechaCreacion, boolean leida, Long usuarioId) {
        this.id = id;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.fechaCreacion = fechaCreacion;
        this.leida = leida;
        this.usuarioId = usuarioId;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public boolean isLeida() {
        return leida;
    }
    
    public void setLeida(boolean leida) {
        this.leida = leida;
    }
    
    public Long getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
} 