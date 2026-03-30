package com.example.clientmanager.dto;

import java.time.LocalDateTime;

/**
 * ErrorResponse - DTO per a respostes d'error amb detalls.
 */
public record ErrorResponse(
    String message,
    String reason,
    int status,
    LocalDateTime timestamp
) {
    /**
     * Factory method para crear ErrorResponse
     */
    public static ErrorResponse of(String message, String reason, int status) {
        return new ErrorResponse(message, reason, status, LocalDateTime.now());
    }

    /**
     * Factory method para crear ErrorResponse con mensaje simple
     */
    public static ErrorResponse of(String message, int status) {
        return new ErrorResponse(message, null, status, LocalDateTime.now());
    }

    /**
     * Factory method para BAD_REQUEST
     */
    public static ErrorResponse badRequest(String message, String reason) {
        return ErrorResponse.of(message, reason, 400);
    }

    /**
     * Factory method para FORBIDDEN
     */
    public static ErrorResponse forbidden(String message, String reason) {
        return ErrorResponse.of(message, reason, 403);
    }

    /**
     * Factory method para NOT_FOUND
     */
    public static ErrorResponse notFound(String message) {
        return ErrorResponse.of(message, 404);
    }
}
