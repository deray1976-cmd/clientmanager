package com.example.clientmanager.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO per a Address - transferència de dades address.
 * Només conté dades públiques de l'adreça sense riscos de circularitat.
 * El clientId és opcional ja que pot ser assignat después de la creació.
 */
public record AddressDto(
        Long id,

        @NotBlank(message = "El carrer és obligatori i no pot ser buit")
        @Size(min = 3, max = 100, message = "El carrer ha de tenir entre 3 i 100 caràcters")
        String street,

        @NotBlank(message = "La ciutat és obligatòria i no pot ser buida")
        @Size(min = 2, max = 50, message = "La ciutat ha de tenir entre 2 i 50 caràcters")
        String city,

        // clientId és opcional en creació, es pot assignar después
        Long clientId
) {
    /**
     * Constructor compacte per validació addicional.
     */
    public AddressDto {
        if (street != null) {
            street = street.trim();
        }
        if (city != null) {
            city = city.trim();
        }
    }

    /**
     * Factory method per a creació sense clientId (per a noves adreces).
     */
    public static AddressDto newAddress(String street, String city) {
        return new AddressDto(null, street, city, null);
    }

    /**
     * Factory method per a creació amb clientId (per a updater/delete).
     */
    public static AddressDto forClient(Long id, String street, String city, Long clientId) {
        return new AddressDto(id, street, city, clientId);
    }
}