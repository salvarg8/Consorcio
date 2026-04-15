package com.consorcio.gestion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "liquidaciones_unidad", uniqueConstraints = {@UniqueConstraint(columnNames = {"liquidacion_mensual_id", "unidad_funcional_id"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE liquidaciones_unidad SET activo = false WHERE id = ?")
@Where(clause = "activo = true")
public class LiquidacionUnidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liquidacion_mensual_id", nullable = false)
    private LiquidacionMensual liquidacionMensual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_funcional_id", nullable = false)
    private UnidadFuncional unidadFuncional;

    @Column(name = "coeficiente_aplicado", nullable = false, precision = 10, scale = 8)
    private BigDecimal coeficienteAplicado;

    @Column(name = "expensa_base_calculada", nullable = false)
    private BigDecimal expensaBaseCalculada;

    @Column(name = "total_infracciones_mes", nullable = false)
    private BigDecimal totalInfraccionesMes;

    @Column(name = "total_amenities_mes", nullable = false)
    private BigDecimal totalAmenitiesMes;

    @Column(name = "total_pagar", nullable = false)
    private BigDecimal totalPagar;

    private boolean activo = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
