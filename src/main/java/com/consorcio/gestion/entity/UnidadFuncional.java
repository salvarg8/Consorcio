package com.consorcio.gestion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "unidades_funcionales", uniqueConstraints = {@UniqueConstraint(columnNames = {"identificador", "consorcio_id"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadFuncional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String identificador;

    @Column(nullable = false)
    private Integer piso;

    private String descripcion;

    @Column(nullable = false)
    @Builder.Default
    private boolean activa = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consorcio_id", nullable = false)
    private Consorcio consorcio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id")
    private Usuario propietario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquilino_id")
    private Usuario inquilino;

    @OneToMany(mappedBy = "unidadFuncional", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ReservaAmenity> reservas = new ArrayList<>();

    @OneToMany(mappedBy = "unidadFuncional", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Infraccion> infracciones = new ArrayList<>();

    @OneToMany(mappedBy = "unidadFuncional", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PagoPendiente> pagos = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}