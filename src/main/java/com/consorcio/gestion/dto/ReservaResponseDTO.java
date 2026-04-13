package com.consorcio.gestion.dto;

import com.consorcio.gestion.enums.EstadoReserva;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record ReservaResponseDTO(
        Long id,
        Long unidadFuncionalId,
        String unidadFuncionalIdentificador,
        Long amenityId,
        String amenityNombre,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        EstadoReserva estado,
        String observaciones,
        String usuarioCreadorEmail,
        Long consorcioId
) {
}
