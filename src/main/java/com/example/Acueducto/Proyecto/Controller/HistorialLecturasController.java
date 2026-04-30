package com.example.Acueducto.Proyecto.Controller;

import com.example.Acueducto.Proyecto.Model.HistorialLecturas;
import com.example.Acueducto.Proyecto.Service.HistorialLecturasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historial-lecturas")
public class HistorialLecturasController {

    private final HistorialLecturasService historialLecturasService;

    public HistorialLecturasController(HistorialLecturasService historialLecturasService) {
        this.historialLecturasService = historialLecturasService;
    }

    /**
     * GET /historial-lecturas/lectura/{lecturaId}
     */
    @GetMapping("/lectura/{lecturaId}")
    public ResponseEntity<List<HistorialLecturas>> listarPorLectura(@PathVariable Long lecturaId) {
        return ResponseEntity.ok(historialLecturasService.listarPorLectura(lecturaId));
    }

    /**
     * GET /historial-lecturas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<HistorialLecturas> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(historialLecturasService.obtenerPorId(id));
    }
}
