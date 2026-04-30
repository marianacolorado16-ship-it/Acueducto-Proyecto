package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.AuditoriaEvento;
import com.example.Acueducto.Proyecto.Model.Usuario;
import com.example.Acueducto.Proyecto.Repository.AuditoriaRepository;
import com.example.Acueducto.Proyecto.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Registra un evento de auditoría
     */
    public AuditoriaEvento registrarEvento(String tipo, String accion, Long referenciaId,
                                          Long usuarioId, String detalle, String valorAnterior,
                                          String valorNuevo, String ipAddress) {
        Usuario usuario = null;
        if (usuarioId != null) {
            usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }

        AuditoriaEvento evento = AuditoriaEvento.builder()
            .tipo(tipo)
            .accion(accion)
            .referenciaId(referenciaId)
            .usuario(usuario)
            .detalle(detalle)
            .valorAnterior(valorAnterior)
            .valorNuevo(valorNuevo)
            .ipAddress(ipAddress)
            .build();

        return auditoriaRepository.save(evento);
    }

    /**
     * Registra un evento simplificado sin valores anteriores/nuevos
     */
    public AuditoriaEvento registrarEvento(String tipo, String accion, Long referenciaId,
                                          Long usuarioId, String detalle) {
        return registrarEvento(tipo, accion, referenciaId, usuarioId, detalle, null, null, null);
    }

    /**
     * Obtiene la historia de auditoría para una entidad específica
     */
    public Page<AuditoriaEvento> obtenerHistoriaEntidad(Long referenciaId, Pageable pageable) {
        return auditoriaRepository
            .findByReferenciaIdOrderByFechaEventoDesc(referenciaId, pageable);
    }

    /**
     * Obtiene eventos de auditoría por tipo
     */
    public Page<AuditoriaEvento> obtenerEventosPorTipo(String tipo, Pageable pageable) {
        return auditoriaRepository
            .findByTipoOrderByFechaEventoDesc(tipo, pageable);
    }

    /**
     * Obtiene eventos de auditoría por usuario
     */
    public Page<AuditoriaEvento> obtenerEventosPorUsuario(Long usuarioId, Pageable pageable) {
        return auditoriaRepository
            .findByUsuarioIdOrderByFechaEventoDesc(usuarioId, pageable);
    }

    /**
     * Obtiene eventos por tipo y acción
     */
    public Page<AuditoriaEvento> obtenerEventosPorTipoYAccion(String tipo, String accion, Pageable pageable) {
        return auditoriaRepository
            .findByTipoAndAccionOrderByFechaEventoDesc(tipo, accion, pageable);
    }

    public Page<AuditoriaEvento> obtenerTodos(Pageable pageable) {
        return auditoriaRepository.findAll(pageable);
    }

    public AuditoriaEvento crearEvento(AuditoriaEvento evento) {
        return auditoriaRepository.save(evento);
    }

    public AuditoriaEvento obtenerPorId(Long id) {
        return auditoriaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Evento de auditoría no encontrado"));
    }

    public AuditoriaEvento actualizarEvento(Long id, AuditoriaEvento evento) {
        AuditoriaEvento existente = obtenerPorId(id);
        evento.setId(existente.getId());
        if (evento.getFechaEvento() == null) {
            evento.setFechaEvento(existente.getFechaEvento());
        }
        return auditoriaRepository.save(evento);
    }

    public void eliminarEvento(Long id) {
        if (!auditoriaRepository.existsById(id)) {
            throw new RuntimeException("Evento de auditoría no encontrado");
        }
        auditoriaRepository.deleteById(id);
    }

    /**
     * Obtiene eventos en un rango de fechas
     */
    public Page<AuditoriaEvento> obtenerEventosPorRangoFechas(LocalDateTime inicio, LocalDateTime fin, Pageable pageable) {
        return auditoriaRepository
            .findByFechaEventoBetweenOrderByFechaEventoDesc(inicio, fin, pageable);
    }

    /**
     * Obtiene eventos recientes (últimos 50)
     */
    public List<AuditoriaEvento> obtenerEventosRecientes() {
        return auditoriaRepository.findTop50ByOrderByFechaEventoDesc();
    }

    /**
     * Obtiene resumen de eventos por tipo en el mes actual
     */
    public List<Object[]> obtenerResumenEventosMes(YearMonth mes) {
        LocalDateTime inicio = mes.atDay(1).atStartOfDay();
        LocalDateTime fin = mes.atEndOfMonth().atTime(23, 59, 59);

        List<AuditoriaEvento> eventos = auditoriaRepository
            .findByFechaEventoBetweenOrderByFechaEventoDesc(inicio, fin, Pageable.unpaged())
            .getContent();

        return eventos.stream()
            .collect(Collectors.groupingBy(AuditoriaEvento::getTipo, Collectors.counting()))
            .entrySet()
            .stream()
            .map(entry -> new Object[]{entry.getKey(), entry.getValue()})
            .collect(Collectors.toList());
    }
}
