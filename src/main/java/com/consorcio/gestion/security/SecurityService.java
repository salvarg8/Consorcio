package com.consorcio.gestion.security;

import com.consorcio.gestion.entity.UnidadFuncional;
import com.consorcio.gestion.entity.Usuario;
import com.consorcio.gestion.exception.BusinessException;
import com.consorcio.gestion.repository.UnidadFuncionalRepository;
import com.consorcio.gestion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final UsuarioRepository usuarioRepository;
    private final UnidadFuncionalRepository unidadFuncionalRepository;

    public boolean isOwner(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(username);
            
            return usuarioOpt.isPresent() && usuarioOpt.get().getId().equals(userId);
        }
        return false;
    }

    public boolean isOwnerOrTenantOfUnit(Long unitId) {
        Usuario currentUser = getAuthenticatedUser();
        Optional<UnidadFuncional> unidadOpt = unidadFuncionalRepository.findById(unitId);
        
        if (unidadOpt.isEmpty()) {
            return false;
        }
        
        UnidadFuncional unidad = unidadOpt.get();
        boolean isOwner = unidad.getPropietario() != null && unidad.getPropietario().getId().equals(currentUser.getId());
        boolean isTenant = unidad.getInquilino() != null && unidad.getInquilino().getId().equals(currentUser.getId());
        
        return isOwner || isTenant;
    }

    public Usuario getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("Usuario no autenticado");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return usuarioRepository.findByEmail(username)
                    .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        }
        throw new BusinessException("Principal no válido");
    }

    public Long getConsorcioIdForCurrentUser() {
        Usuario currentUser = getAuthenticatedUser();
        if (currentUser.getConsorcios() == null || currentUser.getConsorcios().isEmpty()) {
            throw new BusinessException("El usuario no pertenece a ningún consorcio");
        }
        
        // En este ejemplo, asumimos que el usuario solo puede actuar sobre un consorcio a la vez
        // o tomamos el primero si pertenece a varios (se debería implementar una lógica más robusta
        // dependiendo del negocio, ej. pasar el consorcioId en un header 'X-Consorcio-Id' si tiene varios)
        return currentUser.getConsorcios().iterator().next().getId();
    }
}
