package com.example.Acueducto.Proyecto.Service;

import com.example.Acueducto.Proyecto.Model.Cliente;
import com.example.Acueducto.Proyecto.Repository.ClienteRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ClienteService {

    private static final String CLIENTE_NO_ENCONTRADO = "Cliente no encontrado con id ";

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente crearCliente(Cliente cliente) {
        validarDuplicados(null, cliente);
        if (cliente.getEstado() == null) cliente.setEstado("ACTIVO");
        if (cliente.getFechaRegistro() == null) cliente.setFechaRegistro(LocalDate.now());
        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(CLIENTE_NO_ENCONTRADO + id));
    }

    @Transactional(readOnly = true)
    public Page<Cliente> listarClientes(String estado, String busqueda, Pageable pageable) {
        Specification<Cliente> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (estado != null && !estado.isBlank()) {
                predicates.add(cb.equal(root.get("estado"), estado.trim().toUpperCase()));
            }

            if (busqueda != null && !busqueda.isBlank()) {
                String like = "%" + busqueda.trim().toLowerCase() + "%";
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("nombres")), like),
                                cb.like(cb.lower(root.get("apellidos")), like),
                                cb.like(cb.lower(root.get("documento")), like),
                                cb.like(cb.lower(root.get("codigoCliente")), like)
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return clienteRepository.findAll(spec, pageable);
    }

    public Cliente actualizarCliente(Long id, Cliente request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(CLIENTE_NO_ENCONTRADO + id));

        validarDuplicados(id, request);

        // Actualización segura: solo actualiza si el campo no es nulo en el request
        if (request.getCodigoCliente() != null) cliente.setCodigoCliente(request.getCodigoCliente().trim());
        if (request.getDocumento() != null) cliente.setDocumento(request.getDocumento().trim());
        if (request.getNombres() != null) cliente.setNombres(request.getNombres().trim());
        if (request.getApellidos() != null) cliente.setApellidos(request.getApellidos().trim());
        if (request.getDireccion() != null) cliente.setDireccion(request.getDireccion().trim());
        if (request.getTelefono() != null) cliente.setTelefono(request.getTelefono().trim());
        if (request.getEmail() != null) cliente.setEmail(request.getEmail().trim());
        if (request.getEstrato() != null) cliente.setEstrato(request.getEstrato());

        if (request.getEstado() != null && !request.getEstado().isBlank()) {
            cliente.setEstado(request.getEstado().trim().toUpperCase());
        }

        return clienteRepository.save(cliente);
    }

    public Cliente cambiarEstado(Long id, String estado) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(CLIENTE_NO_ENCONTRADO + id));

        cliente.setEstado(estado.trim().toUpperCase());
        return clienteRepository.save(cliente);
    }

    public void desactivarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(CLIENTE_NO_ENCONTRADO + id));
        cliente.setEstado("INACTIVO");
        clienteRepository.save(cliente);
    }

    private void validarDuplicados(Long idActual, Cliente request) {
        // Se agregan validaciones de nulidad antes de usar .trim() para evitar NullPointerException
        if (request.getCodigoCliente() != null && !request.getCodigoCliente().isBlank()) {
            clienteRepository.findByCodigoCliente(request.getCodigoCliente().trim())
                    .filter(c -> idActual == null || !c.getId().equals(idActual))
                    .ifPresent(c -> {
                        throw new RuntimeException("Ya existe un cliente con el código " + request.getCodigoCliente());
                    });
        }

        if (request.getDocumento() != null && !request.getDocumento().isBlank()) {
            clienteRepository.findByDocumento(request.getDocumento().trim())
                    .filter(c -> idActual == null || !c.getId().equals(idActual))
                    .ifPresent(c -> {
                        throw new RuntimeException("Ya existe un cliente con el documento " + request.getDocumento());
                    });
        }
    }
}
