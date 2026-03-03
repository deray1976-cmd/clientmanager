package com.example.clientmanager.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;

public record ClientDto(
        Long id,

        @NotBlank(message = "El nom és obligatori") 
        String name,

        @NotBlank(message = "El cognom és obligatori") 
        String surname,

        @NotNull(message = "L'edat és obligatòria")
        @Min(value = 0, message = "L'edat no pot ser negativa")
        Integer edat,

        @NotBlank(message = "El dni és obligatori") 
        String dni,

        @NotBlank(message = "L'email és obligatori") 
        @Email(message = "Email invàlid") 
        String email,

        @Valid
        @NotEmpty(message = "Cal indicar almenys una adreça") 
        List<AddressDto> addresses

) {}
