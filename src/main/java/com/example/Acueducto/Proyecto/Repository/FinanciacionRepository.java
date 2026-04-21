package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.Financiacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinanciacionRepository extends JpaRepository<Financiacion, Long> {

    Page<Financiacion> findByClienteId(Long clienteId, Pageable pageable);
}
