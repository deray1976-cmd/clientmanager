package com.example.clientmanager.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.repository.ClientRepository;

import com.example.clientmanager.model.Client;

@Service
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    // Mètode per crear client a partir de dades individuals
    public void createClient(Long id, String name, String email) {
         createClient(new ClientDto(name,email));
    }

    // Obtenir tots els clients
    public List<ClientDto> getAllClients() {
        return repository.findAll()
                .stream()
                .map(ClientDto::new)
                .collect(Collectors.toList());
    }

    // Mètode per crear client a partir de ClientDto
    public ClientDto createClient(ClientDto clientDto) {
        //repository.save(clientDto);
        //return clientDto; // Retornem el mateix ClientDto
        Client clientToSave = clientDto.getClient();
        Client savedClient = repository.save(clientToSave);
        return new ClientDto(savedClient);
    }
}