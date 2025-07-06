package com.ahorros.controllers;

import com.ahorros.dto.UsuarioDTO;
import com.ahorros.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    /**
     * Obtiene todos los usuarios
     * @return Lista de usuarios
     */
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> obtenerTodos() {
        List<UsuarioDTO> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(usuarios);
    }
    
    /**
     * Obtiene un usuario por ID
     * @param id ID del usuario
     * @return Usuario encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Registra un nuevo usuario
     * @param request Datos del usuario a registrar
     * @return Usuario creado
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String nombre = request.get("nombre");
            String password = request.get("password");
            
            if (email == null || nombre == null || password == null) {
                return ResponseEntity.badRequest().body("Todos los campos son obligatorios");
            }
            
            UsuarioDTO usuarioCreado = usuarioService.crearUsuario(email, nombre, password);
            return ResponseEntity.ok(usuarioCreado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Autentica un usuario
     * @param request Credenciales del usuario
     * @return Información del usuario si las credenciales son correctas
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("Email y contraseña son obligatorios");
        }
        
        if (usuarioService.verificarCredenciales(email, password)) {
            return usuarioService.obtenerPorEmail(email)
                    .map(usuario -> ResponseEntity.ok(usuarioService.obtenerPorId(usuario.getId()).get()))
                    .orElse(ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.badRequest().body("Credenciales incorrectas");
        }
    }
    
    /**
     * Actualiza un usuario
     * @param id ID del usuario
     * @param request Datos a actualizar
     * @return Usuario actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String nombre = request.get("nombre");
            String email = request.get("email");
            
            if (nombre == null || email == null) {
                return ResponseEntity.badRequest().body("Nombre y email son obligatorios");
            }
            
            UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(id, nombre, email);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Cambia la contraseña de un usuario
     * @param id ID del usuario
     * @param request Nueva contraseña
     * @return Respuesta de éxito
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<?> cambiarPassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String nuevaPassword = request.get("password");
            
            if (nuevaPassword == null) {
                return ResponseEntity.badRequest().body("La nueva contraseña es obligatoria");
            }
            
            usuarioService.cambiarPassword(id, nuevaPassword);
            return ResponseEntity.ok().body("Contraseña actualizada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Desactiva un usuario
     * @param id ID del usuario
     * @return Respuesta de éxito
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivarUsuario(@PathVariable Long id) {
        try {
            usuarioService.desactivarUsuario(id);
            return ResponseEntity.ok().body("Usuario desactivado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Activa un usuario
     * @param id ID del usuario
     * @return Respuesta de éxito
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<?> activarUsuario(@PathVariable Long id) {
        try {
            usuarioService.activarUsuario(id);
            return ResponseEntity.ok().body("Usuario activado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 