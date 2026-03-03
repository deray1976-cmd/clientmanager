package com.example.clientmanager.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record ClientSummaryDto(
       Long id,
       @NotBlank(message = "El nom és obligatori") String name,
       @NotBlank(message = "El cognom és obligatori") String surname,
       @NotBlank(message = "Edat és obligatori") Integer edat,
       @NotBlank(message = "El dni és obligatori") String dni,

       @NotBlank(message = "L'email és obligatori") @Email(message = "Email invàlid") String email
) {}
