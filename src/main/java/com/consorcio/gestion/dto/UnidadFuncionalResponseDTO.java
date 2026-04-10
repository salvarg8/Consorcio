package com.consorcio.gestion.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnidadFuncionalResponseDTO {
    private Long id;
    private String identificador;
    private Integer piso;
    private String descripcion;
    private boolean activa;
    private Long consorcioId;
    private UsuarioResponseDTO propietario;
    private UsuarioResponseDTO inquilino;
}
