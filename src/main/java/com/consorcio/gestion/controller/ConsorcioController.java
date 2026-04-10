package com.consorcio.gestion.controller;

import com.consorcio.gestion.dto.ApiResponseDTO;
import com.consorcio.gestion.dto.ConsorcioRequestDTO;
import com.consorcio.gestion.dto.ConsorcioResponseDTO;
import com.consorcio.gestion.service.ConsorcioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consorcios")
@RequiredArgsConstructor
public class ConsorcioController {

    private final ConsorcioService consorcioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<ConsorcioResponseDTO>>> getAllConsorcios() {
        List<ConsorcioResponseDTO> consorcios = consorcioService.getAllConsorcios();
        return ResponseEntity.ok(ApiResponseDTO.success(consorcios, "Consorcios obtenidos exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<ConsorcioResponseDTO>> getConsorcioById(@PathVariable Long id) {
        ConsorcioResponseDTO consorcio = consorcioService.getConsorcioById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(consorcio, "Consorcio obtenido exitosamente"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<ConsorcioResponseDTO>> createConsorcio(@RequestBody ConsorcioRequestDTO requestDTO) {
        ConsorcioResponseDTO consorcio = consorcioService.createConsorcio(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(consorcio, "Consorcio creado exitosamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<ConsorcioResponseDTO>> updateConsorcio(
            @PathVariable Long id, @RequestBody ConsorcioRequestDTO requestDTO) {
        ConsorcioResponseDTO consorcio = consorcioService.updateConsorcio(id, requestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success(consorcio, "Consorcio actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteConsorcio(@PathVariable Long id) {
        consorcioService.deleteConsorcio(id);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Consorcio eliminado exitosamente"));
    }
}
