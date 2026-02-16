package com.example.clientmanager.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;

public record ClientDto(
        Long id,
        @NotBlank(message = "El nom és obligatori") String name,
        @NotBlank(message = "L'email és obligatori") @Email(message = "Email invàlid") String email,
        @NotEmpty(message = "Cal indicar almenys una adreça")
        @Valid
        List<AddressDto> addresses
) {}