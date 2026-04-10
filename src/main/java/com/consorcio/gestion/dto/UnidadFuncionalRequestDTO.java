package com.consorcio.gestion.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnidadFuncionalRequestDTO {
    private String identificador;
    private Integer piso;
    private String descripcion;
    private Long consorcioId;
}
