package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.CuotaFinanciacion;
import com.example.Acueducto.Proyecto.Repository.CuotaFinanciacionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CuotaFinanciacionService {

    private final CuotaFinanciacionRepository cuotaFinanciacionRepository;

    public CuotaFinanciacionService(CuotaFinanciacionRepository cuotaFinanciacionRepository) {
        this.cuotaFinanciacionRepository = cuotaFinanciacionRepository;
    }

    /**
     * Obtiene una cuota específica por su ID
     */
    @Transactional(readOnly = true)
    public CuotaFinanciacion obtenerPorId(Long id) {
        return cuotaFinanciacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuota de financiación no encontrada con id " + id));
    }

    /**
     * Lista las cuotas, permitiendo filtrar por una financiación específica
     */
    @Transactional(readOnly = true)
    public Page<CuotaFinanciacion> listar(Long financiacionId, Pageable pageable) {
        if (financiacionId != null) {
            return cuotaFinanciacionRepository.findByFinanciacionId(financiacionId, pageable);
        }
        return cuotaFinanciacionRepository.findAll(pageable);
    }
}
