package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.ConsorcioRequestDTO;
import com.consorcio.gestion.dto.ConsorcioResponseDTO;

import java.util.List;

public interface ConsorcioService {
    List<ConsorcioResponseDTO> getAllConsorcios();
    ConsorcioResponseDTO getConsorcioById(Long id);
    ConsorcioResponseDTO createConsorcio(ConsorcioRequestDTO requestDTO);
    ConsorcioResponseDTO updateConsorcio(Long id, ConsorcioRequestDTO requestDTO);
    void deleteConsorcio(Long id);
    List<Long> getConsorcioIdsForAuthenticatedAdmin();
}
