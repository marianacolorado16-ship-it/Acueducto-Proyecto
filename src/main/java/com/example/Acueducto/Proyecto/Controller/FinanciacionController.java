package com.example.Acueducto.Proyecto.Controller;

import com.example.Acueducto.Proyecto.Model.Financiacion;
import com.example.Acueducto.Proyecto.Service.FinanciacionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/financiaciones")
public class FinanciacionController {

    private final FinanciacionService financiacionService;

    public FinanciacionController(FinanciacionService financiacionService) {
        this.financiacionService = financiacionService;
    }

    @PostMapping
    public ResponseEntity<Financiacion> crear(@RequestBody Financiacion request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financiacionService.crearFinanciacion(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Financiacion> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(financiacionService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<Financiacion>> listar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        return ResponseEntity.ok(financiacionService.listar(clienteId, pageable));
    }
}
