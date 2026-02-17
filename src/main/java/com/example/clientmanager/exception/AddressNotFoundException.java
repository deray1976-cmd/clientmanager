package com.example.clientmanager.exception;

public class AddressNotFoundException extends RuntimeException {

    public AddressNotFoundException(Long id) {
        super("No s'ha trobat l'adreça amb id=" + id);
    }
}