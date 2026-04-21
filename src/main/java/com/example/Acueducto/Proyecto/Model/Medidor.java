package com.example.Acueducto.Proyecto.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medidores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medidor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @Column(name = "codigo_serie", nullable = false, unique = true, length = 60)
    private String codigoSerie;
    
    @Column(name = "marca", length = 80)
    private String marca;
    
    @Column(name = "modelo", length = 80)
    private String modelo;
    
    @Column(name = "diametro_pulgadas", precision = 5, scale = 2)
    private BigDecimal diametroPulgadas;
    
    @Column(name = "lectura_inicial", nullable = false, precision = 12, scale = 3)
    @Builder.Default
    private BigDecimal lecturaInicial = BigDecimal.ZERO;
    
    @Column(name = "fecha_instalacion", nullable = false)
    private LocalDate fechaInstalacion;
    
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private String estado = "ACTIVO";
    
    @Column(name = "ubicacion", length = 200)
    private String ubicacion;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fechaInstalacion == null) {
            fechaInstalacion = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
