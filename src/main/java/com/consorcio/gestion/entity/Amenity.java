package com.consorcio.gestion.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
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
@Table(name = "amenities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(name = "capacidad_maxima")
    private Integer capacidadMaxima;

    @Column(nullable = false)
    @Builder.Default
    private boolean habilitado = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consorcio_id", nullable = false)
    private Consorcio consorcio;

    @OneToMany(mappedBy = "amenity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ReservaAmenity> reservas = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private Usuario createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by_id")
    private Usuario updatedBy;
}