package com.consorcio.gestion.dto;

import com.consorcio.gestion.enums.EstadoPago;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record PagoPendienteResponseDTO(
        Long id,
        Long unidadFuncionalId,
        String unidadFuncionalIdentificador,
        String concepto,
        String descripcion,
        BigDecimal monto,
        LocalDate fechaVencimiento,
        EstadoPago estado,
        LocalDateTime fechaPago,
        Long consorcioId
) {
}
