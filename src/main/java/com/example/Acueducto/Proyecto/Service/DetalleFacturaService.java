package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.DetalleFactura;
import com.example.Acueducto.Proyecto.Repository.DetalleFacturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DetalleFacturaService {

    private final DetalleFacturaRepository detalleFacturaRepository;

    public DetalleFacturaService(DetalleFacturaRepository detalleFacturaRepository) {
        this.detalleFacturaRepository = detalleFacturaRepository;
    }

    /**
     * Obtiene todos los detalles asociados a una factura específica
     */
    @Transactional(readOnly = true)
    public List<DetalleFactura> listarPorFactura(Long facturaId) {
        return detalleFacturaRepository.findByFacturaIdOrderByIdAsc(facturaId);
    }

    @Transactional(readOnly = true)
    public DetalleFactura obtenerPorId(Long id) {
        return detalleFacturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de factura no encontrado con id " + id));
    }
}
