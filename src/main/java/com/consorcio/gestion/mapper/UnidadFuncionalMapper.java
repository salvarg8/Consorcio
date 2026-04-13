package com.consorcio.gestion.mapper;

import com.consorcio.gestion.dto.UnidadFuncionalRequestDTO;
import com.consorcio.gestion.dto.UnidadFuncionalResponseDTO;
import com.consorcio.gestion.entity.UnidadFuncional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnidadFuncionalMapper {

    private final UsuarioMapper usuarioMapper;

    public UnidadFuncional toEntity(UnidadFuncionalRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return UnidadFuncional.builder()
                .identificador(dto.getIdentificador())
                .piso(dto.getPiso())
                .descripcion(dto.getDescripcion())
                .coeficiente(dto.getCoeficiente())
                .build();
    }

    public UnidadFuncionalResponseDTO toResponseDTO(UnidadFuncional entity) {
        if (entity == null) {
            return null;
        }
        return UnidadFuncionalResponseDTO.builder()
                .id(entity.getId())
                .identificador(entity.getIdentificador())
                .piso(entity.getPiso())
                .descripcion(entity.getDescripcion())
                .coeficiente(entity.getCoeficiente())
                .activa(entity.isActiva())
                .consorcioId(entity.getConsorcio() != null ? entity.getConsorcio().getId() : null)
                .propietario(entity.getPropietario() != null ? usuarioMapper.toResponseDTO(entity.getPropietario()) : null)
                .inquilino(entity.getInquilino() != null ? usuarioMapper.toResponseDTO(entity.getInquilino()) : null)
                .build();
    }
}
