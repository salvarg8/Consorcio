package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.EstadoInfraccionRequestDTO;
import com.consorcio.gestion.dto.InfraccionRequestDTO;
import com.consorcio.gestion.dto.InfraccionResponseDTO;
import com.consorcio.gestion.entity.Consorcio;
import com.consorcio.gestion.entity.Infraccion;
import com.consorcio.gestion.entity.UnidadFuncional;
import com.consorcio.gestion.enums.EstadoInfraccion;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.mapper.InfraccionMapper;
import com.consorcio.gestion.repository.InfraccionRepository;
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
public class InfraccionServiceTest {

    @Mock
    private InfraccionRepository infraccionRepository;

    @Mock
    private UnidadFuncionalRepository unidadFuncionalRepository;

    @Spy
    private InfraccionMapper infraccionMapper;

    @InjectMocks
    private InfraccionServiceImpl infraccionService;

    private InfraccionRequestDTO requestDTO;
    private Infraccion infraccionEntity;
    private UnidadFuncional unidad;
    private Consorcio consorcio;
    private final Long consorcioId = 1L;

    @BeforeEach
    void setUp() {
        requestDTO = new InfraccionRequestDTO(
                1L, LocalDate.now(), "Ruidos", "Música alta", BigDecimal.valueOf(5000)
        );

        consorcio = new Consorcio(1L, "Consorcio Test", "Calle 123", "30-12345678-9", true, null, null, null, null);

        unidad = UnidadFuncional.builder()
                .id(1L)
                .identificador("1A")
                .activa(true)
                .consorcio(consorcio)
                .build();

        infraccionEntity = Infraccion.builder()
                .id(1L)
                .unidadFuncional(unidad)
                .estado(EstadoInfraccion.PENDIENTE)
                .montoPenalizacion(BigDecimal.valueOf(5000))
                .build();
    }

    @Test
    void create_Exito() {
        when(unidadFuncionalRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.of(unidad));
        when(infraccionRepository.save(any(Infraccion.class))).thenReturn(infraccionEntity);

        InfraccionResponseDTO response = infraccionService.create(requestDTO, consorcioId);

        assertNotNull(response);
        assertEquals(EstadoInfraccion.PENDIENTE, response.estado());
        assertEquals(BigDecimal.valueOf(5000), response.montoPenalizacion());
        verify(infraccionRepository, times(1)).save(any(Infraccion.class));
    }

    @Test
    void create_UnidadInactiva_LanzaBusinessException() {
        unidad.setActiva(false);
        when(unidadFuncionalRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.of(unidad));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            infraccionService.create(requestDTO, consorcioId);
        });

        assertEquals("No se puede crear una infracción para una unidad inactiva", exception.getMessage());
    }

    @Test
    void updateStatus_Exito() {
        when(infraccionRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.of(infraccionEntity));
        when(infraccionRepository.save(any(Infraccion.class))).thenReturn(infraccionEntity);

        EstadoInfraccionRequestDTO updateDTO = new EstadoInfraccionRequestDTO(EstadoInfraccion.PAGADA);
        
        infraccionService.updateStatus(1L, updateDTO, consorcioId);

        assertEquals(EstadoInfraccion.PAGADA, infraccionEntity.getEstado());
        verify(infraccionRepository, times(1)).save(infraccionEntity);
    }
}
