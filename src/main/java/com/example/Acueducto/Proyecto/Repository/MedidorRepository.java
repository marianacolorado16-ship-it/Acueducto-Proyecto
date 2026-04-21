package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.Medidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedidorRepository extends JpaRepository<Medidor, Long> {
    
    Optional<Medidor> findByCodigoSerie(String codigoSerie);
    
    List<Medidor> findByClienteId(Long clienteId);
    
    List<Medidor> findByEstado(String estado);
    
    boolean existsByCodigoSerie(String codigoSerie);
}
