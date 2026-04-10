package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.UnidadFuncionalRequestDTO;
import com.consorcio.gestion.dto.UnidadFuncionalResponseDTO;
import com.consorcio.gestion.entity.UnidadFuncional;
import com.consorcio.gestion.entity.Usuario;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.exception.ResourceNotFoundException;
import com.consorcio.gestion.repository.UnidadFuncionalRepository;
import com.consorcio.gestion.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UnidadFuncionalServiceTest {

    @Mock
    private UnidadFuncionalRepository unidadFuncionalRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UnidadFuncionalService unidadFuncionalService;

    private UnidadFuncionalRequestDTO requestDTO;
    private UnidadFuncional unidadEntity;
    private Usuario propietario;

    @BeforeEach
    void setUp() {
        requestDTO = new UnidadFuncionalRequestDTO("1A", 1, "Frente");

        unidadEntity = UnidadFuncional.builder()
                .id(1L)
                .identificador("1A")
                .piso(1)
                .descripcion("Frente")
                .activa(true)
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
        when(unidadFuncionalRepository.existsByIdentificador(anyString())).thenReturn(false);
        when(unidadFuncionalRepository.save(any(UnidadFuncional.class))).thenReturn(unidadEntity);

        UnidadFuncionalResponseDTO response = unidadFuncionalService.create(requestDTO);

        assertNotNull(response);
        assertEquals("1A", response.getIdentificador());
        verify(unidadFuncionalRepository, times(1)).save(any(UnidadFuncional.class));
    }

    @Test
    void assignOwner_PropietarioInactivo_LanzaBusinessException() {
        propietario.setActivo(false);

        when(unidadFuncionalRepository.findById(1L)).thenReturn(Optional.of(unidadEntity));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(propietario));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            unidadFuncionalService.assignOwner(1L, 2L);
        });

        assertEquals("No se puede asignar un propietario inactivo", exception.getMessage());
        verify(unidadFuncionalRepository, never()).save(any(UnidadFuncional.class));
    }

    @Test
    void assignOwner_Exito() {
        when(unidadFuncionalRepository.findById(1L)).thenReturn(Optional.of(unidadEntity));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(propietario));
        when(unidadFuncionalRepository.save(any(UnidadFuncional.class))).thenReturn(unidadEntity);

        UnidadFuncionalResponseDTO response = unidadFuncionalService.assignOwner(1L, 2L);

        assertNotNull(response.getPropietarioId());
        assertEquals(2L, response.getPropietarioId());
        verify(unidadFuncionalRepository, times(1)).save(unidadEntity);
    }
}
