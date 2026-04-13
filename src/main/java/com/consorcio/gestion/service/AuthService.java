package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.AuthRequestDTO;
import com.consorcio.gestion.dto.AuthResponseDTO;
import com.consorcio.gestion.entity.Usuario;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.exception.ResourceNotFoundException;
import com.consorcio.gestion.mapper.UsuarioMapper;
import com.consorcio.gestion.repository.UsuarioRepository;
import com.consorcio.gestion.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper mapper;

    /**
     * Autentica a un usuario utilizando sus credenciales (email y contraseña).
     * Si las credenciales son válidas y el usuario está activo, genera y retorna un token JWT.
     *
     * @param request DTO que contiene el email y la contraseña del usuario que intenta ingresar.
     * @return {@link AuthResponseDTO} conteniendo el token JWT generado y la información básica del usuario.
     * @throws ResourceNotFoundException si no existe un usuario registrado con el email proporcionado.
     * @throws BusinessException si el usuario existe pero se encuentra en estado inactivo.
     * @throws org.springframework.security.core.AuthenticationException si las credenciales provistas son inválidas.
     */
    public AuthResponseDTO login(AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        Usuario user = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!user.isActivo()) {
            throw new BusinessException("El usuario está inactivo");
        }

        org.springframework.security.core.userdetails.User userDetails =
                (org.springframework.security.core.userdetails.User) org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRol().name())
                        .disabled(!user.isActivo())
                        .build();

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("rol", user.getRol().name());
        extraClaims.put("id", user.getId());

        String jwtToken = jwtService.generateToken(extraClaims, userDetails);

        return AuthResponseDTO.builder()
                .token(jwtToken)
                .usuario(mapper.toResponseDTO(user))
                .build();
    }
}
