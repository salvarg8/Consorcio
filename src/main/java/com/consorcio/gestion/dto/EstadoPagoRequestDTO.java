package com.consorcio.gestion.dto;

import com.consorcio.gestion.enums.EstadoPago;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoPagoRequestDTO {
    @NotNull(message = "El estado no puede ser nulo")
    private EstadoPago estado;
}
