package com.example.Acueducto.Proyecto.Controller;

import com.example.Acueducto.Proyecto.Model.MovimientoInventario;
import com.example.Acueducto.Proyecto.Service.MovimientoInventarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movimientos-inventario")
public class MovimientoInventarioController {
    private final MovimientoInventarioService movimientoInventarioService;

    public MovimientoInventarioController(MovimientoInventarioService movimientoInventarioService) {
        this.movimientoInventarioService = movimientoInventarioService;
    }

    @PostMapping
    public ResponseEntity<MovimientoInventario> registrar(@RequestBody MovimientoInventario request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimientoInventarioService.registrarMovimiento(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventario> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoInventarioService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<MovimientoInventario>> listar(
            @RequestParam(required = false) Long inventarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaMovimiento") String sort,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        return ResponseEntity.ok(movimientoInventarioService.listar(inventarioId, pageable));
    }
}
