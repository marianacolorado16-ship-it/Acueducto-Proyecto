package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.DetalleFactura;
import com.example.Acueducto.Proyecto.Model.Factura;
import com.example.Acueducto.Proyecto.Model.Lectura;
import com.example.Acueducto.Proyecto.Repository.DetalleFacturaRepository;
import com.example.Acueducto.Proyecto.Repository.FacturaRepository;
import com.example.Acueducto.Proyecto.Repository.LecturaRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class FacturaService {

    private static final String FACTURA_NO_ENCONTRADA = "Factura no encontrada con id ";

    private final FacturaRepository facturaRepository;
    private final DetalleFacturaRepository detalleFacturaRepository;
    private final LecturaRepository lecturaRepository;

    public FacturaService(FacturaRepository facturaRepository,
                          DetalleFacturaRepository detalleFacturaRepository,
                          LecturaRepository lecturaRepository) {
        this.facturaRepository = facturaRepository;
        this.detalleFacturaRepository = detalleFacturaRepository;
        this.lecturaRepository = lecturaRepository;
    }

    public Factura generarFacturaDesdeLectura(Long lecturaId, BigDecimal cargoFijo, BigDecimal tarifaM3, BigDecimal moraInput, LocalDate fechaVencimiento, String observacion) {
        Lectura lectura = lecturaRepository.findById(lecturaId)
                .orElseThrow(() -> new RuntimeException("Lectura no encontrada con id " + lecturaId));

        Long clienteId = lectura.getMedidor().getCliente().getId();
        String periodo = lectura.getPeriodo();

        if (facturaRepository.existsByClienteIdAndPeriodo(clienteId, periodo)) {
            throw new RuntimeException("Ya existe una factura para este cliente en el periodo " + periodo);
        }

        BigDecimal cargoFijoFinal = cargoFijo.setScale(2, RoundingMode.HALF_UP);
        BigDecimal tarifaM3Final = tarifaM3.setScale(2, RoundingMode.HALF_UP);
        BigDecimal mora = (moraInput == null ? BigDecimal.ZERO : moraInput).setScale(2, RoundingMode.HALF_UP);

        BigDecimal consumoM3 = lectura.getConsumoM3();
        BigDecimal valorConsumo = consumoM3.multiply(tarifaM3Final).setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotal = cargoFijoFinal.add(valorConsumo).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(mora).setScale(2, RoundingMode.HALF_UP);

        Factura factura = Factura.builder()
                .cliente(lectura.getMedidor().getCliente())
                .lectura(lectura)
                .numeroFactura(generarNumeroFactura(periodo, clienteId))
                .periodo(periodo)
                .fechaEmision(LocalDate.now())
                .fechaVencimiento(fechaVencimiento)
                .subtotal(subtotal)
                .mora(mora)
                .total(total)
                .saldoPendiente(total)
                .estado("PENDIENTE")
                .observacion(observacion)
                .build();

        Factura facturaGuardada = facturaRepository.save(factura);

        List<DetalleFactura> detalles = List.of(
                DetalleFactura.builder()
                        .factura(facturaGuardada)
                        .tipoConcepto("CARGO_FIJO")
                        .concepto("Cargo fijo del servicio")
                        .cantidad(BigDecimal.ONE.setScale(3, RoundingMode.HALF_UP))
                        .valorUnitario(cargoFijoFinal)
                        .valorTotal(cargoFijoFinal)
                        .build(),
                DetalleFactura.builder()
                        .factura(facturaGuardada)
                        .tipoConcepto("CONSUMO")
                        .concepto("Consumo de agua " + consumoM3 + " m3")
                        .cantidad(consumoM3)
                        .valorUnitario(tarifaM3Final)
                        .valorTotal(valorConsumo)
                        .build(),
                DetalleFactura.builder()
                        .factura(facturaGuardada)
                        .tipoConcepto("MORA")
                        .concepto("Mora acumulada")
                        .cantidad(BigDecimal.ONE.setScale(3, RoundingMode.HALF_UP))
                        .valorUnitario(mora)
                        .valorTotal(mora)
                        .build()
        );

        detalleFacturaRepository.saveAll(detalles);
        return facturaGuardada;
    }

    @Transactional(readOnly = true)
    public Factura obtenerFacturaPorId(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(FACTURA_NO_ENCONTRADA + id));
    }

    @Transactional(readOnly = true)
    public Page<Factura> listarFacturas(Long clienteId, String estado, String periodo, Pageable pageable) {
        Specification<Factura> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (clienteId != null) {
                predicates.add(cb.equal(root.get("cliente").get("id"), clienteId));
            }

            if (estado != null && !estado.isBlank()) {
                predicates.add(cb.equal(root.get("estado"), estado.trim().toUpperCase()));
            }

            if (periodo != null && !periodo.isBlank()) {
                predicates.add(cb.equal(root.get("periodo"), periodo.trim()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return facturaRepository.findAll(spec, pageable);
    }

    private String generarNumeroFactura(String periodo, Long clienteId) {
        String periodoSinGuion = periodo.replace("-", "");
        String candidato;

        do {
            int sufijo = ThreadLocalRandom.current().nextInt(1000, 9999);
            candidato = "FAC-" + periodoSinGuion + "-" + clienteId + "-" + sufijo;
        } while (facturaRepository.existsByNumeroFactura(candidato));

        return candidato;
    }
}
