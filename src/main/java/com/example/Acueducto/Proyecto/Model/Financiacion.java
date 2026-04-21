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
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "financiaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Financiacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo;

    @Column(name = "referencia_origen", length = 80)
    private String referenciaOrigen;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "monto_inicial", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoInicial;

    @Column(name = "saldo_actual", nullable = false, precision = 14, scale = 2)
    private BigDecimal saldoActual;

    @Column(name = "numero_cuotas", nullable = false)
    private Integer numeroCuotas;

    @Column(name = "interes_mensual", nullable = false, precision = 8, scale = 4)
    private BigDecimal interesMensual;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (estado == null || estado.isBlank()) {
            estado = "ACTIVA";
        }
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now();
        }
    }
}
