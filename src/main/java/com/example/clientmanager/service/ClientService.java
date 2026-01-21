package com.example.clientmanager.service;

import java.util.List;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.exception.ClientNotFoundException;
import com.example.clientmanager.model.Client;
import com.example.clientmanager.repository.ClientRepository;

@Transactional(readOnly = true)
@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // Mètode per crear client a partir de dades individuals
    public void createClient(Long id, String name, String email) {
         createClient(new ClientDto(name,email));
    }

    // Obtenir tots els clients
    public List<ClientDto> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(client -> new ClientDto(client))
                .collect(Collectors.toList());
    }

    // Mètode per crear client a partir de ClientDto
    @Transactional
    public ClientDto createClient(ClientDto clientDto) {
        ClientDto savedClient = new ClientDto(clientRepository.save(clientDto.getClient()));
        return savedClient;
    }

     // -------------------------
    // FIND BY ID
    // -------------------------
    public ClientDto findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException( id));
        return new ClientDto(client);
    }   

    // -------------------------
    // UPDATE
    // -------------------------
    @Transactional
    public ClientDto update(Long id, ClientDto clientDetails) {
         Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

            existingClient.setName(clientDetails.getClient().getName());
            existingClient.setEmail(clientDetails.getClient().getEmail());
            Client updatedClient = clientRepository.save(existingClient);
        return new ClientDto(updatedClient);
    }

    // -------------------------
    // DELETE
    // -------------------------
    @Transactional
    public void delete(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        clientRepository.delete(client);
    }
}