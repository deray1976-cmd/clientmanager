package com.example.clientmanager.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientDto(
        Long id,
        @NotBlank(message = "El nom és obligatori") String name,
        @NotBlank(message = "L'email és obligatori") @Email(message = "Email invàlid") String email,
        List<AddressDto> addresses
) {}