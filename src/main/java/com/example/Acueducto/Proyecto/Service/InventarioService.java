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
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final UsuarioRepository usuarioRepository;

    public InventarioService(InventarioRepository inventarioRepository,
                             MovimientoInventarioRepository movimientoInventarioRepository,
                             UsuarioRepository usuarioRepository) {
        this.inventarioRepository = inventarioRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Inventario crearItem(Inventario request) {
        if (inventarioRepository.existsByCodigoItem(request.getCodigoItem().trim())) {
            throw new RuntimeException("Ya existe un item con el código " + request.getCodigoItem());
        }

        request.setCodigoItem(request.getCodigoItem().trim());
        request.setNombre(request.getNombre().trim());
        request.setCantidadActual(defaultDecimal(request.getCantidadActual(), 3));
        request.setStockMinimo(defaultDecimal(request.getStockMinimo(), 3));
        request.setCostoPromedio(defaultDecimal(request.getCostoPromedio(), 2));
        
        if (request.getEstado() == null || request.getEstado().isBlank()) {
            request.setEstado("ACTIVO");
        } else {
            request.setEstado(request.getEstado().trim().toUpperCase());
        }

        return inventarioRepository.save(request);
    }

    @Transactional(readOnly = true)
    public Inventario obtenerPorId(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item de inventario no encontrado con id " + id));
    }

    @Transactional(readOnly = true)
    public Page<Inventario> listar(String estado, Pageable pageable) {
        return (estado == null || estado.isBlank())
                ? inventarioRepository.findAll(pageable)
                : inventarioRepository.findByEstado(estado.trim().toUpperCase(), pageable);
    }

    public MovimientoInventario registrarMovimiento(MovimientoInventario request) {
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
                throw new IllegalArgumentException("No hay stock suficiente para realizar la salida");
            }
            inventario.setCantidadActual(stockAnterior.subtract(cantidad));
        }

        inventarioRepository.save(inventario);
        
        request.setInventario(inventario);
        request.setUsuario(usuario);
        return movimientoInventarioRepository.save(request);
    }

    @Transactional(readOnly = true)
    public Page<MovimientoInventario> listarMovimientos(Long inventarioId, Pageable pageable) {
        return inventarioId == null
                ? movimientoInventarioRepository.findAll(pageable)
                : movimientoInventarioRepository.findByInventarioId(inventarioId, pageable);
    }

    private BigDecimal defaultDecimal(BigDecimal value, int scale) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(scale, RoundingMode.HALF_UP);
        }
        return value.setScale(scale, RoundingMode.HALF_UP);
    }
}
