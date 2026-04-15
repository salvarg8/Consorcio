package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.ReservaRequestDTO;
import com.consorcio.gestion.dto.ReservaResponseDTO;
import com.consorcio.gestion.entity.Amenity;
import com.consorcio.gestion.entity.ReservaAmenity;
import com.consorcio.gestion.entity.UnidadFuncional;
import com.consorcio.gestion.entity.Usuario;
import com.consorcio.gestion.enums.EstadoReserva;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.exception.ResourceNotFoundException;
import com.consorcio.gestion.mapper.ReservaMapper;
import com.consorcio.gestion.repository.AmenityRepository;
import com.consorcio.gestion.repository.ReservaAmenityRepository;
import com.consorcio.gestion.repository.UnidadFuncionalRepository;
import com.consorcio.gestion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaAmenityServiceImpl implements ReservaAmenityService {

    private final ReservaAmenityRepository reservaRepository;
    private final UnidadFuncionalRepository unidadFuncionalRepository;
    private final AmenityRepository amenityRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaMapper reservaMapper;

    @Transactional
    public ReservaResponseDTO create(ReservaRequestDTO request, Long consorcioId) {
        UnidadFuncional unidad = unidadFuncionalRepository.findByIdAndConsorcioId(request.unidadFuncionalId(),
                        consorcioId)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad Funcional no encontrada en este consorcio"));

        if (!unidad.isActiva()) {
            throw new BusinessException("No se puede crear una reserva para una unidad inactiva");
        }

        Amenity amenity = amenityRepository.findByIdAndConsorcioId(request.amenityId(), consorcioId)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity no encontrado en este consorcio"));

        if (!amenity.isHabilitado()) {
            throw new BusinessException("El amenity no está habilitado para reservas");
        }

        if (!request.horaFin().isAfter(request.horaInicio())) {
            throw new BusinessException("La hora de fin debe ser posterior a la hora de inicio");
        }

        // Validación de superposición
        List<ReservaAmenity> reservasDelDia = reservaRepository.findAll().stream()
                .filter(r -> r.getAmenity().getId().equals(amenity.getId()))
                .filter(r -> r.getFecha().equals(request.fecha()))
                .filter(r -> r.getEstado() != EstadoReserva.CANCELADA)
                .toList();

        for (ReservaAmenity reservaExistente : reservasDelDia) {
            if (isOverlap(request, reservaExistente)) {
                throw new BusinessException("Ya existe una reserva confirmada o pendiente que se superpone con este horario");
            }
        }

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario creador = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario creador no encontrado"));

        ReservaAmenity reserva = ReservaAmenity.builder()
                .unidadFuncional(unidad)
                .amenity(amenity)
                .fecha(request.fecha())
                .horaInicio(request.horaInicio())
                .horaFin(request.horaFin())
                .observaciones(request.observaciones())
                .estado(EstadoReserva.PENDIENTE)
                .usuarioCreador(creador)
                .build();

        return reservaMapper.toResponseDTO(reservaRepository.save(reserva));
    }

    private boolean isOverlap(ReservaRequestDTO request, ReservaAmenity existente) {
        return request.horaInicio().isBefore(existente.getHoraFin()) &&
               request.horaFin().isAfter(existente.getHoraInicio());
    }

    @Transactional(readOnly = true)
    public Page<ReservaResponseDTO> findAllByConsorcioId(Long consorcioId, Pageable pageable) {
        return reservaRepository.findByConsorcioId(consorcioId, pageable)
                .map(reservaMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO findById(Long id, Long consorcioId) {
        ReservaAmenity reserva = getReservaEntityByConsorcio(id, consorcioId);
        return reservaMapper.toResponseDTO(reserva);
    }

    @Transactional
    public ReservaResponseDTO confirm(Long id, Long consorcioId) {
        ReservaAmenity reserva = getReservaEntityByConsorcio(id, consorcioId);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new BusinessException("Solo se pueden confirmar reservas pendientes");
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return reservaMapper.toResponseDTO(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaResponseDTO cancel(Long id, Long consorcioId) {
        ReservaAmenity reserva = getReservaEntityByConsorcio(id, consorcioId);

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new BusinessException("La reserva ya está cancelada");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        return reservaMapper.toResponseDTO(reservaRepository.save(reserva));
    }

    protected ReservaAmenity getReservaEntityByConsorcio(Long id, Long consorcioId) {
        return reservaRepository.findByIdAndConsorcioId(id, consorcioId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id + " en este consorcio"));
    }
}
