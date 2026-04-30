package com.example.Acueducto.Proyecto.Controller;

import com.example.Acueducto.Proyecto.Model.DetalleFactura;
import com.example.Acueducto.Proyecto.Service.DetalleFacturaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalles-factura")
public class DetalleFacturaController {

    private final DetalleFacturaService detalleFacturaService;

    public DetalleFacturaController(DetalleFacturaService detalleFacturaService) {
        this.detalleFacturaService = detalleFacturaService;
    }

    /**
     * GET /detalles-factura/factura/{facturaId}
     */
    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<List<DetalleFactura>> listarPorFactura(@PathVariable Long facturaId) {
        return ResponseEntity.ok(detalleFacturaService.listarPorFactura(facturaId));
    }

    /**
     * GET /detalles-factura/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DetalleFactura> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(detalleFacturaService.obtenerPorId(id));
    }
}
