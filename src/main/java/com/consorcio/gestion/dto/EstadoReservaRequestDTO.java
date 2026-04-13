package com.consorcio.gestion.dto;

import com.consorcio.gestion.enums.EstadoReserva;
import jakarta.validation.constraints.NotNull;

public record EstadoReservaRequestDTO(
        @NotNull(message = "El estado no puede ser nulo")
        EstadoReserva estado
) {
}
