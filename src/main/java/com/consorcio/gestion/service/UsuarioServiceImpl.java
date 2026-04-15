package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.UsuarioRequestDTO;
import com.consorcio.gestion.dto.UsuarioResponseDTO;
import com.consorcio.gestion.entity.Consorcio;
import com.consorcio.gestion.entity.Usuario;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.exception.ResourceNotFoundException;
import com.consorcio.gestion.mapper.UsuarioMapper;
import com.consorcio.gestion.repository.ConsorcioRepository;
import com.consorcio.gestion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ConsorcioRepository consorcioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public UsuarioResponseDTO create(UsuarioRequestDTO request, Long consorcioId) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("El email ya está registrado");
        }

        Consorcio consorcio = consorcioRepository.findById(consorcioId)
                .orElseThrow(() -> new ResourceNotFoundException("Consorcio no encontrado con id: " + consorcioId));

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.getConsorcios().add(consorcio);
        
        Usuario saved = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> findAllByConsorcioId(Long consorcioId, Pageable pageable) {
        return usuarioRepository.findByConsorcioId(consorcioId, pageable)
                .map(usuarioMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findById(Long id, Long consorcioId) {
        Usuario usuario = getUsuarioEntityByConsorcio(id, consorcioId);
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO update(Long id, UsuarioRequestDTO request, Long consorcioId) {
        Usuario usuario = getUsuarioEntityByConsorcio(id, consorcioId);

        if (!usuario.getEmail().equals(request.email()) && usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("El nuevo email ya está en uso");
        }

        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setEmail(request.email());
        usuario.setRol(request.rol());
        
        if (request.password() != null && !request.password().trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.password()));
        }

        return usuarioMapper.toResponseDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void delete(Long id, Long consorcioId) {
        Usuario usuario = getUsuarioEntityByConsorcio(id, consorcioId);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    protected Usuario getUsuarioEntityByConsorcio(Long id, Long consorcioId) {
        return usuarioRepository.findByIdAndConsorcioId(id, consorcioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id + " en este consorcio"));
    }
}
