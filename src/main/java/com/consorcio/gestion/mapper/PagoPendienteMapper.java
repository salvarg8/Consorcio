package com.consorcio.gestion.mapper;

import com.consorcio.gestion.dto.PagoPendienteResponseDTO;
import com.consorcio.gestion.entity.PagoPendiente;
import org.springframework.stereotype.Component;

@Component
public class PagoPendienteMapper {

    public PagoPendienteResponseDTO toResponseDTO(PagoPendiente entity) {
        if (entity == null) {
            return null;
        }
        return PagoPendienteResponseDTO.builder()
                .id(entity.getId())
                .unidadFuncionalId(entity.getUnidadFuncional() != null ? entity.getUnidadFuncional().getId() : null)
                .unidadFuncionalIdentificador(entity.getUnidadFuncional() != null ? entity.getUnidadFuncional()
                        .getIdentificador() : null)
                .concepto(entity.getConcepto())
                .descripcion(entity.getDescripcion())
                .monto(entity.getMonto())
                .fechaVencimiento(entity.getFechaVencimiento())
                .estado(entity.getEstado())
                .fechaPago(entity.getFechaPago())
                .consorcioId(entity.getUnidadFuncional() != null && entity.getUnidadFuncional().getConsorcio() != null
                        ? entity.getUnidadFuncional().getConsorcio().getId() : null)
                .build();
    }
}
