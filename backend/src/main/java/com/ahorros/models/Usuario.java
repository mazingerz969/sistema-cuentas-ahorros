package com.ahorros.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private LocalDateTime fechaRegistro;
    
    @Column(nullable = false)
    private boolean activo = true;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cuenta> cuentas;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notificacion> notificaciones;
    
    // Constructores
    public Usuario() {}
    
    public Usuario(String email, String nombre, String password) {
        this.email = email;
        this.nombre = nombre;
        this.password = password;
        this.fechaRegistro = LocalDateTime.now();
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
    
    public List<Cuenta> getCuentas() {
        return cuentas;
    }
    
    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }
    
    public List<Notificacion> getNotificaciones() {
        return notificaciones;
    }
    
    public void setNotificaciones(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }
} 