package com.consorcio.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoPendienteRequestDTO {

    @NotNull(message = "El ID de la unidad funcional no puede ser nulo")
    private Long unidadFuncionalId;

    @NotBlank(message = "El concepto no puede estar vacío")
    private String concepto;

    private String descripcion;

    @NotNull(message = "El monto no puede ser nulo")
    @PositiveOrZero(message = "El monto no puede ser negativo")
    private BigDecimal monto;

    @NotNull(message = "La fecha de vencimiento no puede ser nula")
    private LocalDate fechaVencimiento;
}
