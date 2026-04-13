package com.consorcio.gestion.repository;

import com.consorcio.gestion.entity.LiquidacionMensual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LiquidacionMensualRepository extends JpaRepository<LiquidacionMensual, Long> {
    Optional<LiquidacionMensual> findByConsorcioIdAndPeriodo(Long consorcioId, String periodo);
}
