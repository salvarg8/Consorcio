package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.EstadoInfraccionRequestDTO;
import com.consorcio.gestion.dto.InfraccionRequestDTO;
import com.consorcio.gestion.dto.InfraccionResponseDTO;
import com.consorcio.gestion.entity.Infraccion;
import com.consorcio.gestion.entity.UnidadFuncional;
import com.consorcio.gestion.enums.EstadoInfraccion;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.repository.InfraccionRepository;
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
public class InfraccionServiceTest {

    @Mock
    private InfraccionRepository infraccionRepository;

    @Mock
    private UnidadFuncionalRepository unidadFuncionalRepository;

    @InjectMocks
    private InfraccionService infraccionService;

    private InfraccionRequestDTO requestDTO;
    private Infraccion infraccionEntity;
    private UnidadFuncional unidad;

    @BeforeEach
    void setUp() {
        requestDTO = new InfraccionRequestDTO(
                1L, LocalDate.now(), "Ruidos", "Música alta", BigDecimal.valueOf(5000)
        );

        unidad = UnidadFuncional.builder()
                .id(1L)
                .identificador("1A")
                .activa(true)
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
        when(unidadFuncionalRepository.findById(1L)).thenReturn(Optional.of(unidad));
        when(infraccionRepository.save(any(Infraccion.class))).thenReturn(infraccionEntity);

        InfraccionResponseDTO response = infraccionService.create(requestDTO);

        assertNotNull(response);
        assertEquals(EstadoInfraccion.PENDIENTE, response.getEstado());
        assertEquals(BigDecimal.valueOf(5000), response.getMontoPenalizacion());
        verify(infraccionRepository, times(1)).save(any(Infraccion.class));
    }

    @Test
    void create_UnidadInactiva_LanzaBusinessException() {
        unidad.setActiva(false);
        when(unidadFuncionalRepository.findById(1L)).thenReturn(Optional.of(unidad));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            infraccionService.create(requestDTO);
        });

        assertEquals("No se puede crear una infracción para una unidad inactiva", exception.getMessage());
    }

    @Test
    void updateStatus_Exito() {
        when(infraccionRepository.findById(1L)).thenReturn(Optional.of(infraccionEntity));
        when(infraccionRepository.save(any(Infraccion.class))).thenReturn(infraccionEntity);

        EstadoInfraccionRequestDTO updateDTO = new EstadoInfraccionRequestDTO(EstadoInfraccion.PAGADA);
        
        infraccionService.updateStatus(1L, updateDTO);

        assertEquals(EstadoInfraccion.PAGADA, infraccionEntity.getEstado());
        verify(infraccionRepository, times(1)).save(infraccionEntity);
    }
}
