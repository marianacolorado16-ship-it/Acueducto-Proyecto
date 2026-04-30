package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.Rol;
import com.example.Acueducto.Proyecto.Model.Usuario;
import com.example.Acueducto.Proyecto.Repository.RolRepository;
import com.example.Acueducto.Proyecto.Repository.UsuarioRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @Transactional(readOnly = true)
    public Page<Usuario> listarUsuarios(String busqueda, String estado, Pageable pageable) {
        Specification<Usuario> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (estado != null && !estado.isBlank()) {
                predicates.add(cb.equal(root.get("estado"), estado.trim().toUpperCase()));
            }

            if (busqueda != null && !busqueda.isBlank()) {
                String like = "%" + busqueda.trim().toLowerCase() + "%";
                predicates.add(
                    cb.or(
                        cb.like(cb.lower(root.get("username")), like),
                        cb.like(cb.lower(root.get("email")), like)
                    )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return usuarioRepository.findAll(spec, pageable);
    }

    public Usuario crearUsuario(Usuario usuario) {
        // Validaciones básicas de duplicados
        if (usuario.getUsername() != null && usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        if (usuario.getEmail() != null && usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Cargar roles reales desde la base de datos usando solo el ID enviado en el JSON
        if (usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
            List<Rol> rolesRecibidos = new ArrayList<>(usuario.getRoles());
            usuario.getRoles().clear();
            for (Rol r : rolesRecibidos) {
                if (r.getId() != null) {
                    Rol rolPersistido = rolRepository.findById(r.getId())
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + r.getId()));
                    usuario.addRol(rolPersistido);
                }
            }
        }

        if (usuario.getEstado() == null) {
            usuario.setEstado("ACTIVO");
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id " + id));
    }

    public Usuario actualizarUsuario(Long id, Usuario request) {
        Usuario usuario = obtenerPorId(id);

        // Validar si el email ha cambiado y si ya existe
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("El email ya está registrado por otro usuario");
            }
            usuario.setEmail(request.getEmail().trim());
        }

        // Actualización de campos permitidos
        if (request.getEstado() != null && !request.getEstado().isBlank()) {
            usuario.setEstado(request.getEstado().trim().toUpperCase());
        }

        // Actualización de roles (permitiendo enviar solo los IDs en el JSON)
        if (request.getRoles() != null) {
            // Limpiamos los roles actuales y añadimos los nuevos basados en los IDs recibidos
            usuario.getRoles().clear();
            for (Rol r : request.getRoles()) {
                if (r.getId() != null) {
                    Rol rolPersistido = rolRepository.findById(r.getId())
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + r.getId()));
                    usuario.addRol(rolPersistido);
                }
            }
        }

        if (request.getUsername() != null) usuario.setUsername(request.getUsername().trim());

        return usuarioRepository.save(usuario);
    }

    public Usuario cambiarEstado(Long id, String estado) {
        Usuario usuario = obtenerPorId(id);
        usuario.setEstado(estado.trim().toUpperCase());
        return usuarioRepository.save(usuario);
    }

    public void desactivarUsuario(Long id) {
        Usuario usuario = obtenerPorId(id);
        usuario.setEstado("INACTIVO");
        usuarioRepository.save(usuario);
    }
}
