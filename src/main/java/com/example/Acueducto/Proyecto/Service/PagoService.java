package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.Factura;
import com.example.Acueducto.Proyecto.Model.HistorialPagos;
import com.example.Acueducto.Proyecto.Model.Pago;
import com.example.Acueducto.Proyecto.Model.Usuario;
import com.example.Acueducto.Proyecto.Repository.DetalleFacturaRepository;
import com.example.Acueducto.Proyecto.Repository.FacturaRepository;
import com.example.Acueducto.Proyecto.Repository.HistorialPagosRepository;
import com.example.Acueducto.Proyecto.Repository.PagoRepository;
import com.example.Acueducto.Proyecto.Repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PagoService {

    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialPagosRepository historialPagosRepository;
    private final DetalleFacturaRepository detalleFacturaRepository;

    public PagoService(PagoRepository pagoRepository,
                       FacturaRepository facturaRepository,
                       UsuarioRepository usuarioRepository,
                       HistorialPagosRepository historialPagosRepository,
                       DetalleFacturaRepository detalleFacturaRepository) {
        this.pagoRepository = pagoRepository;
        this.facturaRepository = facturaRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialPagosRepository = historialPagosRepository;
        this.detalleFacturaRepository = detalleFacturaRepository;
    }

    public Pago registrarPago(Pago request) {
        if (request.getFactura() == null || request.getFactura().getId() == null) {
            throw new RuntimeException("La factura es requerida para el pago");
        }
        if (request.getRegistradoPor() == null || request.getRegistradoPor().getId() == null) {
            throw new RuntimeException("El usuario que registra es requerido");
        }

        Factura factura = facturaRepository.findById(request.getFactura().getId())
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        Usuario usuario = usuarioRepository.findById(request.getRegistradoPor().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        BigDecimal saldoAnterior = factura.getSaldoPendiente().setScale(2, RoundingMode.HALF_UP);
        BigDecimal montoPago = request.getMonto().setScale(2, RoundingMode.HALF_UP);

        request.setMetodoPago(request.getMetodoPago().trim().toUpperCase());

        if (saldoAnterior.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La factura ya está saldada");
        }

        if (montoPago.compareTo(saldoAnterior) > 0) {
            throw new IllegalArgumentException("El pago no puede ser mayor al saldo pendiente de la factura");
        }

        BigDecimal moraActual = factura.getMora().setScale(2, RoundingMode.HALF_UP);
        BigDecimal aplicadoMora = montoPago.min(moraActual);

        BigDecimal consumoPendiente = detalleFacturaRepository.findByFacturaIdOrderByIdAsc(factura.getId()).stream()
            .filter(d -> "CONSUMO".equalsIgnoreCase(d.getTipoConcepto()) || "CARGO_FIJO".equalsIgnoreCase(d.getTipoConcepto()))
            .map(d -> d.getValorTotal() == null ? BigDecimal.ZERO : d.getValorTotal())
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal financiacionPendiente = detalleFacturaRepository.findByFacturaIdOrderByIdAsc(factura.getId()).stream()
            .filter(d -> "FINANCIACION".equalsIgnoreCase(d.getTipoConcepto()))
            .map(d -> d.getValorTotal() == null ? BigDecimal.ZERO : d.getValorTotal())
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal remanente = montoPago.subtract(aplicadoMora);
        BigDecimal aplicadoConsumo = remanente.min(consumoPendiente);
        remanente = remanente.subtract(aplicadoConsumo);
        BigDecimal aplicadoFinanciacion = remanente.min(financiacionPendiente);

        factura.setMora(moraActual.subtract(aplicadoMora).setScale(2, RoundingMode.HALF_UP));
        factura.setSaldoPendiente(saldoAnterior.subtract(montoPago).setScale(2, RoundingMode.HALF_UP));

        if (factura.getSaldoPendiente().compareTo(BigDecimal.ZERO) == 0) {
            factura.setEstado("PAGADA");
        } else {
            factura.setEstado("PARCIAL");
            if (factura.getFechaVencimiento().isBefore(LocalDate.now())) {
                factura.setEstado("VENCIDA");
            }
        }

        facturaRepository.save(factura);

        request.setCliente(factura.getCliente());
        request.setFactura(factura);
        request.setRegistradoPor(usuario);

        Pago pagoGuardado = pagoRepository.save(request);

        String detalle = "Aplicado a mora=" + aplicadoMora
            + ", aplicado a consumo=" + aplicadoConsumo
            + ", aplicado a financiacion=" + aplicadoFinanciacion;

        HistorialPagos historial = HistorialPagos.builder()
                .pago(pagoGuardado)
                .estadoAnterior(saldoAnterior.compareTo(BigDecimal.ZERO) > 0 ? "PENDIENTE" : "PAGADA")
                .estadoNuevo(factura.getEstado())
                .detalle(detalle)
                .usuario(usuario)
                .build();

        historialPagosRepository.save(historial);

        return pagoGuardado;
    }

    @Transactional(readOnly = true)
    public Page<Pago> listarPagos(Long facturaId, Long clienteId, Pageable pageable) {
        if (facturaId != null) {
            return pagoRepository.findByFacturaId(facturaId, pageable);
        } else if (clienteId != null) {
            return pagoRepository.findByClienteId(clienteId, pageable);
        } else {
            return pagoRepository.findAll(pageable);
        }
    }

    @Transactional(readOnly = true)
    public List<Factura> obtenerCarteraVencida() {
        return facturaRepository.findByFechaVencimientoBeforeAndSaldoPendienteGreaterThan(
                LocalDate.now(), BigDecimal.ZERO);
    }
}
