package com.consorcio.gestion.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaRequestDTO(
        @NotNull(message = "El ID de la unidad funcional no puede ser nulo")
        Long unidadFuncionalId,

        @NotNull(message = "El ID del amenity no puede ser nulo")
        Long amenityId,

        @NotNull(message = "La fecha no puede ser nula")
        @FutureOrPresent(message = "La fecha de reserva debe ser presente o futura")
        LocalDate fecha,

        @NotNull(message = "La hora de inicio no puede ser nula")
        LocalTime horaInicio,

        @NotNull(message = "La hora de fin no puede ser nula")
        LocalTime horaFin,

        String observaciones
) {
}
