package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.AmenityRequestDTO;
import com.consorcio.gestion.dto.AmenityResponseDTO;
import com.consorcio.gestion.entity.Amenity;
import com.consorcio.gestion.entity.Consorcio;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.exception.ResourceNotFoundException;
import com.consorcio.gestion.mapper.AmenityMapper;
import com.consorcio.gestion.repository.AmenityRepository;
import com.consorcio.gestion.repository.ConsorcioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AmenityService {

    private final AmenityRepository amenityRepository;
    private final ConsorcioRepository consorcioRepository;
    private final AmenityMapper amenityMapper;

    @Transactional
    public AmenityResponseDTO create(AmenityRequestDTO request, Long consorcioId) {
        Long resolvedConsorcioId = request.consorcioId() != null ? request.consorcioId() : consorcioId;
        Consorcio consorcio = consorcioRepository.findById(resolvedConsorcioId)
                .orElseThrow(() -> new ResourceNotFoundException("Consorcio no encontrado con id: " + resolvedConsorcioId));

        if (amenityRepository.existsByNombreAndConsorcioId(request.nombre(), resolvedConsorcioId)) {
            throw new BusinessException("Ya existe un amenity con ese nombre en el consorcio");
        }

        Amenity amenity = amenityMapper.toEntity(request);
        amenity.setConsorcio(consorcio);
        return amenityMapper.toResponseDTO(amenityRepository.save(amenity));
    }

    @Transactional(readOnly = true)
    public Page<AmenityResponseDTO> findAllByConsorcioId(Long consorcioId, Pageable pageable) {
        return amenityRepository.findByConsorcioId(consorcioId, pageable)
                .map(amenityMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public AmenityResponseDTO findById(Long id, Long consorcioId) {
        Amenity amenity = getAmenityEntityByConsorcio(id, consorcioId);
        return amenityMapper.toResponseDTO(amenity);
    }

    @Transactional
    public AmenityResponseDTO update(Long id, AmenityRequestDTO request, Long consorcioId) {
        Amenity amenity = getAmenityEntityByConsorcio(id, consorcioId);
        
        if (!amenity.getNombre().equals(request.nombre()) &&
                amenityRepository.existsByNombreAndConsorcioId(request.nombre(), consorcioId)) {
            throw new BusinessException("Ya existe otro amenity con ese nombre en el consorcio");
        }

        amenity.setNombre(request.nombre());
        amenity.setDescripcion(request.descripcion());
        amenity.setCapacidadMaxima(request.capacidadMaxima());
        amenity.setCosto(request.costo());

        return amenityMapper.toResponseDTO(amenityRepository.save(amenity));
    }

    @Transactional
    public void delete(Long id, Long consorcioId) {
        Amenity amenity = getAmenityEntityByConsorcio(id, consorcioId);
        amenity.setHabilitado(false);
        amenityRepository.save(amenity);
    }

    protected Amenity getAmenityEntityByConsorcio(Long id, Long consorcioId) {
        return amenityRepository.findByIdAndConsorcioId(id, consorcioId)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity no encontrado con ID: " + id + " en este consorcio"));
    }
}
