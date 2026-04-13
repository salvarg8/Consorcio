package com.consorcio.gestion.controller;

import com.consorcio.gestion.dto.ApiResponseDTO;
import com.consorcio.gestion.dto.ReservaRequestDTO;
import com.consorcio.gestion.dto.ReservaResponseDTO;
import com.consorcio.gestion.security.SecurityService;
import com.consorcio.gestion.service.ReservaAmenityService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservaAmenityController {

    private final ReservaAmenityService reservaService;
    private final SecurityService securityService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO', 'PROPIETARIO', 'INQUILINO')")
    public ResponseEntity<ApiResponseDTO<ReservaResponseDTO>> create(@Valid @RequestBody ReservaRequestDTO request) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        ReservaResponseDTO response = reservaService.create(request, consorcioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDTO.success(response, "Reserva creada exitosamente")
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<ApiResponseDTO<Page<ReservaResponseDTO>>> findAll(Pageable pageable) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        Page<ReservaResponseDTO> response = reservaService.findAllByConsorcioId(consorcioId, pageable);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Reservas obtenidas exitosamente")
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO', 'PROPIETARIO', 'INQUILINO')")
    public ResponseEntity<ApiResponseDTO<ReservaResponseDTO>> findById(@PathVariable Long id) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        ReservaResponseDTO response = reservaService.findById(id, consorcioId);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Reserva obtenida exitosamente")
        );
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<ApiResponseDTO<ReservaResponseDTO>> confirm(@PathVariable Long id) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        ReservaResponseDTO response = reservaService.confirm(id, consorcioId);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Reserva confirmada exitosamente")
        );
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO', 'PROPIETARIO', 'INQUILINO')")
    public ResponseEntity<ApiResponseDTO<ReservaResponseDTO>> cancel(@PathVariable Long id) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        ReservaResponseDTO response = reservaService.cancel(id, consorcioId);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Reserva cancelada exitosamente")
        );
    }
}
