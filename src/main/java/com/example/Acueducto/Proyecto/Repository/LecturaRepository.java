package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.Lectura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LecturaRepository extends JpaRepository<Lectura, Long> {

    boolean existsByMedidorIdAndPeriodo(Long medidorId, String periodo);

    Optional<Lectura> findTopByMedidorIdOrderByFechaLecturaDescIdDesc(Long medidorId);

    Page<Lectura> findByMedidorId(Long medidorId, Pageable pageable);

    Page<Lectura> findByPeriodo(String periodo, Pageable pageable);
}
