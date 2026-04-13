package com.consorcio.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquidacionGenerarRequestDTO {
    
    @NotBlank(message = "El período es obligatorio")
    @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "El período debe tener el formato YYYY-MM")
    private String periodo;

    @NotNull(message = "El gasto común del mes es obligatorio")
    @PositiveOrZero(message = "El gasto común no puede ser negativo")
    private BigDecimal gastoComunMes;
}
