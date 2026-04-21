package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.HistorialConsumo;
import com.example.Acueducto.Proyecto.Model.HistorialLecturas;
import com.example.Acueducto.Proyecto.Model.Lectura;
import com.example.Acueducto.Proyecto.Model.Medidor;
import com.example.Acueducto.Proyecto.Model.Usuario;
import com.example.Acueducto.Proyecto.Repository.HistorialConsumoRepository;
import com.example.Acueducto.Proyecto.Repository.HistorialLecturasRepository;
import com.example.Acueducto.Proyecto.Repository.LecturaRepository;
import com.example.Acueducto.Proyecto.Repository.MedidorRepository;
import com.example.Acueducto.Proyecto.Repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class LecturaService {

    private static final String LECTURA_NO_ENCONTRADA = "Lectura no encontrada con id ";

    private final LecturaRepository lecturaRepository;
    private final MedidorRepository medidorRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialConsumoRepository historialConsumoRepository;
    private final HistorialLecturasRepository historialLecturasRepository;

    public LecturaService(LecturaRepository lecturaRepository,
                          MedidorRepository medidorRepository,
                          UsuarioRepository usuarioRepository,
                          HistorialConsumoRepository historialConsumoRepository,
                          HistorialLecturasRepository historialLecturasRepository) {
        this.lecturaRepository = lecturaRepository;
        this.medidorRepository = medidorRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialConsumoRepository = historialConsumoRepository;
        this.historialLecturasRepository = historialLecturasRepository;
    }

    public Lectura registrarLectura(Long medidorId, String periodo, LocalDate fechaLectura, BigDecimal lecturaActualInput, String observacion, Long registradoPorId, String motivo) {
        if (lecturaRepository.existsByMedidorIdAndPeriodo(medidorId, periodo)) {
            throw new RuntimeException("Ya existe una lectura para ese medidor y periodo");
        }

        Medidor medidor = medidorRepository.findById(medidorId)
                .orElseThrow(() -> new RuntimeException("Medidor no encontrado con id " + medidorId));

        Usuario usuario = usuarioRepository.findById(registradoPorId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id " + registradoPorId));

        BigDecimal lecturaAnterior = lecturaRepository
                .findTopByMedidorIdOrderByFechaLecturaDescIdDesc(medidor.getId())
                .map(Lectura::getLecturaActual)
                .orElse(medidor.getLecturaInicial());

        BigDecimal lecturaActual = lecturaActualInput.setScale(3, RoundingMode.HALF_UP);

        if (lecturaActual.compareTo(lecturaAnterior) < 0) {
            throw new IllegalArgumentException("La lectura actual no puede ser menor que la lectura anterior");
        }

        BigDecimal consumo = lecturaActual.subtract(lecturaAnterior).setScale(3, RoundingMode.HALF_UP);

        Lectura lectura = Lectura.builder()
                .medidor(medidor)
                .periodo(periodo)
                .fechaLectura(fechaLectura)
                .lecturaAnterior(lecturaAnterior)
                .lecturaActual(lecturaActual)
                .consumoM3(consumo)
                .observacion(observacion)
                .registradoPor(usuario)
                .build();

        Lectura lecturaGuardada = lecturaRepository.save(lectura);

        List<HistorialConsumo> ultimosConsumos = historialConsumoRepository
                .findTop6ByMedidorIdOrderByPeriodoDesc(medidor.getId());

        BigDecimal promedio3m = calcularPromedio(ultimosConsumos, consumo, 3);
        BigDecimal promedio6m = calcularPromedio(ultimosConsumos, consumo, 6);

        HistorialConsumo historialConsumo = HistorialConsumo.builder()
                .cliente(medidor.getCliente())
                .medidor(medidor)
                .periodo(periodo)
                .consumoM3(consumo)
                .promedio3m(promedio3m)
                .promedio6m(promedio6m)
                .build();

        historialConsumoRepository.save(historialConsumo);

        HistorialLecturas historialLecturas = HistorialLecturas.builder()
                .lectura(lecturaGuardada)
                .accion("CREACION")
                .valorAnterior(lecturaAnterior)
                .valorNuevo(lecturaActual)
                .motivo(motivo == null || motivo.isBlank()
                        ? "Registro inicial del periodo"
                        : motivo)
                .usuario(usuario)
                .build();

        historialLecturasRepository.save(historialLecturas);

        return lecturaGuardada;
    }

    @Transactional(readOnly = true)
    public Lectura obtenerPorId(Long id) {
        return lecturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(LECTURA_NO_ENCONTRADA + id));
    }

    @Transactional(readOnly = true)
    public Page<Lectura> listarLecturas(Long medidorId, String periodo, Pageable pageable) {
        if (medidorId != null) {
            return lecturaRepository.findByMedidorId(medidorId, pageable);
        } else if (periodo != null && !periodo.isBlank()) {
            return lecturaRepository.findByPeriodo(periodo, pageable);
        } else {
            return lecturaRepository.findAll(pageable);
        }
    }

    private BigDecimal calcularPromedio(List<HistorialConsumo> historiales, BigDecimal consumoActual, int cantidad) {
        List<BigDecimal> valores = new ArrayList<>();
        valores.add(consumoActual);

        for (HistorialConsumo historial : historiales) {
            if (valores.size() >= cantidad) {
                break;
            }
            valores.add(historial.getConsumoM3());
        }

        BigDecimal suma = valores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return suma.divide(BigDecimal.valueOf(valores.size()), 3, RoundingMode.HALF_UP);
    }
}
