package com.example.Acueducto.Proyecto.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_item", nullable = false, unique = true, length = 50)
    private String codigoItem;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "categoria", length = 80)
    private String categoria;

    @Column(name = "unidad_medida", nullable = false, length = 20)
    private String unidadMedida;

    @Column(name = "cantidad_actual", nullable = false, precision = 14, scale = 3)
    private BigDecimal cantidadActual;

    @Column(name = "stock_minimo", nullable = false, precision = 14, scale = 3)
    private BigDecimal stockMinimo;

    @Column(name = "costo_promedio", nullable = false, precision = 14, scale = 2)
    private BigDecimal costoPromedio;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "ubicacion", length = 120)
    private String ubicacion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (cantidadActual == null) {
            cantidadActual = BigDecimal.ZERO;
        }
        if (stockMinimo == null) {
            stockMinimo = BigDecimal.ZERO;
        }
        if (costoPromedio == null) {
            costoPromedio = BigDecimal.ZERO;
        }
        if (estado == null || estado.isBlank()) {
            estado = "ACTIVO";
        }
    }
}

