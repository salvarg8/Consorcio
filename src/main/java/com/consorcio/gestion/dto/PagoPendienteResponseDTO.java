package com.consorcio.gestion.dto;

import com.consorcio.gestion.enums.EstadoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoPendienteResponseDTO {
    private Long id;
    private Long unidadFuncionalId;
    private String unidadFuncionalIdentificador;
    private String concepto;
    private String descripcion;
    private BigDecimal monto;
    private LocalDate fechaVencimiento;
    private EstadoPago estado;
    private LocalDateTime fechaPago;
    private Long consorcioId;
}
