package com.consorcio.gestion.repository;

import com.consorcio.gestion.entity.Consorcio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsorcioRepository extends JpaRepository<Consorcio, Long> {
}
