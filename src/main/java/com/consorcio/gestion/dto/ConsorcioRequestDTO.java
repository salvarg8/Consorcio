package com.consorcio.gestion.dto;

import lombok.Builder;

@Builder
public record ConsorcioRequestDTO(
        String nombre,
        String direccion,
        String ciudad,
        String cuit,
        Long administracionId
) {
}
