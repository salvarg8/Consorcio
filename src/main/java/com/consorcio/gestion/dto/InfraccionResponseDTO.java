package com.consorcio.gestion.dto;

import com.consorcio.gestion.enums.EstadoInfraccion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfraccionResponseDTO {
    private Long id;
    private Long unidadFuncionalId;
    private String unidadFuncionalIdentificador;
    private LocalDate fecha;
    private String motivo;
    private String descripcion;
    private BigDecimal montoPenalizacion;
    private EstadoInfraccion estado;
    private Long consorcioId;
}
