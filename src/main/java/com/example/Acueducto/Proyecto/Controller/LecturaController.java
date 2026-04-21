package com.example.Acueducto.Proyecto.Controller;

import com.example.Acueducto.Proyecto.Model.Lectura;
import com.example.Acueducto.Proyecto.Service.LecturaService;
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
@RequestMapping("/lecturas")
public class LecturaController {

    private final LecturaService lecturaService;

    public LecturaController(LecturaService lecturaService) {
        this.lecturaService = lecturaService;
    }

    @PostMapping
    public ResponseEntity<Lectura> registrar(
            @RequestParam Long medidorId,
            @RequestParam String periodo,
            @RequestParam String fechaLectura,
            @RequestParam BigDecimal lecturaActual,
            @RequestParam(required = false) String observacion,
            @RequestParam Long registradoPorId,
            @RequestParam(required = false) String motivo) {
        LocalDate fecha = LocalDate.parse(fechaLectura);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lecturaService.registrarLectura(medidorId, periodo, fecha, lecturaActual, observacion, registradoPorId, motivo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lectura> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(lecturaService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<Lectura>> listar(
            @RequestParam(required = false) Long medidorId,
            @RequestParam(required = false) String periodo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaLectura") String sort,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        return ResponseEntity.ok(lecturaService.listarLecturas(medidorId, periodo, pageable));
    }
}
