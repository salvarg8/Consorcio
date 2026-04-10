package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.PagoPendienteRequestDTO;
import com.consorcio.gestion.dto.PagoPendienteResponseDTO;
import com.consorcio.gestion.entity.PagoPendiente;
import com.consorcio.gestion.entity.UnidadFuncional;
import com.consorcio.gestion.enums.EstadoPago;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.repository.PagoPendienteRepository;
import com.consorcio.gestion.repository.UnidadFuncionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagoPendienteServiceTest {

    @Mock
    private PagoPendienteRepository pagoRepository;

    @Mock
    private UnidadFuncionalRepository unidadFuncionalRepository;

    @InjectMocks
    private PagoPendienteService pagoPendienteService;

    private PagoPendienteRequestDTO requestDTO;
    private PagoPendiente pagoEntity;
    private UnidadFuncional unidad;

    @BeforeEach
    void setUp() {
        requestDTO = new PagoPendienteRequestDTO(
                1L, "Expensas", "Octubre", BigDecimal.valueOf(15000), LocalDate.now().plusDays(10)
        );

        unidad = UnidadFuncional.builder()
                .id(1L)
                .identificador("1A")
                .activa(true)
                .build();

        pagoEntity = PagoPendiente.builder()
                .id(1L)
                .unidadFuncional(unidad)
                .monto(BigDecimal.valueOf(15000))
                .fechaVencimiento(LocalDate.now().plusDays(10))
                .estado(EstadoPago.PENDIENTE)
                .build();
    }

    @Test
    void create_Exito_EstadoPendiente() {
        when(unidadFuncionalRepository.findById(1L)).thenReturn(Optional.of(unidad));
        when(pagoRepository.save(any(PagoPendiente.class))).thenReturn(pagoEntity);

        PagoPendienteResponseDTO response = pagoPendienteService.create(requestDTO);

        assertNotNull(response);
        assertEquals(EstadoPago.PENDIENTE, response.getEstado());
        verify(pagoRepository, times(1)).save(any(PagoPendiente.class));
    }

    @Test
    void create_FechaPasada_EstadoVencido() {
        PagoPendienteRequestDTO requestVencido = new PagoPendienteRequestDTO(
                1L, "Expensas", "Octubre", BigDecimal.valueOf(15000), LocalDate.now().minusDays(1)
        );

        PagoPendiente pagoVencidoEntity = PagoPendiente.builder()
                .id(1L)
                .unidadFuncional(unidad)
                .fechaVencimiento(requestVencido.getFechaVencimiento())
                .estado(EstadoPago.VENCIDO)
                .build();

        when(unidadFuncionalRepository.findById(1L)).thenReturn(Optional.of(unidad));
        when(pagoRepository.save(any(PagoPendiente.class))).thenReturn(pagoVencidoEntity);

        PagoPendienteResponseDTO response = pagoPendienteService.create(requestVencido);

        assertNotNull(response);
        assertEquals(EstadoPago.VENCIDO, response.getEstado());
    }

    @Test
    void pay_Exito() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoEntity));
        when(pagoRepository.save(any(PagoPendiente.class))).thenReturn(pagoEntity);

        pagoPendienteService.pay(1L);

        assertEquals(EstadoPago.PAGADO, pagoEntity.getEstado());
        assertNotNull(pagoEntity.getFechaPago());
        verify(pagoRepository, times(1)).save(pagoEntity);
    }

    @Test
    void pay_YaPagado_LanzaBusinessException() {
        pagoEntity.setEstado(EstadoPago.PAGADO);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoEntity));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            pagoPendienteService.pay(1L);
        });

        assertEquals("El pago ya ha sido procesado anteriormente", exception.getMessage());
    }
}
