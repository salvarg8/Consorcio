package com.consorcio.gestion.entity;

import com.consorcio.gestion.enums.EstadoLiquidacion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "liquidaciones_mensuales", uniqueConstraints = {@UniqueConstraint(columnNames = {"consorcio_id", "periodo"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquidacionMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consorcio_id", nullable = false)
    private Consorcio consorcio;

    @Column(nullable = false, length = 7)
    private String periodo; // YYYY-MM

    @Column(name = "gasto_comun_mes", nullable = false)
    private BigDecimal gastoComunMes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoLiquidacion estado;

    @OneToMany(mappedBy = "liquidacionMensual", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LiquidacionUnidad> liquidacionesUnidad = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
