package com.example.Acueducto.Proyecto.Repository;

import com.example.Acueducto.Proyecto.Model.Inventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByCodigoItem(String codigoItem);

    boolean existsByCodigoItem(String codigoItem);

    Page<Inventario> findByEstado(String estado, Pageable pageable);
}
