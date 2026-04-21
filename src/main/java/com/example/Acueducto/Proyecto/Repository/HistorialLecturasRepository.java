package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.HistorialLecturas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialLecturasRepository extends JpaRepository<HistorialLecturas, Long> {

    List<HistorialLecturas> findByLecturaIdOrderByFechaEventoDesc(Long lecturaId);
}
