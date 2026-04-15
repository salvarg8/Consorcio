package com.consorcio.gestion.service;

import com.consorcio.gestion.entity.Administracion;
import com.consorcio.gestion.entity.Consorcio;
import com.consorcio.gestion.entity.Usuario;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.mapper.ConsorcioMapper;
import com.consorcio.gestion.repository.AdministracionRepository;
import com.consorcio.gestion.repository.ConsorcioRepository;
import com.consorcio.gestion.security.SecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsorcioServiceTest {

    @Mock
    private ConsorcioRepository consorcioRepository;

    @Mock
    private AdministracionRepository administracionRepository;

    @Spy
    private ConsorcioMapper consorcioMapper;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ConsorcioService consorcioService;

    @Test
    void getConsorcioIdsForAuthenticatedAdmin_DeberiaRetornarIds() {
        Administracion administracion = Administracion.builder().id(10L).nombre("Admin A").build();

        Consorcio consorcioUsuario = new Consorcio();
        consorcioUsuario.setId(1L);
        consorcioUsuario.setAdministracion(administracion);

        Usuario usuario = Usuario.builder()
                .id(100L)
                .consorcios(new HashSet<>(List.of(consorcioUsuario)))
                .build();

        when(securityService.getAuthenticatedUser()).thenReturn(usuario);
        when(consorcioRepository.findIdsByAdministracionId(10L)).thenReturn(List.of(1L, 2L, 3L));

        List<Long> result = consorcioService.getConsorcioIdsForAuthenticatedAdmin();

        assertEquals(List.of(1L, 2L, 3L), result);
        verify(consorcioRepository).findIdsByAdministracionId(10L);
    }

    @Test
    void getConsorcioIdsForAuthenticatedAdmin_SinConsorcios_LanzaBusinessException() {
        Usuario usuario = Usuario.builder()
                .id(100L)
                .consorcios(new HashSet<>())
                .build();

        when(securityService.getAuthenticatedUser()).thenReturn(usuario);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> consorcioService.getConsorcioIdsForAuthenticatedAdmin()
        );

        assertEquals("El usuario autenticado no pertenece a ningún consorcio", exception.getMessage());
    }

    @Test
    void getConsorcioIdsForAuthenticatedAdmin_MultiplesAdministraciones_LanzaBusinessException() {
        Administracion administracionA = Administracion.builder().id(10L).nombre("Admin A").build();
        Administracion administracionB = Administracion.builder().id(20L).nombre("Admin B").build();

        Consorcio consorcioA = new Consorcio();
        consorcioA.setId(1L);
        consorcioA.setAdministracion(administracionA);

        Consorcio consorcioB = new Consorcio();
        consorcioB.setId(2L);
        consorcioB.setAdministracion(administracionB);

        Usuario usuario = Usuario.builder()
                .id(100L)
                .consorcios(new HashSet<>(List.of(consorcioA, consorcioB)))
                .build();

        when(securityService.getAuthenticatedUser()).thenReturn(usuario);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> consorcioService.getConsorcioIdsForAuthenticatedAdmin()
        );

        assertEquals("El usuario administrador no puede pertenecer a más de una administración", exception.getMessage());
    }
}
