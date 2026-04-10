package com.consorcio.gestion.controller;

import com.consorcio.gestion.dto.ApiResponseDTO;
import com.consorcio.gestion.dto.UsuarioRequestDTO;
import com.consorcio.gestion.dto.UsuarioResponseDTO;
import com.consorcio.gestion.security.SecurityService;
import com.consorcio.gestion.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final SecurityService securityService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<UsuarioResponseDTO>> create(@Valid @RequestBody UsuarioRequestDTO request) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        UsuarioResponseDTO response = usuarioService.create(request, consorcioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDTO.created(response, "Usuario creado exitosamente")
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<ApiResponseDTO<Page<UsuarioResponseDTO>>> findAll(Pageable pageable) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        Page<UsuarioResponseDTO> response = usuarioService.findAllByConsorcioId(consorcioId, pageable);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Usuarios obtenidos exitosamente")
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO') or (hasAnyRole('PROPIETARIO', 'INQUILINO') and @securityService.isOwner(#id))")
    public ResponseEntity<ApiResponseDTO<UsuarioResponseDTO>> findById(@PathVariable Long id) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        UsuarioResponseDTO response = usuarioService.findById(id, consorcioId);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Usuario obtenido exitosamente")
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<UsuarioResponseDTO>> update(
            @PathVariable Long id, 
            @Valid @RequestBody UsuarioRequestDTO request) {
        
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        UsuarioResponseDTO response = usuarioService.update(id, request, consorcioId);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Usuario actualizado exitosamente")
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        usuarioService.delete(id, consorcioId);
        return ResponseEntity.ok(
                ApiResponseDTO.success(null, "Usuario desactivado exitosamente")
        );
    }
}
