package com.consorcio.gestion.repository;

import com.consorcio.gestion.entity.UnidadFuncional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UnidadFuncionalRepository extends JpaRepository<UnidadFuncional, Long> {

    Optional<UnidadFuncional> findByIdAndConsorcioId(Long id, Long consorcioId);

    Optional<UnidadFuncional> findByIdentificadorAndConsorcioId(String identificador, Long consorcioId);

    boolean existsByIdentificadorAndConsorcioId(String identificador, Long consorcioId);

    Page<UnidadFuncional> findByConsorcioId(Long consorcioId, Pageable pageable);

    List<UnidadFuncional> findAllByConsorcioIdAndActivaTrue(Long consorcioId);
}
