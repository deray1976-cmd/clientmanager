package com.example.clientmanager.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressDto(
        Long id,
        @NotBlank(message = "El carrer és obligatori") String street,
        @NotBlank(message = "La ciutat és obligatoria") String city
) {}