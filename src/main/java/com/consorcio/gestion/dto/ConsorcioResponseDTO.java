package com.consorcio.gestion.dto;

import lombok.Builder;

@Builder
public record ConsorcioResponseDTO(
        Long id,
        String nombre,
        String direccion,
        String cuit,
        boolean activo
) {
}
