package com.example.Acueducto.Proyecto.Controller;

import com.example.Acueducto.Proyecto.Model.Medidor;
import com.example.Acueducto.Proyecto.Service.MedidorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medidores")
public class MedidiorController {

    private final MedidorService medidorService;

    public MedidiorController(MedidorService medidorService) {
        this.medidorService = medidorService;
    }

    @PostMapping
    public ResponseEntity<Medidor> crear(@RequestBody Medidor medidor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medidorService.crearMedidor(medidor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medidor> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medidorService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<Medidor>> listar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir
    ) {
        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        return ResponseEntity.ok(medidorService.listar(clienteId, estado, pageable));
    }
}
