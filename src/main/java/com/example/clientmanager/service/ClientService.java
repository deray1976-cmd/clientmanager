package com.example.clientmanager.service;

import org.springframework.stereotype.Service;

import com.example.clientmanager.repository.ClientRepository;

@Service
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public void createClient(String nom) {
        repository.save(nom);
    }
}