package com.example.Acueducto.Proyecto.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cuotas_financiacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuotaFinanciacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financiacion_id", nullable = false)
    private Financiacion financiacion;

    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "valor_cuota", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorCuota;

    @Column(name = "valor_pagado", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorPagado;

    @Column(name = "saldo_cuota", nullable = false, precision = 14, scale = 2)
    private BigDecimal saldoCuota;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
}

