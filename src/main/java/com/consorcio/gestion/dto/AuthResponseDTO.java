package com.consorcio.gestion.dto;

import lombok.Builder;

@Builder
public record AuthResponseDTO(
        String token,
        UsuarioResponseDTO usuario
) {
}
