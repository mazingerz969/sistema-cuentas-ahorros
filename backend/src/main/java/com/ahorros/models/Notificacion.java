package com.ahorros.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
public class Notificacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String mensaje;
    
    @Column(nullable = false)
    private String tipo; // "TRANSACCION", "SALDO_BAJO", "META_ALCANZADA", etc.
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(nullable = false)
    private boolean leida = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    // Constructores
    public Notificacion() {}
    
    public Notificacion(String mensaje, String tipo, Usuario usuario) {
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.usuario = usuario;
        this.fechaCreacion = LocalDateTime.now();
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
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
} 