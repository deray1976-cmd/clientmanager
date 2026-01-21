package com.example.clientmanager.exception;

public class ClientNotFoundException extends RuntimeException {
    
    private final Long id;  // <-- camp per guardar l'id del client

    public ClientNotFoundException(Long id) {
        super("Client amb id " + id + " no trobat"); // missatge
        this.id = id; // guardem l'id
    }

    // Getter per poder-lo utilitzar a l'ExceptionHandler
    public Long getId() {
        return id;
    }
}