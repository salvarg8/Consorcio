package com.consorcio.gestion.dto;

import lombok.Builder;

@Builder
public record AmenityResponseDTO(
        Long id,
        String nombre,
        String descripcion,
        Integer capacidadMaxima,
        boolean habilitado,
        Long consorcioId
) {
}
