package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.ConsorcioRequestDTO;
import com.consorcio.gestion.dto.ConsorcioResponseDTO;
import com.consorcio.gestion.entity.Administracion;
import com.consorcio.gestion.entity.Consorcio;
import com.consorcio.gestion.entity.Usuario;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.exception.ResourceNotFoundException;
import com.consorcio.gestion.mapper.ConsorcioMapper;
import com.consorcio.gestion.repository.AdministracionRepository;
import com.consorcio.gestion.repository.ConsorcioRepository;
import com.consorcio.gestion.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsorcioService {

    private final ConsorcioRepository consorcioRepository;
    private final AdministracionRepository administracionRepository;
    private final ConsorcioMapper consorcioMapper;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<ConsorcioResponseDTO> getAllConsorcios() {
        return consorcioRepository.findAll().stream()
                .map(consorcioMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConsorcioResponseDTO getConsorcioById(Long id) {
        Consorcio consorcio = consorcioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consorcio no encontrado con id: " + id));
        return consorcioMapper.toDto(consorcio);
    }

    @Transactional
    public ConsorcioResponseDTO createConsorcio(ConsorcioRequestDTO requestDTO) {
        Administracion administracion = administracionRepository.findById(requestDTO.administracionId())
                .orElseThrow(() -> new ResourceNotFoundException("Administración no encontrada con id: " + requestDTO.administracionId()));

        Consorcio consorcio = consorcioMapper.toEntity(requestDTO);
        consorcio.setAdministracion(administracion);
        Consorcio savedConsorcio = consorcioRepository.save(consorcio);
        return consorcioMapper.toDto(savedConsorcio);
    }

    @Transactional
    public ConsorcioResponseDTO updateConsorcio(Long id, ConsorcioRequestDTO requestDTO) {
        Consorcio consorcio = consorcioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consorcio no encontrado con id: " + id));

        consorcio.setNombre(requestDTO.nombre());
        consorcio.setDireccion(requestDTO.direccion());
        consorcio.setCiudad(requestDTO.ciudad());
        consorcio.setCuit(requestDTO.cuit());
        if (requestDTO.administracionId() != null) {
            Administracion administracion = administracionRepository.findById(requestDTO.administracionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Administración no encontrada con id: " + requestDTO.administracionId()));
            consorcio.setAdministracion(administracion);
        }

        Consorcio updatedConsorcio = consorcioRepository.save(consorcio);
        return consorcioMapper.toDto(updatedConsorcio);
    }

    @Transactional
    public void deleteConsorcio(Long id) {
        Consorcio consorcio = consorcioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consorcio no encontrado con id: " + id));
        consorcioRepository.delete(consorcio);
    }

    @Transactional(readOnly = true)
    public List<Long> getConsorcioIdsForAuthenticatedAdmin() {
        Usuario authenticatedUser = securityService.getAuthenticatedUser();
        if (authenticatedUser.getConsorcios() == null || authenticatedUser.getConsorcios().isEmpty()) {
            throw new BusinessException("El usuario autenticado no pertenece a ningún consorcio");
        }

        List<Long> administracionIds = authenticatedUser.getConsorcios().stream()
                .map(Consorcio::getAdministracion)
                .filter(Objects::nonNull)
                .map(Administracion::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (administracionIds.isEmpty()) {
            throw new BusinessException("No se pudo determinar la administración del usuario autenticado");
        }
        if (administracionIds.size() > 1) {
            throw new BusinessException("El usuario administrador no puede pertenecer a más de una administración");
        }

        return consorcioRepository.findIdsByAdministracionId(administracionIds.get(0));
    }
}
