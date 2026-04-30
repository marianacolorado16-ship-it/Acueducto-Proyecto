package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.HistorialLecturas;
import com.example.Acueducto.Proyecto.Repository.HistorialLecturasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HistorialLecturasService {

    private final HistorialLecturasRepository historialLecturasRepository;

    public HistorialLecturasService(HistorialLecturasRepository historialLecturasRepository) {
        this.historialLecturasRepository = historialLecturasRepository;
    }

    /**
     * Obtiene todos los eventos de historial asociados a una lectura específica
     */
    @Transactional(readOnly = true)
    public List<HistorialLecturas> listarPorLectura(Long lecturaId) {
        return historialLecturasRepository.findByLecturaIdOrderByFechaEventoDesc(lecturaId);
    }

    @Transactional(readOnly = true)
    public HistorialLecturas obtenerPorId(Long id) {
        return historialLecturasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de historial de lectura no encontrado con id " + id));
    }
}
