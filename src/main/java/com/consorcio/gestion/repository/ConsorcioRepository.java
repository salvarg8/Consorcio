package com.consorcio.gestion.repository;

import com.consorcio.gestion.entity.Consorcio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsorcioRepository extends JpaRepository<Consorcio, Long> {
    @Query("SELECT c.id FROM Consorcio c WHERE c.administracion.id = :administracionId")
    List<Long> findIdsByAdministracionId(@Param("administracionId") Long administracionId);
}
