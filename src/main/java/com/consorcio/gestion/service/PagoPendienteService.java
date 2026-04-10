package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.PagoPendienteRequestDTO;
import com.consorcio.gestion.dto.PagoPendienteResponseDTO;
import com.consorcio.gestion.entity.PagoPendiente;
import com.consorcio.gestion.entity.UnidadFuncional;
import com.consorcio.gestion.enums.EstadoPago;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.exception.ResourceNotFoundException;
import com.consorcio.gestion.mapper.PagoPendienteMapper;
import com.consorcio.gestion.repository.PagoPendienteRepository;
import com.consorcio.gestion.repository.UnidadFuncionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PagoPendienteService {

    private final PagoPendienteRepository pagoRepository;
    private final UnidadFuncionalRepository unidadFuncionalRepository;
    private final PagoPendienteMapper pagoPendienteMapper;

    @Transactional
    public PagoPendienteResponseDTO create(PagoPendienteRequestDTO request, Long consorcioId) {
        UnidadFuncional unidad = unidadFuncionalRepository.findByIdAndConsorcioId(request.getUnidadFuncionalId(), consorcioId)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad Funcional no encontrada en este consorcio"));

        if (!unidad.isActiva()) {
            throw new BusinessException("No se puede crear un pago para una unidad inactiva");
        }

        EstadoPago estadoInicial = LocalDate.now().isAfter(request.getFechaVencimiento()) 
                ? EstadoPago.VENCIDO 
                : EstadoPago.PENDIENTE;

        PagoPendiente pago = PagoPendiente.builder()
                .unidadFuncional(unidad)
                .concepto(request.getConcepto())
                .descripcion(request.getDescripcion())
                .monto(request.getMonto())
                .fechaVencimiento(request.getFechaVencimiento())
                .estado(estadoInicial)
                .build();

        return pagoPendienteMapper.toResponseDTO(pagoRepository.save(pago));
    }

    @Transactional(readOnly = true)
    public Page<PagoPendienteResponseDTO> findAllByConsorcioId(Long consorcioId, Pageable pageable) {
        return pagoRepository.findByConsorcioId(consorcioId, pageable)
                .map(this::checkAndUpdateVencimiento)
                .map(pagoPendienteMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public PagoPendienteResponseDTO findById(Long id, Long consorcioId) {
        PagoPendiente pago = getPagoEntityByConsorcio(id, consorcioId);
        pago = checkAndUpdateVencimiento(pago);
        return pagoPendienteMapper.toResponseDTO(pago);
    }

    @Transactional
    public PagoPendienteResponseDTO pay(Long id, Long consorcioId) {
        PagoPendiente pago = getPagoEntityByConsorcio(id, consorcioId);

        if (pago.getEstado() == EstadoPago.PAGADO) {
            throw new BusinessException("El pago ya ha sido procesado anteriormente");
        }

        pago.setEstado(EstadoPago.PAGADO);
        pago.setFechaPago(LocalDateTime.now());
        
        return pagoPendienteMapper.toResponseDTO(pagoRepository.save(pago));
    }

    private PagoPendiente getPagoEntityByConsorcio(Long id, Long consorcioId) {
        return pagoRepository.findByIdAndConsorcioId(id, consorcioId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago pendiente no encontrado con ID: " + id + " en este consorcio"));
    }

    private PagoPendiente checkAndUpdateVencimiento(PagoPendiente pago) {
        if (pago.getEstado() == EstadoPago.PENDIENTE && LocalDate.now().isAfter(pago.getFechaVencimiento())) {
            pago.setEstado(EstadoPago.VENCIDO);
        }
        return pago;
    }
}
