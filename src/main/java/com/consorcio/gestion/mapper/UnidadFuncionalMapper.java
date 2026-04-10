package com.consorcio.gestion.mapper;

import com.consorcio.gestion.dto.UnidadFuncionalRequestDTO;
import com.consorcio.gestion.dto.UnidadFuncionalResponseDTO;
import com.consorcio.gestion.entity.UnidadFuncional;
import org.springframework.stereotype.Component;

@Component
public class UnidadFuncionalMapper {

    public static UnidadFuncional toEntity(UnidadFuncionalRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return UnidadFuncional.builder()
                .identificador(dto.getIdentificador())
                .piso(dto.getPiso())
                .descripcion(dto.getDescripcion())
                .build();
    }

    public static UnidadFuncionalResponseDTO toResponseDTO(UnidadFuncional entity) {
        if (entity == null) {
            return null;
        }
        return UnidadFuncionalResponseDTO.builder()
                .id(entity.getId())
                .identificador(entity.getIdentificador())
                .piso(entity.getPiso())
                .descripcion(entity.getDescripcion())
                .activa(entity.isActiva())
                .consorcioId(entity.getConsorcio() != null ? entity.getConsorcio().getId() : null)
                .propietario(entity.getPropietario() != null ? UsuarioMapper.toResponseDTO(entity.getPropietario()) : null)
                .inquilino(entity.getInquilino() != null ? UsuarioMapper.toResponseDTO(entity.getInquilino()) : null)
                .build();
    }
}
