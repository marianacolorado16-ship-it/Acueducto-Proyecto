package com.example.Acueducto.Proyecto.Controller;

import com.example.Acueducto.Proyecto.Model.HistorialPagos;
import com.example.Acueducto.Proyecto.Service.HistorialPagosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historial-pagos")
public class HistorialPagosController {

    private final HistorialPagosService historialPagosService;

    public HistorialPagosController(HistorialPagosService historialPagosService) {
        this.historialPagosService = historialPagosService;
    }

    /**
     * GET /historial-pagos/pago/{pagoId}
     */
    @GetMapping("/pago/{pagoId}")
    public ResponseEntity<List<HistorialPagos>> listarPorPago(@PathVariable Long pagoId) {
        return ResponseEntity.ok(historialPagosService.listarPorPago(pagoId));
    }

    /**
     * GET /historial-pagos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<HistorialPagos> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(historialPagosService.obtenerPorId(id));
    }
}
