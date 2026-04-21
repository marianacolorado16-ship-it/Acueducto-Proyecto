package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.HistorialPagos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialPagosRepository extends JpaRepository<HistorialPagos, Long> {
}
