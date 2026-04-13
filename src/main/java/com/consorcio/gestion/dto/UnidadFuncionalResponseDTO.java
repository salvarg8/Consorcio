package com.consorcio.gestion.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UnidadFuncionalResponseDTO(
        Long id,
        String identificador,
        Integer piso,
        String descripcion,
        BigDecimal coeficiente,
        boolean activa,
        Long consorcioId,
        UsuarioResponseDTO propietario,
        UsuarioResponseDTO inquilino
) {
}
