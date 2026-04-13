package com.consorcio.gestion.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
public record ApiResponseDTO<T>(
        LocalDateTime timestamp,
        int status,
        String message,
        T data
) {
    public ApiResponseDTO {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public static <T> ApiResponseDTO<T> success(T data, String message) {
        return ApiResponseDTO.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponseDTO<T> created(T data, String message) {
        return ApiResponseDTO.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
                .build();
    }
}
