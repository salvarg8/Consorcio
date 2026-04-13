package com.consorcio.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InfraccionRequestDTO(
        @NotNull(message = "El ID de la unidad funcional no puede ser nulo")
        Long unidadFuncionalId,

        @NotNull(message = "La fecha no puede ser nula")
        @PastOrPresent(message = "La fecha de la infracción no puede ser futura")
        LocalDate fecha,

        @NotBlank(message = "El motivo no puede estar vacío")
        String motivo,

        String descripcion,

        @NotNull(message = "El monto no puede ser nulo")
        @PositiveOrZero(message = "El monto no puede ser negativo")
        BigDecimal montoPenalizacion
) {
}
