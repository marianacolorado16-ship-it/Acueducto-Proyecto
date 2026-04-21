package com.example.Acueducto.Proyecto.Controller;

import com.example.Acueducto.Proyecto.Model.AuditoriaEvento;
import com.example.Acueducto.Proyecto.Service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    /**
     * Obtiene la historia de auditoría para una entidad específica
     * GET /api/auditoria/entidad/{referenciaId}
     */
    @GetMapping("/entidad/{referenciaId}")
    public ResponseEntity<Page<AuditoriaEvento>> obtenerHistoriaEntidad(
            @PathVariable Long referenciaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaEvento").descending());
        Page<AuditoriaEvento> resultado = auditoriaService.obtenerHistoriaEntidad(referenciaId, pageable);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene eventos de auditoría por tipo
     * GET /api/auditoria/tipo/{tipo}
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<Page<AuditoriaEvento>> obtenerEventosPorTipo(
            @PathVariable String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "fechaEvento") String sort,
            @RequestParam(defaultValue = "desc") String dir) {

        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<AuditoriaEvento> resultado = auditoriaService.obtenerEventosPorTipo(tipo, pageable);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene eventos de auditoría por usuario
     * GET /api/auditoria/usuario/{usuarioId}
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<AuditoriaEvento>> obtenerEventosPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaEvento").descending());
        Page<AuditoriaEvento> resultado = auditoriaService.obtenerEventosPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene eventos por tipo y acción
     * GET /api/auditoria/filtro
     * ?tipo=LECTURA&accion=CREACION&page=0&size=10
     */
    @GetMapping("/filtro")
    public ResponseEntity<Page<AuditoriaEvento>> obtenerEventosFiltrados(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String accion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaEvento").descending());

        Page<AuditoriaEvento> resultado;
        if (tipo != null && accion != null) {
            resultado = auditoriaService.obtenerEventosPorTipoYAccion(tipo, accion, pageable);
        } else if (tipo != null) {
            resultado = auditoriaService.obtenerEventosPorTipo(tipo, pageable);
        } else {
            // Si no hay filtros específicos, devolver eventos recientes
            List<AuditoriaEvento> eventos = auditoriaService.obtenerEventosRecientes();
            resultado = new org.springframework.data.domain.PageImpl<>(
                eventos.subList(0, Math.min(eventos.size(), size)),
                pageable,
                eventos.size()
            );
        }

        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene eventos en un rango de fechas
     * GET /api/auditoria/rango-fechas
     * ?inicio=2024-01-01T00:00:00&fin=2024-01-31T23:59:59&page=0&size=20
     */
    @GetMapping("/rango-fechas")
    public ResponseEntity<Page<AuditoriaEvento>> obtenerEventosPorRangoFechas(
            @RequestParam String inicio,
            @RequestParam String fin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        LocalDateTime inicioDateTime = LocalDateTime.parse(inicio);
        LocalDateTime finDateTime = LocalDateTime.parse(fin);
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaEvento").descending());

        Page<AuditoriaEvento> resultado = auditoriaService
            .obtenerEventosPorRangoFechas(inicioDateTime, finDateTime, pageable);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene eventos recientes (últimos 50)
     * GET /api/auditoria/recientes
     */
    @GetMapping("/recientes")
    public ResponseEntity<List<AuditoriaEvento>> obtenerEventosRecientes() {
        List<AuditoriaEvento> eventos = auditoriaService.obtenerEventosRecientes();
        return ResponseEntity.ok(eventos);
    }

    /**
     * Obtiene resumen de eventos por tipo en el mes especificado
     * GET /api/auditoria/resumen-mes
     * ?mes=2024-01
     */
    @GetMapping("/resumen-mes")
    public ResponseEntity<?> obtenerResumenEventosMes(
            @RequestParam String mes) {

        YearMonth periodo = YearMonth.parse(mes);
        List<Object[]> resumen = auditoriaService.obtenerResumenEventosMes(periodo);

        List<Map<String, Object>> resultado = resumen.stream()
            .map(item -> {
                Map<String, Object> map = new HashMap<>();
                map.put("tipo", item[0]);
                map.put("cantidad", item[1]);
                return map;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }
}
