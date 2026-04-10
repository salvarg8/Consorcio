package com.consorcio.gestion.mapper;

import com.consorcio.gestion.dto.ReservaResponseDTO;
import com.consorcio.gestion.entity.ReservaAmenity;
import org.springframework.stereotype.Component;

@Component
public class ReservaMapper {

    public ReservaResponseDTO toResponseDTO(ReservaAmenity entity) {
        if (entity == null) {
            return null;
        }
        return ReservaResponseDTO.builder()
                .id(entity.getId())
                .unidadFuncionalId(entity.getUnidadFuncional() != null ? entity.getUnidadFuncional().getId() : null)
                .unidadFuncionalIdentificador(entity.getUnidadFuncional() != null ? entity.getUnidadFuncional().getIdentificador() : null)
                .amenityId(entity.getAmenity() != null ? entity.getAmenity().getId() : null)
                .amenityNombre(entity.getAmenity() != null ? entity.getAmenity().getNombre() : null)
                .fecha(entity.getFecha())
                .horaInicio(entity.getHoraInicio())
                .horaFin(entity.getHoraFin())
                .estado(entity.getEstado())
                .observaciones(entity.getObservaciones())
                .usuarioCreadorEmail(entity.getUsuarioCreador() != null ? entity.getUsuarioCreador().getEmail() : null)
                .consorcioId(entity.getUnidadFuncional() != null && entity.getUnidadFuncional().getConsorcio() != null ? entity.getUnidadFuncional().getConsorcio().getId() : null)
                .build();
    }
}
