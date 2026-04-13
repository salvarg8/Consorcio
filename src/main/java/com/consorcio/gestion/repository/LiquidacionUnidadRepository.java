package com.consorcio.gestion.repository;

import com.consorcio.gestion.entity.LiquidacionUnidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiquidacionUnidadRepository extends JpaRepository<LiquidacionUnidad, Long> {
}
