package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {
    
    Optional<Cliente> findByCodigoCliente(String codigoCliente);
    
    Optional<Cliente> findByDocumento(String documento);
    
    List<Cliente> findByEstado(String estado);
    
    List<Cliente> findByNombresContainingIgnoreCase(String nombres);
    
    boolean existsByCodigoCliente(String codigoCliente);
    
    boolean existsByDocumento(String documento);
}
