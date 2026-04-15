package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.UnidadFuncionalRequestDTO;
import com.consorcio.gestion.dto.UnidadFuncionalResponseDTO;
import com.consorcio.gestion.dto.UsuarioResponseDTO;
import com.consorcio.gestion.entity.Consorcio;
import com.consorcio.gestion.entity.UnidadFuncional;
import com.consorcio.gestion.entity.Usuario;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.mapper.UnidadFuncionalMapper;
import com.consorcio.gestion.mapper.UsuarioMapper;
import com.consorcio.gestion.repository.ConsorcioRepository;
import com.consorcio.gestion.repository.UnidadFuncionalRepository;
import com.consorcio.gestion.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UnidadFuncionalServiceTest {

    @Mock
    private UnidadFuncionalRepository unidadFuncionalRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ConsorcioRepository consorcioRepository;
    
    @Spy
    private UsuarioMapper usuarioMapper;

    @Mock
    private UnidadFuncionalMapper unidadFuncionalMapper;

    @InjectMocks
    private UnidadFuncionalServiceImpl unidadFuncionalService;

    private UnidadFuncionalRequestDTO requestDTO;
    private UnidadFuncional unidadEntity;
    private Usuario propietario;
    private Consorcio consorcio;
    private final Long consorcioId = 1L;

    @BeforeEach
    void setUp() {
        requestDTO = UnidadFuncionalRequestDTO.builder()
            .identificador("1A")
            .piso(1)
            .descripcion("Frente")
            .consorcioId(1L)
            .coeficiente(new BigDecimal("0.5"))
            .build();

        consorcio = new Consorcio();
        consorcio.setId(1L);
        consorcio.setNombre("Consorcio 1");
        consorcio.setDireccion("CUIT");
        consorcio.setActivo(true);

        unidadEntity = UnidadFuncional.builder()
                .id(1L)
                .identificador("1A")
                .piso(1)
                .descripcion("Frente")
                .coeficiente(new BigDecimal("0.5"))
                .activa(true)
                .consorcio(consorcio)
                .build();

        propietario = Usuario.builder()
                .id(2L)
                .nombre("Maria")
                .apellido("Gomez")
                .activo(true)
                .build();
    }

    @Test
    void create_Exito() {
        UnidadFuncionalResponseDTO responseDTO = new UnidadFuncionalResponseDTO(
                1L,
                "1A",
                1,
                "Frente",
                new BigDecimal("0.5"),
                true,
                null,
                null,
                null
        );

        when(consorcioRepository.findById(1L)).thenReturn(Optional.of(consorcio));
        when(unidadFuncionalRepository.existsByIdentificadorAndConsorcioId(anyString(), eq(1L))).thenReturn(false);
        when(unidadFuncionalMapper.toEntity(any(UnidadFuncionalRequestDTO.class))).thenReturn(unidadEntity);
        when(unidadFuncionalRepository.save(any(UnidadFuncional.class))).thenReturn(unidadEntity);
        when(unidadFuncionalMapper.toResponseDTO(any(UnidadFuncional.class))).thenReturn(responseDTO);

        UnidadFuncionalResponseDTO response = unidadFuncionalService.create(requestDTO, consorcioId);

        assertNotNull(response);
        assertEquals("1A", response.identificador());
        verify(unidadFuncionalRepository, times(1)).save(any(UnidadFuncional.class));
    }

    @Test
    void assignOwner_PropietarioInactivo_LanzaBusinessException() {
        propietario.setActivo(false);

        when(unidadFuncionalRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.of(unidadEntity));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(propietario));

        BusinessException exception = assertThrows(BusinessException.class, () -> unidadFuncionalService.assignOwner(1L, 2L, consorcioId));

        assertEquals("No se puede asignar un propietario inactivo", exception.getMessage());
        verify(unidadFuncionalRepository, never()).save(any(UnidadFuncional.class));
    }

    @Test
    void assignOwner_Exito() {
        unidadEntity.setPropietario(propietario);

        UnidadFuncionalResponseDTO responseDTO = new UnidadFuncionalResponseDTO(
                1L,
                "1A",
                1,
                "Frente",
                new BigDecimal("0.5"),
                true,
                1L,
                new UsuarioResponseDTO(
                        2L,
                        "Maria",
                        "Gomez",
                        null,
                        null,
                        true,
                        Set.of(1L)
                ),
                null
        );

        when(unidadFuncionalRepository.findByIdAndConsorcioId(1L, consorcioId))
                .thenReturn(Optional.of(unidadEntity));
        when(usuarioRepository.findById(2L))
                .thenReturn(Optional.of(propietario));
        when(unidadFuncionalRepository.save(any(UnidadFuncional.class)))
                .thenReturn(unidadEntity);
        when(unidadFuncionalMapper.toResponseDTO(any(UnidadFuncional.class)))
                .thenReturn(responseDTO);

        UnidadFuncionalResponseDTO response = unidadFuncionalService.assignOwner(1L, 2L, consorcioId);

        assertNotNull(response);
        assertNotNull(response.propietario());
        assertEquals(2L, response.propietario().id());
        verify(unidadFuncionalRepository, times(1)).save(unidadEntity);
    }
}
