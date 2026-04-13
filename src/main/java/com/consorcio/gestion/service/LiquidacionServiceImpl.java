package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.LiquidacionMensualResponseDTO;
import com.consorcio.gestion.dto.LiquidacionUnidadResponseDTO;
import com.consorcio.gestion.entity.Consorcio;
import com.consorcio.gestion.entity.LiquidacionMensual;
import com.consorcio.gestion.entity.LiquidacionUnidad;
import com.consorcio.gestion.entity.PagoPendiente;
import com.consorcio.gestion.entity.UnidadFuncional;
import com.consorcio.gestion.enums.EstadoInfraccion;
import com.consorcio.gestion.enums.EstadoLiquidacion;
import com.consorcio.gestion.enums.EstadoPago;
import com.consorcio.gestion.enums.EstadoReserva;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.exception.ResourceNotFoundException;
import com.consorcio.gestion.repository.ConsorcioRepository;
import com.consorcio.gestion.repository.InfraccionRepository;
import com.consorcio.gestion.repository.LiquidacionMensualRepository;
import com.consorcio.gestion.repository.LiquidacionUnidadRepository;
import com.consorcio.gestion.repository.PagoPendienteRepository;
import com.consorcio.gestion.repository.ReservaAmenityRepository;
import com.consorcio.gestion.repository.UnidadFuncionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LiquidacionServiceImpl implements LiquidacionService {

    private final LiquidacionMensualRepository liquidacionMensualRepository;
    private final LiquidacionUnidadRepository liquidacionUnidadRepository;
    private final UnidadFuncionalRepository unidadFuncionalRepository;
    private final InfraccionRepository infraccionRepository;
    private final ReservaAmenityRepository reservaAmenityRepository;
    private final PagoPendienteRepository pagoPendienteRepository;
    private final ConsorcioRepository consorcioRepository;

    @Override
    @Transactional
    public LiquidacionMensualResponseDTO generar(String periodo, BigDecimal gastoComunMes, Long consorcioId) {
        if (liquidacionMensualRepository.findByConsorcioIdAndPeriodo(consorcioId, periodo).isPresent()) {
            throw new BusinessException("Ya existe una liquidación para el período " + periodo + " en este consorcio");
        }

        Consorcio consorcio = consorcioRepository.findById(consorcioId)
                .orElseThrow(() -> new ResourceNotFoundException("Consorcio no encontrado"));

        List<UnidadFuncional> unidadesActivas = unidadFuncionalRepository.findAllByConsorcioIdAndActivaTrue(consorcioId);
        
        if (unidadesActivas.isEmpty()) {
            throw new BusinessException("No hay unidades funcionales activas en el consorcio");
        }

        BigDecimal sumaCoeficientes = unidadesActivas.stream()
                .map(UnidadFuncional::getCoeficiente)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumaCoeficientes.subtract(BigDecimal.ONE).abs().compareTo(new BigDecimal("0.000001")) > 0) {
            throw new BusinessException("La suma de los coeficientes de las unidades activas debe ser 1.00000000");
        }

        YearMonth ym = YearMonth.parse(periodo);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        LiquidacionMensual liquidacionMensual = LiquidacionMensual.builder()
                .consorcio(consorcio)
                .periodo(periodo)
                .gastoComunMes(gastoComunMes)
                .estado(EstadoLiquidacion.BORRADOR)
                .liquidacionesUnidad(new ArrayList<>())
                .build();

        liquidacionMensual = liquidacionMensualRepository.save(liquidacionMensual);

        for (UnidadFuncional unidad : unidadesActivas) {
            BigDecimal coeficiente = unidad.getCoeficiente();
            BigDecimal expensaBase = gastoComunMes.multiply(coeficiente).setScale(2, RoundingMode.HALF_UP);

            BigDecimal totalInfracciones = infraccionRepository.sumTotalForUnitInPeriod(
                    unidad.getId(), startDate, endDate, EstadoInfraccion.PENDIENTE);

            BigDecimal totalAmenities = reservaAmenityRepository.sumTotalForUnitInPeriod(
                    unidad.getId(), startDate, endDate, EstadoReserva.CONFIRMADA);

            BigDecimal totalPagar = expensaBase.add(totalInfracciones).add(totalAmenities).setScale(2, RoundingMode.HALF_UP);

            LiquidacionUnidad liquidacionUnidad = LiquidacionUnidad.builder()
                    .liquidacionMensual(liquidacionMensual)
                    .unidadFuncional(unidad)
                    .coeficienteAplicado(coeficiente)
                    .expensaBaseCalculada(expensaBase)
                    .totalInfraccionesMes(totalInfracciones)
                    .totalAmenitiesMes(totalAmenities)
                    .totalPagar(totalPagar)
                    .build();

            liquidacionUnidad = liquidacionUnidadRepository.save(liquidacionUnidad);
            liquidacionMensual.getLiquidacionesUnidad().add(liquidacionUnidad);
        }

        return mapToResponseDTO(liquidacionMensual);
    }

    @Override
    @Transactional
    public LiquidacionMensualResponseDTO publicar(Long id, Long consorcioId, boolean generarPagosPendientes) {
        LiquidacionMensual liquidacion = liquidacionMensualRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada"));

        if (!liquidacion.getConsorcio().getId().equals(consorcioId)) {
            throw new BusinessException("La liquidación no pertenece al consorcio");
        }

        if (liquidacion.getEstado() == EstadoLiquidacion.PUBLICADA) {
            throw new BusinessException("La liquidación ya está publicada");
        }

        liquidacion.setEstado(EstadoLiquidacion.PUBLICADA);

        if (generarPagosPendientes) {
            String concepto = "EXPENSAS " + liquidacion.getPeriodo();
            LocalDate fechaVencimiento = YearMonth.parse(liquidacion.getPeriodo()).atDay(10).plusMonths(1); // Vence el 10 del mes siguiente, ej.
            
            for (LiquidacionUnidad lu : liquidacion.getLiquidacionesUnidad()) {
                if (!pagoPendienteRepository.existsByUnidadFuncionalIdAndConcepto(lu.getUnidadFuncional().getId(), concepto)) {
                    PagoPendiente pago = PagoPendiente.builder()
                            .unidadFuncional(lu.getUnidadFuncional())
                            .concepto(concepto)
                            .monto(lu.getTotalPagar())
                            .fechaVencimiento(fechaVencimiento)
                            .estado(EstadoPago.PENDIENTE)
                            .build();
                    pagoPendienteRepository.save(pago);
                }
            }
        }

        liquidacionMensualRepository.save(liquidacion);
        return mapToResponseDTO(liquidacion);
    }

    @Override
    @Transactional(readOnly = true)
    public LiquidacionMensualResponseDTO getLiquidacionByPeriodo(String periodo, Long consorcioId) {
        LiquidacionMensual liquidacion = liquidacionMensualRepository.findByConsorcioIdAndPeriodo(consorcioId, periodo)
                .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada para el período " + periodo));
        return mapToResponseDTO(liquidacion);
    }

    private LiquidacionMensualResponseDTO mapToResponseDTO(LiquidacionMensual entity) {
        List<LiquidacionUnidadResponseDTO> detalles = entity.getLiquidacionesUnidad().stream()
                .map(lu -> LiquidacionUnidadResponseDTO.builder()
                        .id(lu.getId())
                        .unidadFuncionalId(lu.getUnidadFuncional().getId())
                        .unidadFuncionalIdentificador(lu.getUnidadFuncional().getIdentificador())
                        .coeficienteAplicado(lu.getCoeficienteAplicado())
                        .expensaBaseCalculada(lu.getExpensaBaseCalculada())
                        .totalInfraccionesMes(lu.getTotalInfraccionesMes())
                        .totalAmenitiesMes(lu.getTotalAmenitiesMes())
                        .totalPagar(lu.getTotalPagar())
                        .createdAt(lu.getCreatedAt())
                        .updatedAt(lu.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return LiquidacionMensualResponseDTO.builder()
                .id(entity.getId())
                .consorcioId(entity.getConsorcio().getId())
                .periodo(entity.getPeriodo())
                .gastoComunMes(entity.getGastoComunMes())
                .estado(entity.getEstado())
                .detalles(detalles)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}