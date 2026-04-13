package com.consorcio.gestion.controller;

import com.consorcio.gestion.dto.ApiResponseDTO;
import com.consorcio.gestion.dto.PagoPendienteRequestDTO;
import com.consorcio.gestion.dto.PagoPendienteResponseDTO;
import com.consorcio.gestion.security.SecurityService;
import com.consorcio.gestion.service.PagoPendienteService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PagoPendienteController {

    private final PagoPendienteService pagoPendienteService;
    private final SecurityService securityService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<ApiResponseDTO<PagoPendienteResponseDTO>> create(@Valid @RequestBody PagoPendienteRequestDTO request) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        PagoPendienteResponseDTO response = pagoPendienteService.create(request, consorcioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDTO.created(response, "Pago pendiente creado exitosamente")
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<ApiResponseDTO<Page<PagoPendienteResponseDTO>>> findAll(Pageable pageable) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        Page<PagoPendienteResponseDTO> response = pagoPendienteService.findAllByConsorcioId(consorcioId, pageable);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Pagos pendientes obtenidos exitosamente")
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO', 'PROPIETARIO', 'INQUILINO')")
    public ResponseEntity<ApiResponseDTO<PagoPendienteResponseDTO>> findById(@PathVariable Long id) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        PagoPendienteResponseDTO response = pagoPendienteService.findById(id, consorcioId);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Pago pendiente obtenido exitosamente")
        );
    }

    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<ApiResponseDTO<PagoPendienteResponseDTO>> pay(@PathVariable Long id) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        PagoPendienteResponseDTO response = pagoPendienteService.pay(id, consorcioId);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Pago procesado exitosamente")
        );
    }
}
