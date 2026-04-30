package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.HistorialPagos;
import com.example.Acueducto.Proyecto.Repository.HistorialPagosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HistorialPagosService {

    private final HistorialPagosRepository historialPagosRepository;

    public HistorialPagosService(HistorialPagosRepository historialPagosRepository) {
        this.historialPagosRepository = historialPagosRepository;
    }

    /**
     * Obtiene la trazabilidad de un pago específico
     */
    @Transactional(readOnly = true)
    public List<HistorialPagos> listarPorPago(Long pagoId) {
        return historialPagosRepository.findByPagoIdOrderByIdDesc(pagoId);
    }

    @Transactional(readOnly = true)
    public HistorialPagos obtenerPorId(Long id) {
        return historialPagosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de historial de pago no encontrado con id " + id));
    }
}
