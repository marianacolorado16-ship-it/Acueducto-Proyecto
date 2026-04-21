package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.Usuario;
import com.example.Acueducto.Proyecto.Model.Rol;
import com.example.Acueducto.Proyecto.Repository.AuthenticationRepository;
import com.example.Acueducto.Proyecto.Repository.RolRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthenticationService {

        private final AuthenticationRepository authenticationRepository;
        private final RolRepository rolRepository;

        public AuthenticationService(AuthenticationRepository authenticationRepository,
                                     RolRepository rolRepository) {
                this.authenticationRepository = authenticationRepository;
                this.rolRepository = rolRepository;
        }
    
    /**
     * Autentica un usuario verificando credenciales
     */
    public Usuario login(String username, String password) {
        Usuario usuario = authenticationRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Validación directa al eliminar Spring Security PasswordEncoder
        if (usuario.getPasswordHash() != null && !usuario.getPasswordHash().equals(password)) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        
        return usuario;
    }
    
    /**
     * Registra un nuevo usuario en el sistema
     */
    public Usuario register(Usuario usuario) {
        // Validar que el usuario no exista
        if (authenticationRepository.existsByUsername(usuario.getUsername())) {
                        throw new RuntimeException("El nombre de usuario ya existe");
        }
        
        if (authenticationRepository.existsByEmail(usuario.getEmail())) {
                        throw new RuntimeException("El email ya está registrado");
        }
        
        // Configuración de estado por defecto
        if (usuario.getEstado() == null) {
            usuario.setEstado("ACTIVO");
        }
        
        // Asignar rol por defecto (CLIENTE)
        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));
        usuario.addRol(rolCliente);
        
        return authenticationRepository.save(usuario);
    }

    /**
     * Restablece la contraseña de un usuario basado en su email
     */
    public void resetPassword(String email, String newPassword) {
        Usuario usuario = authenticationRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No existe un usuario registrado con ese correo electrónico"));
        
        usuario.setPasswordHash(newPassword);
        authenticationRepository.save(usuario);
    }
}
