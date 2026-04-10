package com.consorcio.gestion.controller;

import com.consorcio.gestion.dto.ApiResponseDTO;
import com.consorcio.gestion.dto.UnidadFuncionalRequestDTO;
import com.consorcio.gestion.dto.UnidadFuncionalResponseDTO;
import com.consorcio.gestion.security.SecurityService;
import com.consorcio.gestion.service.UnidadFuncionalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/unidades")
@RequiredArgsConstructor
public class UnidadFuncionalController {

    private final UnidadFuncionalService unidadFuncionalService;
    private final SecurityService securityService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<UnidadFuncionalResponseDTO>> create(
            @Valid @RequestBody UnidadFuncionalRequestDTO request) {
        
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        request.setConsorcioId(consorcioId);
        
        UnidadFuncionalResponseDTO response = unidadFuncionalService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDTO.success(response, "Unidad Funcional creada exitosamente")
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<ApiResponseDTO<Page<UnidadFuncionalResponseDTO>>> findAll(Pageable pageable) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        Page<UnidadFuncionalResponseDTO> response = unidadFuncionalService.findAllByConsorcioId(consorcioId, pageable);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Unidades Funcionales obtenidas exitosamente")
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO', 'PROPIETARIO', 'INQUILINO')")
    public ResponseEntity<ApiResponseDTO<UnidadFuncionalResponseDTO>> findById(@PathVariable Long id) {
        // Validación adicional en el service para asegurar que la unidad pertenece
        // al consorcio del usuario logueado.
        UnidadFuncionalResponseDTO response = unidadFuncionalService.findById(id);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Unidad Funcional obtenida exitosamente")
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<UnidadFuncionalResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody UnidadFuncionalRequestDTO request) {
        
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        request.setConsorcioId(consorcioId);
        
        UnidadFuncionalResponseDTO response = unidadFuncionalService.update(id, request);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Unidad Funcional actualizada exitosamente")
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        unidadFuncionalService.delete(id);
        return ResponseEntity.ok(
                ApiResponseDTO.success(null, "Unidad Funcional desactivada exitosamente")
        );
    }

    @PatchMapping("/{id}/assign-owner/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<UnidadFuncionalResponseDTO>> assignOwner(
            @PathVariable Long id,
            @PathVariable Long userId) {
        UnidadFuncionalResponseDTO response = unidadFuncionalService.assignOwner(id, userId);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Propietario asignado exitosamente")
        );
    }

    @PatchMapping("/{id}/assign-tenant/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROPIETARIO')")
    public ResponseEntity<ApiResponseDTO<UnidadFuncionalResponseDTO>> assignTenant(
            @PathVariable Long id,
            @PathVariable Long userId) {
        UnidadFuncionalResponseDTO response = unidadFuncionalService.assignInquilino(id, userId);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Inquilino asignado exitosamente")
        );
    }
}
