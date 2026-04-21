package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.MovimientoInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    Page<MovimientoInventario> findByInventarioId(Long inventarioId, Pageable pageable);
}