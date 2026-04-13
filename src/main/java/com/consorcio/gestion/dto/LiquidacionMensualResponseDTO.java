package com.consorcio.gestion.dto;

import com.consorcio.gestion.enums.EstadoLiquidacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquidacionMensualResponseDTO {
    private Long id;
    private Long consorcioId;
    private String periodo;
    private BigDecimal gastoComunMes;
    private EstadoLiquidacion estado;
    private List<LiquidacionUnidadResponseDTO> detalles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
