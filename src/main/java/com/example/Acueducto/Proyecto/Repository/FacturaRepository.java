package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.Factura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long>, JpaSpecificationExecutor<Factura> {

    boolean existsByClienteIdAndPeriodo(Long clienteId, String periodo);

    boolean existsByNumeroFactura(String numeroFactura);

    Page<Factura> findByEstado(String estado, Pageable pageable);

    List<Factura> findByFechaVencimientoBeforeAndSaldoPendienteGreaterThan(LocalDate fecha, BigDecimal minimo);
}
