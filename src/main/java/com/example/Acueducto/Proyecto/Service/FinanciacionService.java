package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.Cliente;
import com.example.Acueducto.Proyecto.Model.Financiacion;
import com.example.Acueducto.Proyecto.Repository.ClienteRepository;
import com.example.Acueducto.Proyecto.Repository.FinanciacionRepository;
import com.example.Acueducto.Proyecto.Model.CuotaFinanciacion;
import com.example.Acueducto.Proyecto.Repository.CuotaFinanciacionRepository;
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
public class FinanciacionService {

    private final FinanciacionRepository financiacionRepository;
    private final CuotaFinanciacionRepository cuotaFinanciacionRepository;
    private final ClienteRepository clienteRepository;

    public FinanciacionService(FinanciacionRepository financiacionRepository,
                               CuotaFinanciacionRepository cuotaFinanciacionRepository,
                               ClienteRepository clienteRepository) {
        this.financiacionRepository = financiacionRepository;
        this.cuotaFinanciacionRepository = cuotaFinanciacionRepository;
        this.clienteRepository = clienteRepository;
    }

    public Financiacion crearFinanciacion(Financiacion request) {
        if (request.getCliente() == null || request.getCliente().getId() == null) {
            throw new RuntimeException("El cliente es requerido para la financiación");
        }

        Cliente cliente = clienteRepository.findById(request.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id " + request.getCliente().getId()));

        BigDecimal montoInicial = request.getMontoInicial().setScale(2, RoundingMode.HALF_UP);
        BigDecimal interesMensual = request.getInteresMensual().setScale(4, RoundingMode.HALF_UP);

        BigDecimal factorInteres = BigDecimal.ONE.add(interesMensual.multiply(BigDecimal.valueOf(request.getNumeroCuotas())));
        BigDecimal montoFinanciado = montoInicial.multiply(factorInteres).setScale(2, RoundingMode.HALF_UP);

        request.setCliente(cliente);
        request.setMontoInicial(montoInicial);
        request.setSaldoActual(montoFinanciado);
        request.setTipo(request.getTipo().trim().toUpperCase());
        if (request.getEstado() == null) {
            request.setEstado("ACTIVA");
        }

        Financiacion financiacionGuardada = financiacionRepository.save(request);

        BigDecimal cuotaBase = montoFinanciado.divide(BigDecimal.valueOf(request.getNumeroCuotas()), 2, RoundingMode.HALF_UP);
        BigDecimal acumulado = BigDecimal.ZERO;

        List<CuotaFinanciacion> cuotas = new ArrayList<>();
        LocalDate fechaCuota = request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now();

        for (int i = 1; i <= request.getNumeroCuotas(); i++) {
            BigDecimal valorCuota = cuotaBase;
            if (i == request.getNumeroCuotas()) {
                valorCuota = montoFinanciado.subtract(acumulado).setScale(2, RoundingMode.HALF_UP);
            }
            acumulado = acumulado.add(valorCuota);

            cuotas.add(CuotaFinanciacion.builder()
                    .financiacion(financiacionGuardada)
                    .numeroCuota(i)
                    .fechaVencimiento(fechaCuota.plusMonths(i - 1L))
                    .valorCuota(valorCuota)
                    .valorPagado(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                    .saldoCuota(valorCuota)
                    .estado("PENDIENTE")
                    .build());
        }

        cuotaFinanciacionRepository.saveAll(cuotas);
        return financiacionGuardada;
    }

    @Transactional(readOnly = true)
    public Financiacion obtenerPorId(Long id) {
        return financiacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Financiación no encontrada con id " + id));
    }

    @Transactional(readOnly = true)
    public Page<Financiacion> listar(Long clienteId, Pageable pageable) {
        return clienteId == null
                ? financiacionRepository.findAll(pageable)
                : financiacionRepository.findByClienteId(clienteId, pageable);
    }
}
