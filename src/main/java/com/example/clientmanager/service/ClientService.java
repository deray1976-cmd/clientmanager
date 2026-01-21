package com.example.clientmanager.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.clientmanager.dto.ClientDto;
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
        //repository.save(clientDto);
        //return clientDto; // Retornem el mateix ClientDto
        ClientDto savedClient = new ClientDto(clientRepository.save(clientDto.getClient()));
        return savedClient;
    }

     // -------------------------
    // FIND BY ID
    // -------------------------
    public Optional<ClientDto> findById(Long id) {
    return clientRepository.findById(id)   // Optional<Client>
            .map(client -> new ClientDto(client)); // Optional<ClientDto>
    }   

    // -------------------------
    // UPDATE
    // -------------------------
    @Transactional
    public Optional<ClientDto> update(Long id, ClientDto clientDetails) {
        return clientRepository.findById(id).map(existingClient -> {
            existingClient.setName(clientDetails.getClient().getName());
            existingClient.setEmail(clientDetails.getClient().getEmail());
            // afegeix altres camps que tingui Client
            return new ClientDto(clientRepository.save(existingClient));
        });
    }

    // -------------------------
    // DELETE
    // -------------------------
    @Transactional
    public boolean delete(Long id) {
        return clientRepository.findById(id).map(client -> {
            clientRepository.delete(client);
            return true;
        }).orElse(false);
    }
}