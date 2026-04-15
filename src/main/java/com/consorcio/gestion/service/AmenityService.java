package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.AmenityRequestDTO;
import com.consorcio.gestion.dto.AmenityResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AmenityService {
    AmenityResponseDTO create(AmenityRequestDTO request, Long consorcioId);
    Page<AmenityResponseDTO> findAllByConsorcioId(Long consorcioId, Pageable pageable);
    AmenityResponseDTO findById(Long id, Long consorcioId);
    AmenityResponseDTO update(Long id, AmenityRequestDTO request, Long consorcioId);
    void delete(Long id, Long consorcioId);
}
