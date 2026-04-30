package com.example.Acueducto.Proyecto.Controller;

import com.example.Acueducto.Proyecto.Model.CuotaFinanciacion;
import com.example.Acueducto.Proyecto.Service.CuotaFinanciacionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuotas-financiacion")
public class CuotaFinanciacionController {

    private final CuotaFinanciacionService cuotaFinanciacionService;

    public CuotaFinanciacionController(CuotaFinanciacionService cuotaFinanciacionService) {
        this.cuotaFinanciacionService = cuotaFinanciacionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuotaFinanciacion> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cuotaFinanciacionService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<CuotaFinanciacion>> listar(
            @RequestParam(required = false) Long financiacionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "numeroCuota") String sort,
            @RequestParam(defaultValue = "asc") String dir
    ) {
        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        return ResponseEntity.ok(cuotaFinanciacionService.listar(financiacionId, pageable));
    }
}
