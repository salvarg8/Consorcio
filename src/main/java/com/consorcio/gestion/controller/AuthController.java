package com.consorcio.gestion.controller;

import com.consorcio.gestion.dto.ApiResponseDTO;
import com.consorcio.gestion.dto.AuthRequestDTO;
import com.consorcio.gestion.dto.AuthResponseDTO;
import com.consorcio.gestion.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint para la autenticación de usuarios.
     * Recibe las credenciales del usuario y, si son válidas, devuelve un token JWT para futuras solicitudes.
     *
     * @param request {@link AuthRequestDTO} que contiene el email y la contraseña del usuario.
     * @return {@link ResponseEntity} con un {@link ApiResponseDTO} que encapsula el {@link AuthResponseDTO}
     *         con el token JWT y los datos del usuario, o un mensaje de error si la autenticación falla.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> login(@Valid @RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        
        return ResponseEntity.ok(ApiResponseDTO.<AuthResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Login exitoso")
                .data(response)
                .build());
    }
}
