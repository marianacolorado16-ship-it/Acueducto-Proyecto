package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.Medidor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedidorRepository extends JpaRepository<Medidor, Long> {
    boolean existsByCodigoSerie(String codigoSerie);
    Page<Medidor> findByClienteId(Long clienteId, Pageable pageable);
    Page<Medidor> findByEstado(String estado, Pageable pageable);
}