package com.consorcio.gestion.dto;

import com.consorcio.gestion.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private RolUsuario rol;
    private boolean activo;
    private Set<Long> consorcioIds;
}
