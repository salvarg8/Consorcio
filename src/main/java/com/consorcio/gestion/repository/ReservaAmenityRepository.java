package com.consorcio.gestion.repository;

import com.consorcio.gestion.entity.ReservaAmenity;
import com.consorcio.gestion.enums.EstadoReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface ReservaAmenityRepository extends JpaRepository<ReservaAmenity, Long>, JpaSpecificationExecutor<ReservaAmenity> {
    
    @Query("SELECT r FROM ReservaAmenity r WHERE r.unidadFuncional.consorcio.id = :consorcioId")
    Page<ReservaAmenity> findByConsorcioId(@Param("consorcioId") Long consorcioId, Pageable pageable);

    @Query("SELECT r FROM ReservaAmenity r WHERE r.id = :id AND r.unidadFuncional.consorcio.id = :consorcioId")
    Optional<ReservaAmenity> findByIdAndConsorcioId(@Param("id") Long id, @Param("consorcioId") Long consorcioId);

    @Query("SELECT COALESCE(SUM(r.amenity.costo), 0) FROM ReservaAmenity r " +
            "WHERE r.unidadFuncional.id = :unidadId " +
            "AND r.fecha BETWEEN :startDate AND :endDate " +
            "AND r.estado = :estado")
    BigDecimal sumTotalForUnitInPeriod(
            @Param("unidadId") Long unidadId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("estado") EstadoReserva estado
    );
}
