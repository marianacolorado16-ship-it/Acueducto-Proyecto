package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.Inventario;
import com.example.Acueducto.Proyecto.Model.MovimientoInventario;
import com.example.Acueducto.Proyecto.Model.Usuario;
import com.example.Acueducto.Proyecto.Repository.InventarioRepository;
import com.example.Acueducto.Proyecto.Repository.MovimientoInventarioRepository;
import com.example.Acueducto.Proyecto.Repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Transactional
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final InventarioRepository inventarioRepository;
    private final UsuarioRepository usuarioRepository;

    public MovimientoInventarioService(MovimientoInventarioRepository movimientoInventarioRepository,
                                       InventarioRepository inventarioRepository,
                                       UsuarioRepository usuarioRepository) {
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.inventarioRepository = inventarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public MovimientoInventario registrarMovimiento(MovimientoInventario request) {
        if (request.getInventario() == null || request.getInventario().getId() == null) {
            throw new RuntimeException("El item de inventario es requerido");
        }

        Inventario inventario = inventarioRepository.findById(request.getInventario().getId())
                .orElseThrow(() -> new RuntimeException("Item de inventario no encontrado"));

        Usuario usuario = usuarioRepository.findById(request.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String tipo = request.getTipoMovimiento().trim().toUpperCase();
        if (!"ENTRADA".equals(tipo) && !"SALIDA".equals(tipo)) {
            throw new IllegalArgumentException("El tipo de movimiento debe ser ENTRADA o SALIDA");
        }

        BigDecimal cantidad = request.getCantidad().setScale(3, RoundingMode.HALF_UP);
        BigDecimal stockAnterior = inventario.getCantidadActual();

        if ("ENTRADA".equals(tipo)) {
            inventario.setCantidadActual(stockAnterior.add(cantidad));
        } else {
            if (stockAnterior.compareTo(cantidad) < 0) {
                throw new IllegalArgumentException("No hay stock suficiente para realizar la salida. Stock actual: " + stockAnterior);
            }
            inventario.setCantidadActual(stockAnterior.subtract(cantidad));
        }

        inventarioRepository.save(inventario);
        
        request.setInventario(inventario);
        request.setUsuario(usuario);
        return movimientoInventarioRepository.save(request);
    }

    @Transactional(readOnly = true)
    public MovimientoInventario obtenerPorId(Long id) {
        return movimientoInventarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento de inventario no encontrado con id " + id));
    }

    @Transactional(readOnly = true)
    public Page<MovimientoInventario> listar(Long inventarioId, Pageable pageable) {
        return inventarioId == null
                ? movimientoInventarioRepository.findAll(pageable)
                : movimientoInventarioRepository.findByInventarioId(inventarioId, pageable);
    }
}
