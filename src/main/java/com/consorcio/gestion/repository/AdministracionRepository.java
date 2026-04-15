package com.consorcio.gestion.repository;

import com.consorcio.gestion.entity.Administracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministracionRepository extends JpaRepository<Administracion, Long> {
}
