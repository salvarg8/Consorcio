package com.consorcio.gestion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquidacionPublicarRequestDTO {
    @NotNull(message = "Generar pagos pendientes es obligatorio")
    private Boolean generarPagosPendientes;
}
