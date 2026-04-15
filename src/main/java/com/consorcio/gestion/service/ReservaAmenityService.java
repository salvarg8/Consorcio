package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.ReservaRequestDTO;
import com.consorcio.gestion.dto.ReservaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservaAmenityService {
    ReservaResponseDTO create(ReservaRequestDTO request, Long consorcioId);
    Page<ReservaResponseDTO> findAllByConsorcioId(Long consorcioId, Pageable pageable);
    ReservaResponseDTO findById(Long id, Long consorcioId);
    ReservaResponseDTO confirm(Long id, Long consorcioId);
    ReservaResponseDTO cancel(Long id, Long consorcioId);
}
