package com.consorcio.gestion.repository;

import com.consorcio.gestion.entity.Infraccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InfraccionRepository extends JpaRepository<Infraccion, Long>, JpaSpecificationExecutor<Infraccion> {

    @Query("SELECT i FROM Infraccion i WHERE i.unidadFuncional.consorcio.id = :consorcioId")
    Page<Infraccion> findByConsorcioId(@Param("consorcioId") Long consorcioId, Pageable pageable);

    @Query("SELECT i FROM Infraccion i WHERE i.id = :id AND i.unidadFuncional.consorcio.id = :consorcioId")
    Optional<Infraccion> findByIdAndConsorcioId(@Param("id") Long id, @Param("consorcioId") Long consorcioId);
}
