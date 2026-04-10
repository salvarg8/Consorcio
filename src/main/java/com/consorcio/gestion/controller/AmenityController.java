package com.consorcio.gestion.controller;

import com.consorcio.gestion.dto.AmenityRequestDTO;
import com.consorcio.gestion.dto.AmenityResponseDTO;
import com.consorcio.gestion.dto.ApiResponseDTO;
import com.consorcio.gestion.security.SecurityService;
import com.consorcio.gestion.service.AmenityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;
    private final SecurityService securityService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<AmenityResponseDTO>> create(@Valid @RequestBody AmenityRequestDTO request) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        AmenityResponseDTO response = amenityService.create(request, consorcioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDTO.success(response, "Amenity creado exitosamente")
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO', 'PROPIETARIO', 'INQUILINO')")
    public ResponseEntity<ApiResponseDTO<Page<AmenityResponseDTO>>> findAll(Pageable pageable) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        Page<AmenityResponseDTO> response = amenityService.findAllByConsorcioId(consorcioId, pageable);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Amenities obtenidos exitosamente")
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO', 'PROPIETARIO', 'INQUILINO')")
    public ResponseEntity<ApiResponseDTO<AmenityResponseDTO>> findById(@PathVariable Long id) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        AmenityResponseDTO response = amenityService.findById(id, consorcioId);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Amenity obtenido exitosamente")
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<AmenityResponseDTO>> update(
            @PathVariable Long id, 
            @Valid @RequestBody AmenityRequestDTO request) {
        
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        AmenityResponseDTO response = amenityService.update(id, request, consorcioId);
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Amenity actualizado exitosamente")
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        Long consorcioId = securityService.getConsorcioIdForCurrentUser();
        amenityService.delete(id, consorcioId);
        return ResponseEntity.ok(
                ApiResponseDTO.success(null, "Amenity desactivado exitosamente")
        );
    }
}
