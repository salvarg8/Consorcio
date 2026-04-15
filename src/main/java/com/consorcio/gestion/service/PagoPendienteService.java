package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.PagoPendienteRequestDTO;
import com.consorcio.gestion.dto.PagoPendienteResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PagoPendienteService {
    PagoPendienteResponseDTO create(PagoPendienteRequestDTO request, Long consorcioId);
    Page<PagoPendienteResponseDTO> findAllByConsorcioId(Long consorcioId, Pageable pageable);
    PagoPendienteResponseDTO findById(Long id, Long consorcioId);
    PagoPendienteResponseDTO pay(Long id, Long consorcioId);
}
