package com.ahorros.dto;

import java.time.LocalDateTime;

public class UsuarioDTO {
    
    private Long id;
    private String email;
    private String nombre;
    private LocalDateTime fechaRegistro;
    private boolean activo;
    
    // Constructor vacío
    public UsuarioDTO() {}
    
    // Constructor con campos
    public UsuarioDTO(Long id, String email, String nombre, LocalDateTime fechaRegistro, boolean activo) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
} 