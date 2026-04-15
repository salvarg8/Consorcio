package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.ConsorcioRequestDTO;
import com.consorcio.gestion.dto.ConsorcioResponseDTO;
import com.consorcio.gestion.entity.Administracion;
import com.consorcio.gestion.entity.Consorcio;
import com.consorcio.gestion.exception.ResourceNotFoundException;
import com.consorcio.gestion.mapper.ConsorcioMapper;
import com.consorcio.gestion.repository.AdministracionRepository;
import com.consorcio.gestion.repository.ConsorcioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsorcioService {

    private final ConsorcioRepository consorcioRepository;
    private final AdministracionRepository administracionRepository;
    private final ConsorcioMapper consorcioMapper;

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
}
