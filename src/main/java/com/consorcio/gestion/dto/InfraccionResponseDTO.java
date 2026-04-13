package com.consorcio.gestion.dto;

import com.consorcio.gestion.enums.EstadoInfraccion;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InfraccionResponseDTO(
        Long id,
        Long unidadFuncionalId,
        String unidadFuncionalIdentificador,
        LocalDate fecha,
        String motivo,
        String descripcion,
        BigDecimal montoPenalizacion,
        EstadoInfraccion estado,
        Long consorcioId
) {
}
