package com.consorcio.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoPendienteRequestDTO(
        @NotNull(message = "El ID de la unidad funcional no puede ser nulo")
        Long unidadFuncionalId,

        @NotBlank(message = "El concepto no puede estar vacío")
        String concepto,

        String descripcion,

        @NotNull(message = "El monto no puede ser nulo")
        @PositiveOrZero(message = "El monto no puede ser negativo")
        BigDecimal monto,

        @NotNull(message = "La fecha de vencimiento no puede ser nula")
        LocalDate fechaVencimiento
) {
}
