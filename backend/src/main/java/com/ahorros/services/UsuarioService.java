package com.ahorros.services;

import com.ahorros.dto.UsuarioDTO;
import com.ahorros.models.Usuario;
import com.ahorros.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Obtiene todos los usuarios
     * @return Lista de DTOs de usuarios
     */
    public List<UsuarioDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene un usuario por ID
     * @param id ID del usuario
     * @return Optional con el DTO del usuario
     */
    public Optional<UsuarioDTO> obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(this::convertirADTO);
    }
    
    /**
     * Obtiene un usuario por email
     * @param email Email del usuario
     * @return Optional con el usuario
     */
    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    /**
     * Crea un nuevo usuario
     * @param email Email del usuario
     * @param nombre Nombre del usuario
     * @param password Contraseña del usuario
     * @return DTO del usuario creado
     */
    public UsuarioDTO crearUsuario(String email, String nombre, String password) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }
        
        Usuario usuario = new Usuario(email, nombre, passwordEncoder.encode(password));
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return convertirADTO(usuarioGuardado);
    }
    
    /**
     * Actualiza un usuario
     * @param id ID del usuario
     * @param nombre Nuevo nombre
     * @param email Nuevo email
     * @return DTO del usuario actualizado
     */
    public UsuarioDTO actualizarUsuario(Long id, String nombre, String email) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!usuario.getEmail().equals(email) && usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }
        
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertirADTO(usuarioActualizado);
    }
    
    /**
     * Cambia la contraseña de un usuario
     * @param id ID del usuario
     * @param nuevaPassword Nueva contraseña
     */
    public void cambiarPassword(Long id, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
    }
    
    /**
     * Desactiva un usuario
     * @param id ID del usuario
     */
    public void desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
    
    /**
     * Activa un usuario
     * @param id ID del usuario
     */
    public void activarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }
    
    /**
     * Verifica las credenciales de un usuario
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return true si las credenciales son correctas
     */
    public boolean verificarCredenciales(String email, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            return usuario.isActivo() && passwordEncoder.matches(password, usuario.getPassword());
        }
        return false;
    }
    
    /**
     * Convierte un Usuario a UsuarioDTO
     * @param usuario Usuario a convertir
     * @return UsuarioDTO
     */
    private UsuarioDTO convertirADTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getFechaRegistro(),
                usuario.isActivo()
        );
    }
} 