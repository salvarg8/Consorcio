package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.UnidadFuncionalRequestDTO;
import com.consorcio.gestion.dto.UnidadFuncionalResponseDTO;
import com.consorcio.gestion.entity.Consorcio;
import com.consorcio.gestion.entity.UnidadFuncional;
import com.consorcio.gestion.entity.Usuario;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.exception.ResourceNotFoundException;
import com.consorcio.gestion.mapper.UnidadFuncionalMapper;
import com.consorcio.gestion.repository.ConsorcioRepository;
import com.consorcio.gestion.repository.UnidadFuncionalRepository;
import com.consorcio.gestion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnidadFuncionalServiceImpl implements UnidadFuncionalService {

    private final UnidadFuncionalRepository unidadFuncionalRepository;
    private final ConsorcioRepository consorcioRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadFuncionalMapper unidadFuncionalMapper;

    @Transactional
    public UnidadFuncionalResponseDTO create(UnidadFuncionalRequestDTO request, Long consorcioId) {
        Consorcio consorcio = consorcioRepository.findById(consorcioId)
                .orElseThrow(() -> new ResourceNotFoundException("Consorcio no encontrado con id: " + consorcioId));

        if (unidadFuncionalRepository.existsByIdentificadorAndConsorcioId(request.getIdentificador(), consorcioId)) {
            throw new BusinessException("El identificador de la unidad funcional ya existe para este consorcio");
        }

        UnidadFuncional unidad = unidadFuncionalMapper.toEntity(request);
        unidad.setConsorcio(consorcio);
        return unidadFuncionalMapper.toResponseDTO(unidadFuncionalRepository.save(unidad));
    }

    @Transactional(readOnly = true)
    public Page<UnidadFuncionalResponseDTO> findAllByConsorcioId(Long consorcioId, Pageable pageable) {
        return unidadFuncionalRepository.findByConsorcioId(consorcioId, pageable)
                .map(unidadFuncionalMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public UnidadFuncionalResponseDTO findById(Long id, Long consorcioId) {
        UnidadFuncional unidad = getUnidadFuncionalEntityByConsorcio(id, consorcioId);
        return unidadFuncionalMapper.toResponseDTO(unidad);
    }

    @Transactional
    public UnidadFuncionalResponseDTO update(Long id, UnidadFuncionalRequestDTO request, Long consorcioId) {
        UnidadFuncional unidad = getUnidadFuncionalEntityByConsorcio(id, consorcioId);

        if (!unidad.getIdentificador().equals(request.getIdentificador()) &&
                unidadFuncionalRepository.existsByIdentificadorAndConsorcioId(request.getIdentificador(), consorcioId)) {
            throw new BusinessException("El nuevo identificador ya está en uso para este consorcio");
        }

        unidad.setIdentificador(request.getIdentificador());
        unidad.setPiso(request.getPiso());
        unidad.setDescripcion(request.getDescripcion());
        unidad.setCoeficiente(request.getCoeficiente());

        return unidadFuncionalMapper.toResponseDTO(unidadFuncionalRepository.save(unidad));
    }

    @Transactional
    public void delete(Long id, Long consorcioId) {
        UnidadFuncional unidad = getUnidadFuncionalEntityByConsorcio(id, consorcioId);
        unidad.setActiva(false);
        unidadFuncionalRepository.save(unidad);
    }

    @Transactional
    public UnidadFuncionalResponseDTO assignOwner(Long unitId, Long userId, Long consorcioId) {
        UnidadFuncional unidad = getUnidadFuncionalEntityByConsorcio(unitId, consorcioId);

        Usuario propietario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        if (!propietario.isActivo()) {
            throw new BusinessException("No se puede asignar un propietario inactivo");
        }

        unidad.setPropietario(propietario);
        propietario.getUnidades().add(unidad);
        return unidadFuncionalMapper.toResponseDTO(unidadFuncionalRepository.save(unidad));
    }

    @Transactional
    public UnidadFuncionalResponseDTO assignInquilino(Long unitId, Long userId, Long consorcioId) {
        UnidadFuncional unidad = getUnidadFuncionalEntityByConsorcio(unitId, consorcioId);

        Usuario inquilino = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        if (!inquilino.isActivo()) {
            throw new BusinessException("No se puede asignar un inquilino inactivo");
        }

        unidad.setInquilino(inquilino);
        inquilino.getUnidades().add(unidad);
        return unidadFuncionalMapper.toResponseDTO(unidadFuncionalRepository.save(unidad));
    }

    public UnidadFuncional getUnidadFuncionalEntityByConsorcio(Long id, Long consorcioId) {
        return unidadFuncionalRepository.findByIdAndConsorcioId(id, consorcioId)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad Funcional no encontrada con ID: " + id + " en este consorcio"));
    }
}
