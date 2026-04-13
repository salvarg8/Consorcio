package com.consorcio.gestion.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public ErrorResponseDTO {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
