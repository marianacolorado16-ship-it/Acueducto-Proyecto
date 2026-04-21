package com.example.Acueducto.Proyecto.Controller;

import com.example.Acueducto.Proyecto.Model.Factura;
import com.example.Acueducto.Proyecto.Service.FacturaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @PostMapping("/generar")
    public ResponseEntity<Factura> generar(
            @RequestParam Long lecturaId,
            @RequestParam BigDecimal cargoFijo,
            @RequestParam BigDecimal tarifaM3,
            @RequestParam(required = false) BigDecimal mora,
            @RequestParam String fechaVencimiento,
            @RequestParam(required = false) String observacion) {
        LocalDate vencimiento = LocalDate.parse(fechaVencimiento);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(facturaService.generarFacturaDesdeLectura(lecturaId, cargoFijo, tarifaM3, mora, vencimiento, observacion));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.obtenerFacturaPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<Factura>> listar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String periodo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaEmision") String sort,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        return ResponseEntity.ok(facturaService.listarFacturas(clienteId, estado, periodo, pageable));
    }
}
