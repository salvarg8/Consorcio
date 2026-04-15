package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.PagoPendienteRequestDTO;
import com.consorcio.gestion.dto.PagoPendienteResponseDTO;
import com.consorcio.gestion.entity.Consorcio;
import com.consorcio.gestion.entity.PagoPendiente;
import com.consorcio.gestion.entity.UnidadFuncional;
import com.consorcio.gestion.enums.EstadoPago;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.mapper.PagoPendienteMapper;
import com.consorcio.gestion.repository.PagoPendienteRepository;
import com.consorcio.gestion.repository.UnidadFuncionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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

    @Spy
    private PagoPendienteMapper pagoPendienteMapper;

    @InjectMocks
    private PagoPendienteService pagoPendienteService;

    private PagoPendienteRequestDTO requestDTO;
    private PagoPendiente pagoEntity;
    private UnidadFuncional unidad;
    private Consorcio consorcio;
    private final Long consorcioId = 1L;

    @BeforeEach
    void setUp() {
        requestDTO = new PagoPendienteRequestDTO(
                1L, "Expensas", "Octubre", BigDecimal.valueOf(15000), LocalDate.now().plusDays(10)
        );

        Consorcio consorcio = new Consorcio();
        consorcio.setId(1L);
        consorcio.setNombre("Consorcio Test");
        consorcio.setDireccion("Calle 123");
        consorcio.setCuit("30-12345678-9");
        consorcio.setActivo(true);

        unidad = UnidadFuncional.builder()
                .id(1L)
                .identificador("1A")
                .activa(true)
                .consorcio(consorcio)
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
        when(unidadFuncionalRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.of(unidad));
        when(pagoRepository.save(any(PagoPendiente.class))).thenReturn(pagoEntity);

        PagoPendienteResponseDTO response = pagoPendienteService.create(requestDTO, consorcioId);

        assertNotNull(response);
        assertEquals(EstadoPago.PENDIENTE, response.estado());
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
                .fechaVencimiento(requestVencido.fechaVencimiento())
                .estado(EstadoPago.VENCIDO)
                .build();

        when(unidadFuncionalRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.of(unidad));
        when(pagoRepository.save(any(PagoPendiente.class))).thenReturn(pagoVencidoEntity);

        PagoPendienteResponseDTO response = pagoPendienteService.create(requestVencido, consorcioId);

        assertNotNull(response);
        assertEquals(EstadoPago.VENCIDO, response.estado());
    }

    @Test
    void pay_Exito() {
        when(pagoRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.of(pagoEntity));
        when(pagoRepository.save(any(PagoPendiente.class))).thenReturn(pagoEntity);

        pagoPendienteService.pay(1L, consorcioId);

        assertEquals(EstadoPago.PAGADO, pagoEntity.getEstado());
        assertNotNull(pagoEntity.getFechaPago());
        verify(pagoRepository, times(1)).save(pagoEntity);
    }

    @Test
    void pay_YaPagado_LanzaBusinessException() {
        pagoEntity.setEstado(EstadoPago.PAGADO);
        when(pagoRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.of(pagoEntity));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            pagoPendienteService.pay(1L, consorcioId);
        });

        assertEquals("El pago ya ha sido procesado anteriormente", exception.getMessage());
    }
}
