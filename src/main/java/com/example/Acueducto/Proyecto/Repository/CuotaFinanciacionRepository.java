package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.CuotaFinanciacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuotaFinanciacionRepository extends JpaRepository<CuotaFinanciacion, Long> {
    
    Page<CuotaFinanciacion> findByFinanciacionId(Long financiacionId, Pageable pageable);
    
}