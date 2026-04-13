package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.ReservaRequestDTO;
import com.consorcio.gestion.dto.ReservaResponseDTO;
import com.consorcio.gestion.entity.Amenity;
import com.consorcio.gestion.entity.Consorcio;
import com.consorcio.gestion.entity.ReservaAmenity;
import com.consorcio.gestion.entity.UnidadFuncional;
import com.consorcio.gestion.entity.Usuario;
import com.consorcio.gestion.enums.EstadoReserva;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.mapper.ReservaMapper;
import com.consorcio.gestion.repository.AmenityRepository;
import com.consorcio.gestion.repository.ReservaAmenityRepository;
import com.consorcio.gestion.repository.UnidadFuncionalRepository;
import com.consorcio.gestion.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservaAmenityServiceTest {

    @Mock
    private ReservaAmenityRepository reservaRepository;
    @Mock
    private UnidadFuncionalRepository unidadFuncionalRepository;
    @Mock
    private AmenityRepository amenityRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @Spy
    private ReservaMapper reservaMapper;

    @InjectMocks
    private ReservaAmenityService reservaService;

    private ReservaRequestDTO requestDTO;
    private UnidadFuncional unidad;
    private Amenity amenity;
    private Usuario usuario;
    private Consorcio consorcio;
    private final Long consorcioId = 1L;

    @BeforeEach
    void setUp() {
        requestDTO = new ReservaRequestDTO(
                1L, 1L, LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0), "Test"
        );

        consorcio = new Consorcio(1L, "Consorcio 1", "Direccion", "CUIT", true, null, null, null, null);

        unidad = UnidadFuncional.builder().id(1L).activa(true).identificador("1A").consorcio(consorcio).build();
        amenity = Amenity.builder().id(1L).habilitado(true).nombre("SUM").build();
        usuario = Usuario.builder().id(1L).email("test@user.com").build();
    }

    private void mockSecurityContext() {
        UserDetails userDetails = mock(UserDetails.class);
        lenient().when(userDetails.getUsername()).thenReturn("test@user.com");
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void create_Exito() {
        mockSecurityContext();
        when(unidadFuncionalRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.of(unidad));
        when(amenityRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.of(amenity));
        when(usuarioRepository.findByEmail("test@user.com")).thenReturn(Optional.of(usuario));
        when(reservaRepository.findAll()).thenReturn(Collections.emptyList());
        when(reservaRepository.save(any(ReservaAmenity.class))).thenAnswer(i -> {
            ReservaAmenity r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        ReservaResponseDTO response = reservaService.create(requestDTO, consorcioId);

        assertNotNull(response);
        assertEquals(EstadoReserva.PENDIENTE, response.estado());
        verify(reservaRepository, times(1)).save(any(ReservaAmenity.class));
    }

    @Test
    void create_AmenityDeshabilitado_LanzaBusinessException() {
        amenity.setHabilitado(false);
        when(unidadFuncionalRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.of(unidad));
        when(amenityRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.of(amenity));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reservaService.create(requestDTO, consorcioId);
        });

        assertEquals("El amenity no está habilitado para reservas", exception.getMessage());
    }

    @Test
    void create_SuperposicionDeHorario_LanzaBusinessException() {
        ReservaAmenity reservaExistente = ReservaAmenity.builder()
                .amenity(amenity)
                .fecha(requestDTO.fecha())
                .horaInicio(LocalTime.of(11, 0))
                .horaFin(LocalTime.of(13, 0))
                .estado(EstadoReserva.CONFIRMADA)
                .build();

        when(unidadFuncionalRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.of(unidad));
        when(amenityRepository.findByIdAndConsorcioId(1L, consorcioId)).thenReturn(Optional.of(amenity));
        when(reservaRepository.findAll()).thenReturn(Collections.singletonList(reservaExistente));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reservaService.create(requestDTO, consorcioId);
        });

        assertEquals("Ya existe una reserva confirmada o pendiente que se superpone con este horario", exception.getMessage());
    }
}
