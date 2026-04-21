package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.Turno;
import com.example.Acueducto.Proyecto.Model.Usuario;
import com.example.Acueducto.Proyecto.Repository.TurnoRepository;
import com.example.Acueducto.Proyecto.Repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final UsuarioRepository usuarioRepository;

    public TurnoService(TurnoRepository turnoRepository, UsuarioRepository usuarioRepository) {
        this.turnoRepository = turnoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Turno crearTurno(Turno request) {
        if (request.getOperador() == null || request.getOperador().getId() == null) {
            throw new RuntimeException("El operador es requerido para el turno");
        }

        Usuario operador = usuarioRepository.findById(request.getOperador().getId())
                .orElseThrow(() -> new RuntimeException("Operador no encontrado con id " + request.getOperador().getId()));

        if (!request.getHoraFin().isAfter(request.getHoraInicio())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }

        boolean existeMismoInicio = turnoRepository.existsByOperadorIdAndFechaTurnoAndHoraInicio(
                request.getOperador().getId(), request.getFechaTurno(), request.getHoraInicio());

        if (existeMismoInicio) {
            throw new IllegalArgumentException("Ya existe un turno para el operador con la misma fecha y hora de inicio");
        }

        request.setOperador(operador);
        if (request.getEstado() == null || request.getEstado().isBlank()) {
            request.setEstado("PROGRAMADO");
        } else {
            request.setEstado(request.getEstado().trim().toUpperCase());
        }

        return turnoRepository.save(request);
    }

    @Transactional(readOnly = true)
    public Turno obtenerPorId(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado con id " + id));
    }

    @Transactional(readOnly = true)
    public Page<Turno> listar(Long operadorId, String estado, Pageable pageable) {
        if (operadorId != null) {
            return turnoRepository.findByOperadorId(operadorId, pageable);
        } else if (estado != null && !estado.isBlank()) {
            return turnoRepository.findByEstado(estado.trim().toUpperCase(), pageable);
        } else {
            return turnoRepository.findAll(pageable);
        }
    }
}
