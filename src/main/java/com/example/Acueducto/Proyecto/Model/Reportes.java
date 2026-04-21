package com.example.Acueducto.Proyecto.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes_generados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reportes {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre; // Ejemplo: "Recaudo Mensual Enero 2024"

    @Column(nullable = false, length = 50)
    private String tipo; // RECAUDO, CONSUMO, CARTERA, INVENTARIO, DASHBOARD

    @Column(length = 20)
    private String periodo; // Formato YYYY-MM si aplica

    @Column(columnDefinition = "TEXT")
    private String parametros; // Filtros usados guardados como JSON

    @Column(name = "url_descarga", length = 255)
    private String urlDescarga; // Ruta si el reporte se exportó a PDF/Excel

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario generadoPor;

    @Column(name = "fecha_generacion", nullable = false, updatable = false)
    private LocalDateTime fechaGeneracion;

    @PrePersist
    protected void onCreate() {
        this.fechaGeneracion = LocalDateTime.now();
    }
}
