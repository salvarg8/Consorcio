package com.consorcio.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmenityResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer capacidadMaxima;
    private boolean habilitado;
    private Long consorcioId;
}
