package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.*;
import com.example.Acueducto.Proyecto.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportesService {

    private final FacturaRepository facturaRepository;
    private final PagoRepository pagoRepository;
    private final LecturaRepository lecturaRepository;
    private final HistorialConsumoRepository historialConsumoRepository;
    private final ClienteRepository clienteRepository;
    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    /**
     * Genera reporte de recaudo por período
     */
    public List<Map<String, Object>> generarReporteRecaudoMensual(YearMonth periodo) {
        String periodoStr = periodo.toString(); // YYYY-MM

        // Obtener todas las facturas del período
        List<Factura> facturas = facturaRepository.findAll()
            .stream()
            .filter(f -> f.getPeriodo().equals(periodoStr))
            .collect(Collectors.toList());

        // Agrupar por cliente
        Map<Cliente, List<Factura>> facturasPorCliente = facturas.stream()
            .collect(Collectors.groupingBy(Factura::getCliente));

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Map.Entry<Cliente, List<Factura>> entry : facturasPorCliente.entrySet()) {
            Cliente cliente = entry.getKey();
            List<Factura> facturasCliente = entry.getValue();

            BigDecimal totalFacturado = facturasCliente.stream()
                .map(Factura::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Obtener pagos realizados en este período para este cliente
            List<Pago> pagosCliente = pagoRepository.findAll()
                .stream()
                .filter(p -> p.getCliente().getId().equals(cliente.getId()))
                .filter(p -> p.getFechaPago().getYear() == periodo.getYear() &&
                             p.getFechaPago().getMonthValue() == periodo.getMonthValue())
                .collect(Collectors.toList());

            BigDecimal totalPagado = pagosCliente.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal saldoPendiente = totalFacturado.subtract(totalPagado);

            Map<String, Object> item = new HashMap<>();
            item.put("clienteId", cliente.getId());
            item.put("clienteNombre", cliente.getNombreCompleto());
            item.put("totalFacturado", totalFacturado);
            item.put("totalPagado", totalPagado);
            item.put("saldoPendiente", saldoPendiente);
            item.put("facturasEmitidas", facturasCliente.size());
            item.put("pagosRealizados", pagosCliente.size());
            resultado.add(item);
        }

        // Ordenar por saldo pendiente descendente
        resultado.sort((a, b) -> ((BigDecimal) b.get("saldoPendiente")).compareTo((BigDecimal) a.get("saldoPendiente")));
        return resultado;
    }

    /**
     * Genera reporte de consumo por período
     */
    public List<Map<String, Object>> generarReporteConsumoMensual(YearMonth periodo) {
        String periodoStr = periodo.toString();

        // Obtener historiales de consumo del período
        List<HistorialConsumo> historialesConsumo = historialConsumoRepository.findAll()
            .stream()
            .filter(hc -> hc.getPeriodo().equals(periodoStr))
            .collect(Collectors.toList());

        List<Map<String, Object>> resultado = historialesConsumo.stream()
            .map(hc -> {
                Medidor medidor = hc.getMedidor();
                Cliente cliente = medidor.getCliente();

                Map<String, Object> item = new HashMap<>();
                item.put("medidorId", medidor.getId());
                item.put("clienteId", cliente.getId());
                item.put("clienteNombre", cliente.getNombreCompleto());
                item.put("numeroMedidor", medidor.getCodigoSerie());
                item.put("consumoM3", hc.getConsumoM3());
                item.put("promedio3Meses", hc.getPromedio3m());
                item.put("promedio6Meses", hc.getPromedio6m());
                return item;
            })
            .collect(Collectors.toList());

        resultado.sort((a, b) -> ((BigDecimal) b.get("consumoM3")).compareTo((BigDecimal) a.get("consumoM3")));
        return resultado;
    }

    /**
     * Genera reporte de cartera envejecida
     */
    public Map<String, Object> generarReporteCarteraEnvejecida() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDate hoy = ahora.toLocalDate();
        List<Factura> facturasVencidas = facturaRepository.findAll()
            .stream()
            .filter(f -> f.getFechaVencimiento().isBefore(hoy) && 
                        f.getSaldoPendiente().compareTo(BigDecimal.ZERO) > 0)
            .collect(Collectors.toList());

        int cuentas0_30 = 0;
        BigDecimal monto0_30 = BigDecimal.ZERO;

        int cuentas30_60 = 0;
        BigDecimal monto30_60 = BigDecimal.ZERO;

        int cuentas60_90 = 0;
        BigDecimal monto60_90 = BigDecimal.ZERO;

        int cuentas90plus = 0;
        BigDecimal monto90plus = BigDecimal.ZERO;

        for (Factura factura : facturasVencidas) {
            long diasVencido = ChronoUnit.DAYS.between(factura.getFechaVencimiento(), hoy);

            if (diasVencido <= 30) {
                cuentas0_30++;
                monto0_30 = monto0_30.add(factura.getSaldoPendiente());
            } else if (diasVencido <= 60) {
                cuentas30_60++;
                monto30_60 = monto30_60.add(factura.getSaldoPendiente());
            } else if (diasVencido <= 90) {
                cuentas60_90++;
                monto60_90 = monto60_90.add(factura.getSaldoPendiente());
            } else {
                cuentas90plus++;
                monto90plus = monto90plus.add(factura.getSaldoPendiente());
            }
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("cuentas0_30Dias", cuentas0_30);
        resultado.put("monto0_30Dias", monto0_30);
        resultado.put("cuentas30_60Dias", cuentas30_60);
        resultado.put("monto30_60Dias", monto30_60);
        resultado.put("cuentas60_90Dias", cuentas60_90);
        resultado.put("monto60_90Dias", monto60_90);
        resultado.put("cuentas90PlusDias", cuentas90plus);
        resultado.put("monto90PlusDias", monto90plus);
        resultado.put("totalCuentasVencidas", facturasVencidas.size());
        resultado.put("totalMontoVencido", monto0_30.add(monto30_60).add(monto60_90).add(monto90plus));
        return resultado;
    }

    /**
     * Genera lista detallada de facturas vencidas
     */
    public List<Map<String, Object>> generarCarteraVencidaDetallada() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDate hoy = ahora.toLocalDate();

        return facturaRepository.findAll()
            .stream()
            .filter(f -> f.getFechaVencimiento().isBefore(hoy) && 
                        f.getSaldoPendiente().compareTo(BigDecimal.ZERO) > 0)
            .map(f -> {
                long diasVencido = ChronoUnit.DAYS.between(f.getFechaVencimiento(), hoy);
                Map<String, Object> item = new HashMap<>();
                item.put("facturaId", f.getId());
                item.put("numeroFactura", f.getNumeroFactura());
                item.put("clienteId", f.getCliente().getId());
                item.put("clienteNombre", f.getCliente().getNombreCompleto());
                item.put("saldoPendiente", f.getSaldoPendiente());
                item.put("periodo", f.getPeriodo());
                item.put("fechaVencimiento", f.getFechaVencimiento());
                item.put("diasVencido", (int) diasVencido);
                return item;
            })
            .sorted((a, b) -> ((Integer) b.get("diasVencido")).compareTo((Integer) a.get("diasVencido")))
            .collect(Collectors.toList());
    }

    /**
     * Genera reporte de valorización de inventario
     */
    public Map<String, Object> generarReporteValorizacionInventario() {
        List<Inventario> items = inventarioRepository.findAll()
            .stream()
            .filter(i -> "ACTIVO".equals(i.getEstado()))
            .collect(Collectors.toList());

        BigDecimal valorTotalInventario = BigDecimal.ZERO;
        List<Map<String, Object>> itemsList = new ArrayList<>();
        int itemsBajoStock = 0;

        for (Inventario item : items) {
            BigDecimal valorTotal = item.getCantidadActual()
                .multiply(item.getCostoPromedio())
                .setScale(2, RoundingMode.HALF_UP);

            valorTotalInventario = valorTotalInventario.add(valorTotal);

            boolean bajoStock = item.getCantidadActual().compareTo(item.getStockMinimo()) <= 0;
            if (bajoStock) {
                itemsBajoStock++;
            }

            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("inventarioId", item.getId());
            itemMap.put("codigoItem", item.getCodigoItem());
            itemMap.put("nombre", item.getNombre());
            itemMap.put("categoria", item.getCategoria());
            itemMap.put("cantidadActual", item.getCantidadActual());
            itemMap.put("costoPromedio", item.getCostoPromedio());
            itemMap.put("valorTotal", valorTotal);
            itemMap.put("bajoStock", bajoStock);
            itemMap.put("stockMinimo", item.getStockMinimo());
            itemsList.add(itemMap);
        }

        itemsList.sort((a, b) -> ((BigDecimal) b.get("valorTotal")).compareTo((BigDecimal) a.get("valorTotal")));

        Map<String, Object> response = new HashMap<>();
        response.put("valorTotalInventario", valorTotalInventario);
        response.put("itemsActivos", items.size());
        response.put("itemsBajoStock", itemsBajoStock);
        response.put("items", itemsList);
        return response;
    }

    /**
     * Genera reporte de rotación de inventario
     */
    public Map<String, Object> generarReporteRotacionInventario(YearMonth periodo) {
        LocalDate inicioMes = periodo.atDay(1);
        LocalDate finMes = periodo.atEndOfMonth();
        LocalDateTime inicioDateTime = inicioMes.atStartOfDay();
        LocalDateTime finDateTime = finMes.atTime(23, 59, 59);

        List<MovimientoInventario> movimientos = movimientoInventarioRepository.findAll()
            .stream()
            .filter(m -> !m.getFechaMovimiento().isBefore(inicioDateTime) &&
                        !m.getFechaMovimiento().isAfter(finDateTime))
            .collect(Collectors.toList());

        Map<Inventario, List<MovimientoInventario>> movimientosPorInventario = movimientos.stream()
            .collect(Collectors.groupingBy(MovimientoInventario::getInventario));

        List<Map<String, Object>> itemsList = new ArrayList<>();
        int totalMovimientos = 0;

        for (Map.Entry<Inventario, List<MovimientoInventario>> entry : movimientosPorInventario.entrySet()) {
            Inventario inventario = entry.getKey();
            List<MovimientoInventario> movs = entry.getValue();

            long entradas = movs.stream()
                .filter(m -> "ENTRADA".equals(m.getTipoMovimiento()))
                .count();

            long salidas = movs.stream()
                .filter(m -> "SALIDA".equals(m.getTipoMovimiento()))
                .count();

            BigDecimal tasaRotacion = BigDecimal.ZERO;
            if (inventario.getCantidadActual().compareTo(BigDecimal.ZERO) > 0) {
                tasaRotacion = BigDecimal.valueOf(salidas)
                    .divide(inventario.getCantidadActual(), 2, RoundingMode.HALF_UP);
            }

            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("inventarioId", inventario.getId());
            itemMap.put("codigoItem", inventario.getCodigoItem());
            itemMap.put("nombre", inventario.getNombre());
            itemMap.put("movimientosEntrada", (int) entradas);
            itemMap.put("movimientosSalida", (int) salidas);
            itemMap.put("totalMovimientos", (int) (entradas + salidas));
            itemMap.put("tasaRotacion", tasaRotacion);
            itemsList.add(itemMap);

            totalMovimientos += (entradas + salidas);
        }

        itemsList.sort((a, b) -> ((Integer) b.get("totalMovimientos")).compareTo((Integer) a.get("totalMovimientos")));

        Map<String, Object> response = new HashMap<>();
        response.put("periodo", periodo.toString());
        response.put("totalMovimientos", totalMovimientos);
        response.put("items", itemsList);
        return response;
    }

    /**
     * Genera dashboard ejecutivo con métricas clave
     */
    public Map<String, Object> generarDashboardEjecutivo() {
        YearMonth mesActual = YearMonth.now();

        // Recaudo del mes actual
        BigDecimal recaudoMes = pagoRepository.findAll()
            .stream()
            .filter(p -> p.getFechaPago().getYear() == mesActual.getYear() &&
                        p.getFechaPago().getMonthValue() == mesActual.getMonthValue())
            .map(Pago::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Cartera pendiente total
        BigDecimal carteraPendiente = facturaRepository.findAll()
            .stream()
            .filter(f -> f.getSaldoPendiente().compareTo(BigDecimal.ZERO) > 0)
            .map(Factura::getSaldoPendiente)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Clientes activos
        long clientesActivos = clienteRepository.findAll()
            .stream()
            .filter(c -> "ACTIVO".equals(c.getEstado()))
            .count();

        // Consumo promedio
        BigDecimal consumoPromedio = historialConsumoRepository.findAll()
            .stream()
            .map(HistorialConsumo::getConsumoM3)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(Math.max(historialConsumoRepository.findAll().size(), 1)),
                   2, RoundingMode.HALF_UP);

        // Inventario bajo stock
        long itemsBajoStock = inventarioRepository.findAll()
            .stream()
            .filter(i -> "ACTIVO".equals(i.getEstado()) && 
                        i.getCantidadActual().compareTo(i.getStockMinimo()) <= 0)
            .count();

        // Valor total inventario
        BigDecimal valorInventario = inventarioRepository.findAll()
            .stream()
            .filter(i -> "ACTIVO".equals(i.getEstado()))
            .map(i -> i.getCantidadActual().multiply(i.getCostoPromedio()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("recaudoMes", recaudoMes);
        dashboard.put("carteraPendiente", carteraPendiente);
        dashboard.put("clientesActivos", (int) clientesActivos);
        dashboard.put("consumoPromedio", consumoPromedio);
        dashboard.put("inventarioItemsBajoStock", (int) itemsBajoStock);
        dashboard.put("valorInventario", valorInventario);
        return dashboard;
    }
}
