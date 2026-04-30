package com.example.Acueducto.Proyecto.Controller;

import com.example.Acueducto.Proyecto.Model.HistorialConsumo;
import com.example.Acueducto.Proyecto.Service.HistorialConsumoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/historial-consumo")
public class HistorialConsumoController {

    private final HistorialConsumoService historialConsumoService;

    public HistorialConsumoController(HistorialConsumoService historialConsumoService) {
        this.historialConsumoService = historialConsumoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialConsumo> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(historialConsumoService.obtenerPorId(id));
    }

    @GetMapping("/medidor/{medidorId}")
    public ResponseEntity<Page<HistorialConsumo>> listarPorMedidor(
            @PathVariable Long medidorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size, // Por defecto 12 meses
            @RequestParam(defaultValue = "periodo") String sort,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        return ResponseEntity.ok(historialConsumoService.listarPorMedidor(medidorId, pageable));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Page<HistorialConsumo>> listarPorCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "periodo") String sort,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        return ResponseEntity.ok(historialConsumoService.listarPorCliente(clienteId, pageable));
    }
}
