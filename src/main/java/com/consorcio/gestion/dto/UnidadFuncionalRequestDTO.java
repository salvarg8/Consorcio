package com.consorcio.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnidadFuncionalRequestDTO {
    private String identificador;
    private Integer piso;
    private String descripcion;
    private Long consorcioId;
}
