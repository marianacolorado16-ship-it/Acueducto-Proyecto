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
@Table(name = "historial_lecturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialLecturas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lectura_id", nullable = false)
    private Lectura lectura;

    @Column(name = "accion", nullable = false, length = 30)
    private String accion;

    @Column(name = "valor_anterior", precision = 12, scale = 3)
    private BigDecimal valorAnterior;

    @Column(name = "valor_nuevo", precision = 12, scale = 3)
    private BigDecimal valorNuevo;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_evento", nullable = false, updatable = false)
    private LocalDateTime fechaEvento;

    @PrePersist
    protected void onCreate() {
        fechaEvento = LocalDateTime.now();
    }
}

