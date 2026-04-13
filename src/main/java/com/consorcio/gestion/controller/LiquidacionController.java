package com.consorcio.gestion.controller;

import com.consorcio.gestion.dto.ApiResponseDTO;
import com.consorcio.gestion.dto.LiquidacionGenerarRequestDTO;
import com.consorcio.gestion.dto.LiquidacionMensualResponseDTO;
import com.consorcio.gestion.dto.LiquidacionPublicarRequestDTO;
import com.consorcio.gestion.service.LiquidacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/liquidaciones")
@RequiredArgsConstructor
public class LiquidacionController {

    private final LiquidacionService liquidacionService;

    @PostMapping("/generar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ENCARGADO')")
    public ResponseEntity<ApiResponseDTO<LiquidacionMensualResponseDTO>> generarLiquidacion(
            @RequestParam("consorcioId") Long consorcioId,
            @Valid @RequestBody LiquidacionGenerarRequestDTO request) {
        
        LiquidacionMensualResponseDTO response = liquidacionService.generar(request.getPeriodo(), request.getGastoComunMes(), consorcioId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDTO.created(response, "Liquidación generada en borrador con éxito")
        );
    }

    @PostMapping("/{id}/publicar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ENCARGADO')")
    public ResponseEntity<ApiResponseDTO<LiquidacionMensualResponseDTO>> publicarLiquidacion(
            @PathVariable("id") Long id,
            @RequestParam("consorcioId") Long consorcioId,
            @Valid @RequestBody LiquidacionPublicarRequestDTO request) {
        
        LiquidacionMensualResponseDTO response = liquidacionService.publicar(id, consorcioId, request.getGenerarPagosPendientes());
        
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Liquidación publicada con éxito")
        );
    }

    @GetMapping("/{periodo}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ENCARGADO')")
    public ResponseEntity<ApiResponseDTO<LiquidacionMensualResponseDTO>> getLiquidacionPorPeriodo(
            @PathVariable("periodo") String periodo,
            @RequestParam("consorcioId") Long consorcioId) {
        
        LiquidacionMensualResponseDTO response = liquidacionService.getLiquidacionByPeriodo(periodo, consorcioId);
        
        return ResponseEntity.ok(
                ApiResponseDTO.success(response, "Liquidación obtenida con éxito")
        );
    }
}
