package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.ConsorcioRequestDTO;
import com.consorcio.gestion.dto.LiquidacionMensualResponseDTO;
import com.consorcio.gestion.entity.Consorcio;
import com.consorcio.gestion.entity.LiquidacionMensual;
import com.consorcio.gestion.entity.LiquidacionUnidad;
import com.consorcio.gestion.entity.UnidadFuncional;
import com.consorcio.gestion.enums.EstadoLiquidacion;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.repository.ConsorcioRepository;
import com.consorcio.gestion.repository.InfraccionRepository;
import com.consorcio.gestion.repository.LiquidacionMensualRepository;
import com.consorcio.gestion.repository.LiquidacionUnidadRepository;
import com.consorcio.gestion.repository.PagoPendienteRepository;
import com.consorcio.gestion.repository.ReservaAmenityRepository;
import com.consorcio.gestion.repository.UnidadFuncionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LiquidacionServiceTest {

    @Mock
    private LiquidacionMensualRepository liquidacionMensualRepository;

    @Mock
    private LiquidacionUnidadRepository liquidacionUnidadRepository;

    @Mock
    private UnidadFuncionalRepository unidadFuncionalRepository;

    @Mock
    private InfraccionRepository infraccionRepository;

    @Mock
    private ReservaAmenityRepository reservaAmenityRepository;

    @Mock
    private PagoPendienteRepository pagoPendienteRepository;

    @Mock
    private ConsorcioRepository consorcioRepository;

    @InjectMocks
    private LiquidacionServiceImpl liquidacionService;

    private Consorcio consorcioEntity;
    private UnidadFuncional uf1;
    private UnidadFuncional uf2;

    @BeforeEach
    void setUp() {
        consorcioEntity = new Consorcio();
        consorcioEntity.setId(1L);
        consorcioEntity.setCuit("123456789");
        consorcioEntity.setNombre("Consorcio Test");
        consorcioEntity.setDireccion("Calle Test");

        uf1 = UnidadFuncional.builder()
                .id(1L)
                .identificador("1A")
                .coeficiente(new BigDecimal("0.40000000"))
                .activa(true)
                .consorcio(consorcioEntity)
                .build();

        uf2 = UnidadFuncional.builder()
                .id(2L)
                .identificador("1B")
                .coeficiente(new BigDecimal("0.60000000"))
                .activa(true)
                .consorcio(consorcioEntity)
                .build();
    }

    @Test
    void generar_Success() {
        String periodo = "2023-10";
        BigDecimal gastoComun = new BigDecimal("10000");

        when(liquidacionMensualRepository.findByConsorcioIdAndPeriodo(1L, periodo)).thenReturn(Optional.empty());
        when(consorcioRepository.findById(1L)).thenReturn(Optional.of(consorcioEntity));
        when(unidadFuncionalRepository.findAllByConsorcioIdAndActivaTrue(1L)).thenReturn(List.of(uf1, uf2));

        when(infraccionRepository.sumTotalForUnitInPeriod(anyLong(), any(), any(), any())).thenReturn(BigDecimal.ZERO);
        when(reservaAmenityRepository.sumTotalForUnitInPeriod(anyLong(), any(), any(), any())).thenReturn(BigDecimal.ZERO);

        LiquidacionMensual savedLiquidacion = LiquidacionMensual.builder()
                .id(1L)
                .consorcio(consorcioEntity)
                .periodo(periodo)
                .gastoComunMes(gastoComun)
                .estado(EstadoLiquidacion.BORRADOR)
                .liquidacionesUnidad(new ArrayList<>())
                .build();
        when(liquidacionMensualRepository.save(any(LiquidacionMensual.class))).thenReturn(savedLiquidacion);

        when(liquidacionUnidadRepository.save(any(LiquidacionUnidad.class))).thenAnswer(i -> {
            LiquidacionUnidad lu = i.getArgument(0);
            lu.setId((long) (Math.random() * 1000));
            return lu;
        });

        LiquidacionMensualResponseDTO response = liquidacionService.generar(periodo, gastoComun, 1L);

        assertNotNull(response);
        assertEquals(2, response.getDetalles().size());
        assertEquals(new BigDecimal("4000.00"), response.getDetalles().get(0).getTotalPagar());
        assertEquals(new BigDecimal("6000.00"), response.getDetalles().get(1).getTotalPagar());

        verify(liquidacionMensualRepository, times(1)).save(any(LiquidacionMensual.class));
        verify(liquidacionUnidadRepository, times(2)).save(any(LiquidacionUnidad.class));
    }

    @Test
    void generar_CoeficientesNoSumanUno() {
        String periodo = "2023-10";
        BigDecimal gastoComun = new BigDecimal("10000");

        uf2.setCoeficiente(new BigDecimal("0.50000000")); // Sum is 0.9

        when(liquidacionMensualRepository.findByConsorcioIdAndPeriodo(1L, periodo)).thenReturn(Optional.empty());
        when(consorcioRepository.findById(1L)).thenReturn(Optional.of(consorcioEntity));
        when(unidadFuncionalRepository.findAllByConsorcioIdAndActivaTrue(1L)).thenReturn(List.of(uf1, uf2));

        assertThrows(BusinessException.class, () -> liquidacionService.generar(periodo, gastoComun, 1L));
        
        verify(liquidacionMensualRepository, never()).save(any());
    }

    @Test
    void generar_DuplicadoPeriodo() {
        String periodo = "2023-10";
        BigDecimal gastoComun = new BigDecimal("10000");

        when(liquidacionMensualRepository.findByConsorcioIdAndPeriodo(1L, periodo))
                .thenReturn(Optional.of(new LiquidacionMensual()));

        assertThrows(BusinessException.class, () -> liquidacionService.generar(periodo, gastoComun, 1L));
        
        verify(consorcioRepository, never()).findById(anyLong());
    }

    @Test
    void publicar_CreatesPagosPendientes() {
        LiquidacionMensual liquidacion = LiquidacionMensual.builder()
                .id(1L)
                .consorcio(consorcioEntity)
                .periodo("2023-10")
                .estado(EstadoLiquidacion.BORRADOR)
                .liquidacionesUnidad(new ArrayList<>())
                .build();

        LiquidacionUnidad lu1 = LiquidacionUnidad.builder()
                .id(1L)
                .liquidacionMensual(liquidacion)
                .unidadFuncional(uf1)
                .totalPagar(new BigDecimal("4000.00"))
                .build();
        liquidacion.getLiquidacionesUnidad().add(lu1);

        when(liquidacionMensualRepository.findById(1L)).thenReturn(Optional.of(liquidacion));
        when(pagoPendienteRepository.existsByUnidadFuncionalIdAndConcepto(anyLong(), anyString())).thenReturn(false);

        LiquidacionMensualResponseDTO response = liquidacionService.publicar(1L, consorcioEntity.getId(), true);

        assertNotNull(response);
        assertEquals(EstadoLiquidacion.PUBLICADA, response.getEstado());
        verify(pagoPendienteRepository, times(1)).save(any());
        verify(liquidacionMensualRepository, times(1)).save(liquidacion);
    }
}