package com.consorcio.gestion.dto;

import com.consorcio.gestion.enums.EstadoReserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaResponseDTO {
    private Long id;
    private Long unidadFuncionalId;
    private String unidadFuncionalIdentificador;
    private Long amenityId;
    private String amenityNombre;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoReserva estado;
    private String observaciones;
    private String usuarioCreadorEmail;
    private Long consorcioId;
}
