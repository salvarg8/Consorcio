package com.consorcio.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfraccionRequestDTO {

    @NotNull(message = "El ID de la unidad funcional no puede ser nulo")
    private Long unidadFuncionalId;

    @NotNull(message = "La fecha no puede ser nula")
    @PastOrPresent(message = "La fecha de la infracción no puede ser futura")
    private LocalDate fecha;

    @NotBlank(message = "El motivo no puede estar vacío")
    private String motivo;

    private String descripcion;

    @NotNull(message = "El monto no puede ser nulo")
    @PositiveOrZero(message = "El monto no puede ser negativo")
    private BigDecimal montoPenalizacion;
}
