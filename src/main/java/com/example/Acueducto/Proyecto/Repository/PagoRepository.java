package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Page<Pago> findByClienteId(Long clienteId, Pageable pageable);

    Page<Pago> findByFacturaId(Long facturaId, Pageable pageable);
}