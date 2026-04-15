package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.EstadoInfraccionRequestDTO;
import com.consorcio.gestion.dto.InfraccionRequestDTO;
import com.consorcio.gestion.dto.InfraccionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InfraccionService {
    InfraccionResponseDTO create(InfraccionRequestDTO request, Long consorcioId);
    Page<InfraccionResponseDTO> findAllByConsorcioId(Long consorcioId, Pageable pageable);
    InfraccionResponseDTO findById(Long id, Long consorcioId);
    InfraccionResponseDTO updateStatus(Long id, EstadoInfraccionRequestDTO request, Long consorcioId);
}
