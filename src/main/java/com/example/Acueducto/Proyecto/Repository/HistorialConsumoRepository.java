package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.HistorialConsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialConsumoRepository extends JpaRepository<HistorialConsumo, Long> {

    Optional<HistorialConsumo> findByMedidorIdAndPeriodo(Long medidorId, String periodo);

    List<HistorialConsumo> findTop6ByMedidorIdOrderByPeriodoDesc(Long medidorId);
}
