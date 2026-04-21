package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.CuotaFinanciacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuotaFinanciacionRepository extends JpaRepository<CuotaFinanciacion, Long> {

    List<CuotaFinanciacion> findByFinanciacionIdOrderByNumeroCuotaAsc(Long financiacionId);
}
