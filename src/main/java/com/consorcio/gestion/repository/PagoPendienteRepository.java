package com.consorcio.gestion.repository;

import com.consorcio.gestion.entity.PagoPendiente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PagoPendienteRepository extends JpaRepository<PagoPendiente, Long>, JpaSpecificationExecutor<PagoPendiente> {

    @Query("SELECT p FROM PagoPendiente p WHERE p.unidadFuncional.consorcio.id = :consorcioId")
    Page<PagoPendiente> findByConsorcioId(@Param("consorcioId") Long consorcioId, Pageable pageable);

    @Query("SELECT p FROM PagoPendiente p WHERE p.id = :id AND p.unidadFuncional.consorcio.id = :consorcioId")
    Optional<PagoPendiente> findByIdAndConsorcioId(@Param("id") Long id, @Param("consorcioId") Long consorcioId);
}
