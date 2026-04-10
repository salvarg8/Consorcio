package com.consorcio.gestion.controller;

import com.consorcio.gestion.dto.ApiResponseDTO;
import com.consorcio.gestion.dto.EstadoInfraccionRequestDTO;
import com.consorcio.gestion.dto.InfraccionRequestDTO;
import com.consorcio.gestion.dto.InfraccionResponseDTO;
import com.consorcio.gestion.security.SecurityService;
import com.consorcio.gestion.service.InfraccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/infractions")
@RequiredArgsConstructor
public class InfraccionController {

    private final InfraccionService infraccionService;
    private final SecurityService securityService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<ApiResponseDTO<InfraccionResponseDTO>> create(@Valid @RequestBody InfraccionRequestDTO request) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        InfraccionResponseDTO response = infraccionService.create(request, consorcioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDTO.created(response, "Infracción creada exitosamente")
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<ApiResponseDTO<Page<InfraccionResponseDTO>>> findAll(Pageable pageable) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        Page<InfraccionResponseDTO> response = infraccionService.findAllByConsorcioId(consorcioId, pageable);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Infracciones obtenidas exitosamente")
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO', 'PROPIETARIO', 'INQUILINO')")
    public ResponseEntity<ApiResponseDTO<InfraccionResponseDTO>> findById(@PathVariable Long id) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        InfraccionResponseDTO response = infraccionService.findById(id, consorcioId);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Infracción obtenida exitosamente")
        );
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<ApiResponseDTO<InfraccionResponseDTO>> updateStatus(
            @PathVariable Long id, 
            @Valid @RequestBody EstadoInfraccionRequestDTO request) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        InfraccionResponseDTO response = infraccionService.updateStatus(id, request, consorcioId);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Estado de infracción actualizado exitosamente")
        );
    }
}
