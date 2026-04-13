package com.consorcio.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AmenityRequestDTO(
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,

        String descripcion,

        @NotNull(message = "La capacidad máxima no puede ser nula")
        @Positive(message = "La capacidad máxima debe ser un número positivo")
        Integer capacidadMaxima
) {
}
