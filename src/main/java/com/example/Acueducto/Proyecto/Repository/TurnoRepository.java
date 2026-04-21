package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.Turno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    Page<Turno> findByOperadorId(Long operadorId, Pageable pageable);

    Page<Turno> findByEstado(String estado, Pageable pageable);

    boolean existsByOperadorIdAndFechaTurnoAndHoraInicio(Long operadorId, LocalDate fechaTurno, java.time.LocalTime horaInicio);
}