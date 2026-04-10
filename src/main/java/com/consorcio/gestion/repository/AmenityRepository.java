package com.consorcio.gestion.repository;

import com.consorcio.gestion.entity.Amenity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    Page<Amenity> findByConsorcioId(Long consorcioId, Pageable pageable);
    Optional<Amenity> findByIdAndConsorcioId(Long id, Long consorcioId);
    boolean existsByNombreAndConsorcioId(String nombre, Long consorcioId);
}
