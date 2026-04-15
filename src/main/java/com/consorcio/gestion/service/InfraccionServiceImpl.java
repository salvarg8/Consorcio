package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.EstadoInfraccionRequestDTO;
import com.consorcio.gestion.dto.InfraccionRequestDTO;
import com.consorcio.gestion.dto.InfraccionResponseDTO;
import com.consorcio.gestion.entity.Infraccion;
import com.consorcio.gestion.entity.UnidadFuncional;
import com.consorcio.gestion.enums.EstadoInfraccion;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.exception.ResourceNotFoundException;
import com.consorcio.gestion.mapper.InfraccionMapper;
import com.consorcio.gestion.repository.InfraccionRepository;
import com.consorcio.gestion.repository.UnidadFuncionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InfraccionServiceImpl implements InfraccionService {

    private final InfraccionRepository infraccionRepository;
    private final UnidadFuncionalRepository unidadFuncionalRepository;
    private final InfraccionMapper infraccionMapper;

    @Transactional
    public InfraccionResponseDTO create(InfraccionRequestDTO request, Long consorcioId) {
        UnidadFuncional unidad = unidadFuncionalRepository.findByIdAndConsorcioId(request.unidadFuncionalId(),
                        consorcioId)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad Funcional no encontrada en este consorcio"));

        if (!unidad.isActiva()) {
            throw new BusinessException("No se puede crear una infracción para una unidad inactiva");
        }

        Infraccion infraccion = Infraccion.builder()
                .unidadFuncional(unidad)
                .fecha(request.fecha())
                .motivo(request.motivo())
                .descripcion(request.descripcion())
                .montoPenalizacion(request.montoPenalizacion())
                .estado(EstadoInfraccion.PENDIENTE)
                .build();

        return infraccionMapper.toResponseDTO(infraccionRepository.save(infraccion));
    }

    @Transactional(readOnly = true)
    public Page<InfraccionResponseDTO> findAllByConsorcioId(Long consorcioId, Pageable pageable) {
        return infraccionRepository.findByConsorcioId(consorcioId, pageable)
                .map(infraccionMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public InfraccionResponseDTO findById(Long id, Long consorcioId) {
        Infraccion infraccion = getInfraccionEntityByConsorcio(id, consorcioId);
        return infraccionMapper.toResponseDTO(infraccion);
    }

    @Transactional
    public InfraccionResponseDTO updateStatus(Long id, EstadoInfraccionRequestDTO request, Long consorcioId) {
        Infraccion infraccion = getInfraccionEntityByConsorcio(id, consorcioId);
        
        if (infraccion.getEstado() == request.estado()) {
            throw new BusinessException("La infracción ya se encuentra en el estado " + request.estado());
        }

        infraccion.setEstado(request.estado());
        return infraccionMapper.toResponseDTO(infraccionRepository.save(infraccion));
    }

    private Infraccion getInfraccionEntityByConsorcio(Long id, Long consorcioId) {
        return infraccionRepository.findByIdAndConsorcioId(id, consorcioId)
                .orElseThrow(() -> new ResourceNotFoundException("Infracción no encontrada con ID: " + id + " en este consorcio"));
    }
}
