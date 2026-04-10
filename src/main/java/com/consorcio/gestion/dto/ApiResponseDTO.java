package com.consorcio.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseDTO<T> {
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String message;
    private T data;

    // Método estático de conveniencia para respuestas exitosas (HTTP 200 OK)
    public static <T> ApiResponseDTO<T> success(T data, String message) {
        return ApiResponseDTO.<T>builder()
                .status(HttpStatus.OK.value()) // Código de estado HTTP 200
                .message(message)
                .data(data)
                .build();
    }

    // Método estático de conveniencia para respuestas de creación (HTTP 201 Created)
    public static <T> ApiResponseDTO<T> created(T data, String message) {
        return ApiResponseDTO.<T>builder()
                .status(HttpStatus.CREATED.value()) // Código de estado HTTP 201
                .message(message)
                .data(data)
                .build();
    }
}
