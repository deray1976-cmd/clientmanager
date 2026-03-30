package com.example.clientmanager.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

/**
 * DTO per a Client - transferència de dades client.
 * Conté tota la informació del client incloses les seves adreces.
 */
public record ClientDto(
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
        String email,

        @Valid
        // Addresses es opcional en actualitzacions, obligatori en creacions
        List<AddressDto> addresses

) {
    /**
     * Constructor compacte per a trim() de String fields.
     */
    public ClientDto {
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
     * Factory method per a creació sense id (per a noves creacions).
     */
    public static ClientDto newClient(String name, String surname, Integer edat, 
                                      String dni, String email, List<AddressDto> addresses) {
        return new ClientDto(null, name, surname, edat, dni, email, addresses);
    }

    /**
     * Factory method per a actualitzacions existents.
     */
    public static ClientDto existing(Long id, String name, String surname, Integer edat, 
                                     String dni, String email, List<AddressDto> addresses) {
        return new ClientDto(id, name, surname, edat, dni, email, addresses);
    }
}
