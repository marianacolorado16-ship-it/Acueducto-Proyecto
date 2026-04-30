package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.Cliente;
import com.example.Acueducto.Proyecto.Model.Medidor;
import com.example.Acueducto.Proyecto.Repository.ClienteRepository;
import com.example.Acueducto.Proyecto.Repository.MedidorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Transactional
public class MedidorService {

    private final MedidorRepository medidorRepository;
    private final ClienteRepository clienteRepository;

    public MedidorService(MedidorRepository medidorRepository, ClienteRepository clienteRepository) {
        this.medidorRepository = medidorRepository;
        this.clienteRepository = clienteRepository;
    }

    public Medidor crearMedidor(Medidor request) {
        if (request.getCliente() == null || request.getCliente().getId() == null) {
            throw new RuntimeException("El cliente es requerido para registrar un medidor");
        }

        Cliente cliente = clienteRepository.findById(request.getCliente().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        if (medidorRepository.existsByCodigoSerie(request.getCodigoSerie().trim())) {
            throw new RuntimeException("Ya existe un medidor registrado con el código de serie: " + request.getCodigoSerie());
        }

        request.setCliente(cliente);
        request.setCodigoSerie(request.getCodigoSerie().trim());
        if (request.getEstado() == null) request.setEstado("ACTIVO");
        if (request.getLecturaInicial() == null) {
            request.setLecturaInicial(BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP));
        }

        return medidorRepository.save(request);
    }

    @Transactional(readOnly = true)
    public Medidor obtenerPorId(Long id) {
        return medidorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medidor no encontrado con id " + id));
    }

    @Transactional(readOnly = true)
    public Page<Medidor> listar(Long clienteId, String estado, Pageable pageable) {
        if (clienteId != null) {
            return medidorRepository.findByClienteId(clienteId, pageable);
        } else if (estado != null && !estado.isBlank()) {
            return medidorRepository.findByEstado(estado.trim().toUpperCase(), pageable);
        }
        return medidorRepository.findAll(pageable);
    }
}
