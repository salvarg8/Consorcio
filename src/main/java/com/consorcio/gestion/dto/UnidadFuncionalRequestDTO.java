package com.consorcio.gestion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnidadFuncionalRequestDTO {
    
    @NotBlank(message = "El identificador no puede estar vacío")
    private String identificador;
    
    @NotNull(message = "El piso es obligatorio")
    private Integer piso;
    
    private String descripcion;
    
    private Long consorcioId;

    @NotNull(message = "El coeficiente es obligatorio")
    @DecimalMin(value = "0.00000001", message = "El coeficiente debe ser mayor a 0")
    private BigDecimal coeficiente;
}
