package com.consorcio.gestion.dto;

import com.consorcio.gestion.enums.EstadoPago;
import jakarta.validation.constraints.NotNull;

public record EstadoPagoRequestDTO(
        @NotNull(message = "El estado no puede ser nulo")
        EstadoPago estado
) {
}
