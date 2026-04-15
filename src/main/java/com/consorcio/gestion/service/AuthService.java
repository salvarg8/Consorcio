package com.consorcio.gestion.service;

import com.consorcio.gestion.dto.AuthRequestDTO;
import com.consorcio.gestion.dto.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO login(AuthRequestDTO request);
}
