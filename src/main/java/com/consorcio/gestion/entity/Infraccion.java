package com.consorcio.gestion.entity;

import com.consorcio.gestion.enums.EstadoInfraccion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "infracciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Infraccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_funcional_id", nullable = false)
    private UnidadFuncional unidadFuncional;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String motivo;

    private String descripcion;

    @Column(name = "monto_penalizacion", nullable = false)
    private BigDecimal montoPenalizacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInfraccion estado;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
