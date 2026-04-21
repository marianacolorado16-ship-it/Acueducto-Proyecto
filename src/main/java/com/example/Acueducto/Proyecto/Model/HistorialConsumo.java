package com.example.Acueducto.Proyecto.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_consumo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialConsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medidor_id", nullable = false)
    private Medidor medidor;

    @Column(name = "periodo", nullable = false, length = 7)
    private String periodo;

    @Column(name = "consumo_m3", nullable = false, precision = 12, scale = 3)
    private BigDecimal consumoM3;

    @Column(name = "promedio_3m", precision = 12, scale = 3)
    private BigDecimal promedio3m;

    @Column(name = "promedio_6m", precision = 12, scale = 3)
    private BigDecimal promedio6m;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

