package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.HistorialConsumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialConsumoRepository extends JpaRepository<HistorialConsumo, Long> {

    Page<HistorialConsumo> findByMedidorIdOrderByPeriodoDesc(Long medidorId, Pageable pageable);

    Page<HistorialConsumo> findByClienteIdOrderByPeriodoDesc(Long clienteId, Pageable pageable);

    List<HistorialConsumo> findTop6ByMedidorIdOrderByPeriodoDesc(Long medidorId);
}