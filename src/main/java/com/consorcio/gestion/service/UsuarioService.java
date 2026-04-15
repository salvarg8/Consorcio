package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.UsuarioRequestDTO;
import com.consorcio.gestion.dto.UsuarioResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {
    UsuarioResponseDTO create(UsuarioRequestDTO request, Long consorcioId);
    Page<UsuarioResponseDTO> findAllByConsorcioId(Long consorcioId, Pageable pageable);
    UsuarioResponseDTO findById(Long id, Long consorcioId);
    UsuarioResponseDTO update(Long id, UsuarioRequestDTO request, Long consorcioId);
    void delete(Long id, Long consorcioId);
}
