package com.consorcio.gestion.dto;

import lombok.Builder;

@Builder
public record UnidadFuncionalResponseDTO(
        Long id,
        String identificador,
        Integer piso,
        String descripcion,
        boolean activa,
        Long consorcioId,
        UsuarioResponseDTO propietario,
        UsuarioResponseDTO inquilino
) {
}
