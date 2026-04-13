package com.consorcio.gestion.mapper;

import com.consorcio.gestion.dto.ConsorcioRequestDTO;
import com.consorcio.gestion.dto.ConsorcioResponseDTO;
import com.consorcio.gestion.entity.Consorcio;
import org.springframework.stereotype.Component;

@Component
public class ConsorcioMapper {

    public Consorcio toEntity(ConsorcioRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Consorcio consorcio = new Consorcio();
        consorcio.setNombre(dto.nombre());
        consorcio.setDireccion(dto.direccion());
        consorcio.setCuit(dto.cuit());
        return consorcio;
    }

    public ConsorcioResponseDTO toDto(Consorcio entity) {
        if (entity == null) {
            return null;
        }

        return ConsorcioResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .direccion(entity.getDireccion())
                .cuit(entity.getCuit())
                .activo(entity.isActivo())
                .build();
    }
}
