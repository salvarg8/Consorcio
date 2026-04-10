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
public class UnidadFuncionalService {

    private final UnidadFuncionalRepository unidadFuncionalRepository;
    private final ConsorcioRepository consorcioRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public UnidadFuncionalResponseDTO create(UnidadFuncionalRequestDTO request) {
        if (request.getConsorcioId() == null) {
            throw new BusinessException("El consorcioId es obligatorio");
        }

        Consorcio consorcio = consorcioRepository.findById(request.getConsorcioId())
                .orElseThrow(() -> new ResourceNotFoundException("Consorcio no encontrado con id: " + request.getConsorcioId()));

        if (unidadFuncionalRepository.existsByIdentificadorAndConsorcioId(request.getIdentificador(), request.getConsorcioId())) {
            throw new BusinessException("El identificador de la unidad funcional ya existe para este consorcio");
        }

        UnidadFuncional unidad = UnidadFuncionalMapper.toEntity(request);
        unidad.setConsorcio(consorcio);
        return UnidadFuncionalMapper.toResponseDTO(unidadFuncionalRepository.save(unidad));
    }

    @Transactional(readOnly = true)
    public Page<UnidadFuncionalResponseDTO> findAll(Pageable pageable) {
        return unidadFuncionalRepository.findAll(pageable)
                .map(UnidadFuncionalMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<UnidadFuncionalResponseDTO> findAllByConsorcioId(Long consorcioId, Pageable pageable) {
        return unidadFuncionalRepository.findByConsorcioId(consorcioId, pageable)
                .map(UnidadFuncionalMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public UnidadFuncionalResponseDTO findById(Long id) {
        UnidadFuncional unidad = getUnidadFuncionalEntity(id);
        return UnidadFuncionalMapper.toResponseDTO(unidad);
    }

    @Transactional
    public UnidadFuncionalResponseDTO update(Long id, UnidadFuncionalRequestDTO request) {
        UnidadFuncional unidad = getUnidadFuncionalEntity(id);

        if (request.getConsorcioId() != null && !unidad.getConsorcio().getId().equals(request.getConsorcioId())) {
             Consorcio consorcio = consorcioRepository.findById(request.getConsorcioId())
                .orElseThrow(() -> new ResourceNotFoundException("Consorcio no encontrado con id: " + request.getConsorcioId()));
             unidad.setConsorcio(consorcio);
        }

        if (!unidad.getIdentificador().equals(request.getIdentificador()) &&
                unidadFuncionalRepository.existsByIdentificadorAndConsorcioId(request.getIdentificador(), unidad.getConsorcio().getId())) {
            throw new BusinessException("El nuevo identificador ya está en uso para este consorcio");
        }

        unidad.setIdentificador(request.getIdentificador());
        unidad.setPiso(request.getPiso());
        unidad.setDescripcion(request.getDescripcion());

        return UnidadFuncionalMapper.toResponseDTO(unidadFuncionalRepository.save(unidad));
    }

    @Transactional
    public void delete(Long id) {
        UnidadFuncional unidad = getUnidadFuncionalEntity(id);
        unidad.setActiva(false);
        unidadFuncionalRepository.save(unidad);
    }

    @Transactional
    public UnidadFuncionalResponseDTO assignOwner(Long unitId, Long userId) {
        UnidadFuncional unidad = getUnidadFuncionalEntity(unitId);

        Usuario propietario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        if (!propietario.isActivo()) {
            throw new BusinessException("No se puede asignar un propietario inactivo");
        }

        unidad.setPropietario(propietario);
        return UnidadFuncionalMapper.toResponseDTO(unidadFuncionalRepository.save(unidad));
    }

    @Transactional
    public UnidadFuncionalResponseDTO assignInquilino(Long unitId, Long userId) {
        UnidadFuncional unidad = getUnidadFuncionalEntity(unitId);

        Usuario inquilino = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        if (!inquilino.isActivo()) {
            throw new BusinessException("No se puede asignar un inquilino inactivo");
        }

        unidad.setInquilino(inquilino);
        return UnidadFuncionalMapper.toResponseDTO(unidadFuncionalRepository.save(unidad));
    }

    public UnidadFuncional getUnidadFuncionalEntity(Long id) {
        return unidadFuncionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad Funcional no encontrada con ID: " + id));
    }
}
