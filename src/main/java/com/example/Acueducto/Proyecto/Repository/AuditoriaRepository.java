package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.AuditoriaEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<AuditoriaEvento, Long> {

    // Find events by type
    Page<AuditoriaEvento> findByTipoOrderByFechaEventoDesc(String tipo, Pageable pageable);

    // Find events by user
    Page<AuditoriaEvento> findByUsuarioIdOrderByFechaEventoDesc(Long usuarioId, Pageable pageable);

    // Find events by referenced entity
    Page<AuditoriaEvento> findByReferenciaIdOrderByFechaEventoDesc(Long referenciaId, Pageable pageable);

    // Find events by type and action
    Page<AuditoriaEvento> findByTipoAndAccionOrderByFechaEventoDesc(String tipo, String accion, Pageable pageable);

    // Find events within date range
    Page<AuditoriaEvento> findByFechaEventoBetweenOrderByFechaEventoDesc(
        LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    // Find events by type, user and date range
    @Query("SELECT ae FROM AuditoriaEvento ae WHERE ae.tipo = :tipo AND ae.usuario.id = :usuarioId " +
           "AND ae.fechaEvento BETWEEN :inicio AND :fin ORDER BY ae.fechaEvento DESC")
    Page<AuditoriaEvento> findByTipoAndUsuarioAndDateRange(
        @Param("tipo") String tipo,
        @Param("usuarioId") Long usuarioId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin,
        Pageable pageable);

    // Find all events by tipo, ordenados por fecha descendente con paginación
    Page<AuditoriaEvento> findAllByTipoOrderByFechaEventoDesc(String tipo, Pageable pageable);

    // Find recent events
    List<AuditoriaEvento> findTop50ByOrderByFechaEventoDesc();
}
