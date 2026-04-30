package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.HistorialPagos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialPagosRepository extends JpaRepository<HistorialPagos, Long> {

    /**
     * Busca la trazabilidad de un pago específico ordenando por el ID más reciente
     */
    List<HistorialPagos> findByPagoIdOrderByIdDesc(Long pagoId);

}