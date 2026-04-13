package com.consorcio.gestion.mapper;

import com.consorcio.gestion.dto.UsuarioRequestDTO;
import com.consorcio.gestion.dto.UsuarioResponseDTO;
import com.consorcio.gestion.entity.Consorcio;
import com.consorcio.gestion.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Usuario.builder()
                .nombre(dto.nombre())
                .apellido(dto.apellido())
                .email(dto.email())
                .password(dto.password())
                .rol(dto.rol())
                .build();
    }

    public UsuarioResponseDTO toResponseDTO(Usuario entity) {
        if (entity == null) {
            return null;
        }
        return UsuarioResponseDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .email(entity.getEmail())
                .rol(entity.getRol())
                .activo(entity.isActivo())
                .consorcioIds(entity.getConsorcios() != null ? 
                    entity.getConsorcios().stream().map(Consorcio::getId).collect(Collectors.toSet()) : null)
                .build();
    }
}
