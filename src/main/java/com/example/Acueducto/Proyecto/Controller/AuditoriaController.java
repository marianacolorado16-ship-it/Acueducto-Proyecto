package com.example.Acueducto.Proyecto.Controller;

import com.example.Acueducto.Proyecto.Model.AuditoriaEvento;
import com.example.Acueducto.Proyecto.Service.AuditoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auditoria")
@CrossOrigin(origins = "*")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @PostMapping
    public ResponseEntity<AuditoriaEvento> crear(@RequestBody AuditoriaEvento evento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auditoriaService.crearEvento(evento));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditoriaEvento> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(auditoriaService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuditoriaEvento> actualizar(@PathVariable Long id, @RequestBody AuditoriaEvento evento) {
        return ResponseEntity.ok(auditoriaService.actualizarEvento(id, evento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        auditoriaService.eliminarEvento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<AuditoriaEvento>> listar(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaEvento") String sort,
            @RequestParam(defaultValue = "desc") String dir) {

        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        if (usuarioId != null) {
            return ResponseEntity.ok(auditoriaService.obtenerEventosPorUsuario(usuarioId, pageable));
        }

        if (tipo != null && accion != null) {
            return ResponseEntity.ok(auditoriaService.obtenerEventosPorTipoYAccion(tipo, accion, pageable));
        }

        if (tipo != null) {
            return ResponseEntity.ok(auditoriaService.obtenerEventosPorTipo(tipo, pageable));
        }

        return ResponseEntity.ok(auditoriaService.obtenerTodos(pageable));
    }
}
