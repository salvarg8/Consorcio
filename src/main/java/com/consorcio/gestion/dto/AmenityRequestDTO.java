package com.consorcio.gestion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AmenityRequestDTO(
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,

        String descripcion,

        @NotNull(message = "La capacidad máxima no puede ser nula")
        @Positive(message = "La capacidad máxima debe ser un número positivo")
        Integer capacidadMaxima,

        Long consorcioId,

        @NotNull(message = "El costo no puede ser nulo")
        @DecimalMin(value = "0.0", inclusive = false, message = "El costo debe ser mayor a 0")
        BigDecimal costo
) {
}
