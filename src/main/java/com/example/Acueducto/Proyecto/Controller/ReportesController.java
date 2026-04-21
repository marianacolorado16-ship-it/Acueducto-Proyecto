package com.example.Acueducto.Proyecto.Controller;

import com.example.Acueducto.Proyecto.Service.ReportesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.*;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReportesController {

    private final ReportesService reportesService;

    /**
     * Genera reporte de recaudo mensual
     * GET /api/reportes/recaudo?periodo=2024-01
     */
    @GetMapping("/recaudo")
    public ResponseEntity<List<Map<String, Object>>> generarReporteRecaudoMensual(
            @RequestParam String periodo) {

        YearMonth yearMonth = YearMonth.parse(periodo);
        List<Map<String, Object>> resultado = reportesService.generarReporteRecaudoMensual(yearMonth);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Genera reporte de consumo mensual
     * GET /api/reportes/consumo?periodo=2024-01
     */
    @GetMapping("/consumo")
    public ResponseEntity<List<Map<String, Object>>> generarReporteConsumoMensual(
            @RequestParam String periodo) {

        YearMonth yearMonth = YearMonth.parse(periodo);
        List<Map<String, Object>> resultado = reportesService.generarReporteConsumoMensual(yearMonth);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Genera reporte de cartera envejecida (resumen por rango de días)
     * GET /api/reportes/cartera/envejecida
     */
    @GetMapping("/cartera/envejecida")
    public ResponseEntity<Map<String, Object>> generarReporteCarteraEnvejecida() {
        Map<String, Object> resultado = reportesService.generarReporteCarteraEnvejecida();
        return ResponseEntity.ok(resultado);
    }

    /**
     * Genera lista detallada de facturas vencidas
     * GET /api/reportes/cartera/vencida-detallada
     */
    @GetMapping("/cartera/vencida-detallada")
    public ResponseEntity<List<Map<String, Object>>> generarCarteraVencidaDetallada() {
        List<Map<String, Object>> resultado = reportesService.generarCarteraVencidaDetallada();
        return ResponseEntity.ok(resultado);
    }

    /**
     * Genera reporte de valorización de inventario
     * GET /api/reportes/inventario/valorizacion
     */
    @GetMapping("/inventario/valorizacion")
    public ResponseEntity<Map<String, Object>> generarReporteValorizacionInventario() {
        Map<String, Object> resultado = reportesService.generarReporteValorizacionInventario();
        return ResponseEntity.ok(resultado);
    }

    /**
     * Genera reporte de rotación de inventario
     * GET /api/reportes/inventario/rotacion?periodo=2024-01
     */
    @GetMapping("/inventario/rotacion")
    public ResponseEntity<Map<String, Object>> generarReporteRotacionInventario(
            @RequestParam String periodo) {

        YearMonth yearMonth = YearMonth.parse(periodo);
        Map<String, Object> resultado = reportesService.generarReporteRotacionInventario(yearMonth);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Genera dashboard ejecutivo con métricas clave
     * GET /api/reportes/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> generarDashboardEjecutivo() {
        Map<String, Object> resultado = reportesService.generarDashboardEjecutivo();
        return ResponseEntity.ok(resultado);
    }

    /**
     * Endpoint de salud para verificar que el servicio de reportes está disponible
     * GET /api/reportes/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Servicio de reportes disponible");
    }
}
