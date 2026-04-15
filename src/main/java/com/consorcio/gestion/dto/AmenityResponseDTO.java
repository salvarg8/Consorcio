package com.consorcio.gestion.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AmenityResponseDTO(
        Long id,
        String nombre,
        String descripcion,
        Integer capacidadMaxima,
        BigDecimal costo,
        boolean habilitado,
        Long consorcioId
) {
}
