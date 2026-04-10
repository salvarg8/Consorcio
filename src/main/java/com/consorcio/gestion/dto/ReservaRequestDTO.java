package com.consorcio.gestion.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {

    @NotNull(message = "El ID de la unidad funcional no puede ser nulo")
    private Long unidadFuncionalId;

    @NotNull(message = "El ID del amenity no puede ser nulo")
    private Long amenityId;

    @NotNull(message = "La fecha no puede ser nula")
    @FutureOrPresent(message = "La fecha de reserva debe ser presente o futura")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio no puede ser nula")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin no puede ser nula")
    private LocalTime horaFin;

    private String observaciones;
}
