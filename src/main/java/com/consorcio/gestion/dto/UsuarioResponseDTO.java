package com.consorcio.gestion.dto;

import com.consorcio.gestion.enums.RolUsuario;
import lombok.Builder;

import java.util.Set;

@Builder
public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String email,
        RolUsuario rol,
        boolean activo,
        Set<Long> consorcioIds
) {
}
