package com.consorcio.gestion.dto;

import com.consorcio.gestion.enums.EstadoInfraccion;
import jakarta.validation.constraints.NotNull;

public record EstadoInfraccionRequestDTO(
        @NotNull(message = "El estado no puede ser nulo")
        EstadoInfraccion estado
) {
}
