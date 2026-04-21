package com.example.Acueducto.Proyecto.Model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_eventos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String tipo; // LECTURA, FACTURA, PAGO, FINANCIACION, TURNO, INVENTARIO, CLIENTE

    @Column(nullable = false, length = 50)
    private String accion; // CREACION, ACTUALIZACION, CANCELACION, ELIMINACION, REGISTRADO

    @Column(nullable = false)
    private Long referenciaId; // ID of the entity being audited

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String detalle; // Description of what happened

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior; // Previous value (as JSON or text)

    @Column(name = "valor_nuevo", columnDefinition = "TEXT")
    private String valorNuevo; // New value (as JSON or text)

    @Column(nullable = false, name = "fecha_evento")
    private LocalDateTime fechaEvento;

    @Column(name = "ip_address", length = 50)
    private String ipAddress; // Optional: IP of user who triggered the event

    @PrePersist
    protected void onCreate() {
        fechaEvento = LocalDateTime.now();
    }
}
