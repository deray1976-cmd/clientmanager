package com.example.clientmanager.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(Long id) {
        super("Client amb id " + id + " no trobat");
    }
}