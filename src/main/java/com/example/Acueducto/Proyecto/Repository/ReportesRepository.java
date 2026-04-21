package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.Reportes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportesRepository extends JpaRepository<Reportes, Long> {
    
    /**
     * Obtiene el historial de reportes generados por un usuario específico
     */
    Page<Reportes> findByGeneradoPorIdOrderByFechaGeneracionDesc(Long usuarioId, Pageable pageable);

    /**
     * Busca reportes por tipo (RECAUDO, CONSUMO, etc.)
     */
    List<Reportes> findByTipoOrderByFechaGeneracionDesc(String tipo);

    /**
     * Ejemplo de consulta nativa para optimizar el reporte de recaudo 
     * que actualmente se procesa en memoria en el Service.
     */
    @Query(value = "SELECT f.periodo, SUM(f.total) as total_facturado, SUM(f.total - f.saldo_pendiente) as total_pagado " +
                   "FROM facturas f WHERE f.periodo = :periodo GROUP BY f.periodo", nativeQuery = true)
    List<Object[]> obtenerResumenFinancieroPeriodo(@Param("periodo") String periodo);
}
