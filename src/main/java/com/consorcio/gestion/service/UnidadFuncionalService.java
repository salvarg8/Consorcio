package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.UnidadFuncionalRequestDTO;
import com.consorcio.gestion.dto.UnidadFuncionalResponseDTO;
import com.consorcio.gestion.entity.UnidadFuncional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UnidadFuncionalService {
    UnidadFuncionalResponseDTO create(UnidadFuncionalRequestDTO request, Long consorcioId);
    Page<UnidadFuncionalResponseDTO> findAllByConsorcioId(Long consorcioId, Pageable pageable);
    UnidadFuncionalResponseDTO findById(Long id, Long consorcioId);
    UnidadFuncionalResponseDTO update(Long id, UnidadFuncionalRequestDTO request, Long consorcioId);
    void delete(Long id, Long consorcioId);
    UnidadFuncionalResponseDTO assignOwner(Long unitId, Long userId, Long consorcioId);
    UnidadFuncionalResponseDTO assignInquilino(Long unitId, Long userId, Long consorcioId);
    UnidadFuncional getUnidadFuncionalEntityByConsorcio(Long id, Long consorcioId);
}
