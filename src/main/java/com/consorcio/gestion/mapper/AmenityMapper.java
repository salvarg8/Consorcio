package com.consorcio.gestion.mapper;

import com.consorcio.gestion.dto.AmenityRequestDTO;
import com.consorcio.gestion.dto.AmenityResponseDTO;
import com.consorcio.gestion.entity.Amenity;
import org.springframework.stereotype.Component;

@Component
public class AmenityMapper {

    public Amenity toEntity(AmenityRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Amenity.builder()
                .nombre(dto.nombre())
                .descripcion(dto.descripcion())
                .capacidadMaxima(dto.capacidadMaxima())
                .costo(dto.costo())
                .build();
    }

    public AmenityResponseDTO toResponseDTO(Amenity entity) {
        if (entity == null) {
            return null;
        }
        return AmenityResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .capacidadMaxima(entity.getCapacidadMaxima())
                .costo(entity.getCosto())
                .habilitado(entity.isHabilitado())
                .consorcioId(entity.getConsorcio() != null ? entity.getConsorcio().getId() : null)
                .build();
    }
}
