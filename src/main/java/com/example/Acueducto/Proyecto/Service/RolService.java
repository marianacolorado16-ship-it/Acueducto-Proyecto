package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.Rol;
import com.example.Acueducto.Proyecto.Repository.RolRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public Rol crearRol(Rol rol) {
        if (rolRepository.findByNombre(rol.getNombre().trim().toUpperCase()).isPresent()) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + rol.getNombre());
        }
        rol.setNombre(rol.getNombre().trim().toUpperCase());
        return rolRepository.save(rol);
    }

    @Transactional(readOnly = true)
    public Rol obtenerPorId(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id " + id));
    }

    @Transactional(readOnly = true)
    public List<Rol> listarTodo() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Rol> listarPaginado(Pageable pageable) {
        return rolRepository.findAll(pageable);
    }

    public Rol actualizarRol(Long id, Rol request) {
        Rol rol = obtenerPorId(id);
        
        if (request.getNombre() != null && !request.getNombre().equalsIgnoreCase(rol.getNombre())) {
            if (rolRepository.findByNombre(request.getNombre().trim().toUpperCase()).isPresent()) {
                throw new RuntimeException("Ya existe otro rol con ese nombre");
            }
            rol.setNombre(request.getNombre().trim().toUpperCase());
        }
        
        if (request.getDescripcion() != null) rol.setDescripcion(request.getDescripcion().trim());

        return rolRepository.save(rol);
    }
}
