package com.example.clientmanager.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

/**
 * DTO de resum de Client - informació bàsica sense adreces.
 * Utilitzat per a llisting i búsquedas on no cal la informació completa.
 */
public record ClientSummaryDto(
       @NotNull(message = "El id és obligatori")
       Long id,
       
       @NotBlank(message = "El nom és obligatori i no pot ser buit")
       @Size(min = 2, max = 50, message = "El nom ha de tenir entre 2 i 50 caràcters")
       String name,
       
       @NotBlank(message = "El cognom és obligatori i no pot ser buit")
       @Size(min = 2, max = 50, message = "El cognom ha de tenir entre 2 i 50 caràcters")
       String surname,
       
       @NotNull(message = "L'edat és obligatòria")
       @Min(value = 0, message = "L'edat no pot ser negativa")
       @Max(value = 120, message = "L'edat no pot superar 120 anys")
       Integer edat,
       
       @NotBlank(message = "El dni és obligatori i no pot ser buit")
       @Size(min = 5, max = 20, message = "El dni ha de tenir entre 5 i 20 caràcters")
       String dni,

       @NotBlank(message = "L'email és obligatori i no pot ser buit")
       @Email(message = "Email invàlid - format: user@example.com")
       String email
) {
    /**
     * Constructor compacte per a trim() de String fields.
     */
    public ClientSummaryDto {
        if (name != null) {
            name = name.trim();
        }
        if (surname != null) {
            surname = surname.trim();
        }
        if (dni != null) {
            dni = dni.trim().toUpperCase();
        }
        if (email != null) {
            email = email.trim().toLowerCase();
        }
    }

    /**
     * Factory method per crear un resum desde un ClientDto.
     */
    public static ClientSummaryDto fromClientDto(ClientDto clientDto) {
        return new ClientSummaryDto(
            clientDto.id(),
            clientDto.name(),
            clientDto.surname(),
            clientDto.edat(),
            clientDto.dni(),
            clientDto.email()
        );
    }
}
