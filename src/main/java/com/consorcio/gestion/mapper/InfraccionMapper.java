package com.consorcio.gestion.mapper;

import com.consorcio.gestion.dto.InfraccionResponseDTO;
import com.consorcio.gestion.entity.Infraccion;
import org.springframework.stereotype.Component;

@Component
public class InfraccionMapper {

    public InfraccionResponseDTO toResponseDTO(Infraccion entity) {
        if (entity == null) {
            return null;
        }
        return InfraccionResponseDTO.builder()
                .id(entity.getId())
                .unidadFuncionalId(entity.getUnidadFuncional() != null ? entity.getUnidadFuncional().getId() : null)
                .unidadFuncionalIdentificador(entity.getUnidadFuncional() != null ? entity.getUnidadFuncional()
                        .getIdentificador() : null)
                .fecha(entity.getFecha())
                .motivo(entity.getMotivo())
                .descripcion(entity.getDescripcion())
                .montoPenalizacion(entity.getMontoPenalizacion())
                .estado(entity.getEstado())
                .consorcioId(entity.getUnidadFuncional() != null && entity.getUnidadFuncional().getConsorcio() != null ?
                        entity.getUnidadFuncional().getConsorcio().getId() : null)
                .build();
    }
}
