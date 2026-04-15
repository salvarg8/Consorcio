package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.UsuarioRequestDTO;
import com.consorcio.gestion.dto.UsuarioResponseDTO;
import com.consorcio.gestion.entity.Consorcio;
import com.consorcio.gestion.entity.Usuario;
import com.consorcio.gestion.enums.RolUsuario;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.exception.ResourceNotFoundException;
import com.consorcio.gestion.mapper.UsuarioMapper;
import com.consorcio.gestion.repository.ConsorcioRepository;
import com.consorcio.gestion.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    
    @Mock
    private ConsorcioRepository consorcioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequestDTO requestDTO;
    private Usuario usuarioEntity;
    private Consorcio consorcio;
    private final Long consorcioId = 1L;

    @BeforeEach
    void setUp() {
        requestDTO = new UsuarioRequestDTO(
                "Juan", "Perez", "juan@test.com", "password123", RolUsuario.PROPIETARIO
        );

        consorcio = new Consorcio();
        consorcio.setId(1L);
        consorcio.setNombre("Consorcio 1");
        consorcio.setDireccion("Direccion");
        consorcio.setCuit("CUIT");
        consorcio.setActivo(true);

        usuarioEntity = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@test.com")
                .password("encodedPassword")
                .rol(RolUsuario.PROPIETARIO)
                .activo(true)
                .consorcios(new HashSet<>())
                .build();
    }

    @Test
    void create_Exito() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(consorcioRepository.findById(consorcioId)).thenReturn(Optional.of(consorcio));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEntity);

        UsuarioResponseDTO response = usuarioService.create(requestDTO, consorcioId);

        assertNotNull(response);
        assertEquals("Juan", response.nombre());
        assertEquals("juan@test.com", response.email());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void create_EmailExistente_LanzaBusinessException() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> usuarioService.create(requestDTO, consorcioId));

        assertEquals("El email ya está registrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void findById_UsuarioInexistente_LanzaResourceNotFoundException() {
        when(usuarioRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> usuarioService.findById(1L, consorcioId));

        assertTrue(exception.getMessage().contains("Usuario no encontrado con ID: 1 en este consorcio"));
    }

    @Test
    void delete_SoftDeleteExitoso() {
        when(usuarioRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.of(usuarioEntity));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEntity);

        usuarioService.delete(1L, consorcioId);

        assertFalse(usuarioEntity.isActivo());
        verify(usuarioRepository, times(1)).save(usuarioEntity);
    }
}
