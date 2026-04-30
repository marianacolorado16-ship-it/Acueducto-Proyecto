package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.HistorialConsumo;
import com.example.Acueducto.Proyecto.Repository.HistorialConsumoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HistorialConsumoService {

    private final HistorialConsumoRepository historialConsumoRepository;

    public HistorialConsumoService(HistorialConsumoRepository historialConsumoRepository) {
        this.historialConsumoRepository = historialConsumoRepository;
    }

    /**
     * Obtiene el historial paginado para un medidor específico, ordenado usualmente por periodo
     */
    @Transactional(readOnly = true)
    public Page<HistorialConsumo> listarPorMedidor(Long medidorId, Pageable pageable) {
        return historialConsumoRepository.findByMedidorIdOrderByPeriodoDesc(medidorId, pageable);
    }

    /**
     * Obtiene el historial paginado para un cliente específico
     */
    @Transactional(readOnly = true)
    public Page<HistorialConsumo> listarPorCliente(Long clienteId, Pageable pageable) {
        return historialConsumoRepository.findByClienteIdOrderByPeriodoDesc(clienteId, pageable);
    }

    @Transactional(readOnly = true)
    public HistorialConsumo obtenerPorId(Long id) {
        return historialConsumoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de historial de consumo no encontrado con id " + id));
    }
}
