package com.consorcio.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquidacionUnidadResponseDTO {
    private Long id;
    private Long unidadFuncionalId;
    private String unidadFuncionalIdentificador;
    private BigDecimal coeficienteAplicado;
    private BigDecimal expensaBaseCalculada;
    private BigDecimal totalInfraccionesMes;
    private BigDecimal totalAmenitiesMes;
    private BigDecimal totalPagar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
