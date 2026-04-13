package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.LiquidacionMensualResponseDTO;

import java.math.BigDecimal;

public interface LiquidacionService {
    LiquidacionMensualResponseDTO generar(String periodo, BigDecimal gastoComunMes, Long consorcioId);
    LiquidacionMensualResponseDTO publicar(Long id, Long consorcioId, boolean generarPagosPendientes);
    LiquidacionMensualResponseDTO getLiquidacionByPeriodo(String periodo, Long consorcioId);
}
