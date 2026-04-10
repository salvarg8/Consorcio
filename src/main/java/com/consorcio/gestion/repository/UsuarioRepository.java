package com.consorcio.gestion.repository;

import com.consorcio.gestion.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM Usuario u JOIN u.consorcios c WHERE c.id = :consorcioId")
    Page<Usuario> findByConsorcioId(@Param("consorcioId") Long consorcioId, Pageable pageable);

    @Query("SELECT u FROM Usuario u JOIN u.consorcios c WHERE u.id = :id AND c.id = :consorcioId")
    Optional<Usuario> findByIdAndConsorcioId(@Param("id") Long id, @Param("consorcioId") Long consorcioId);
}
