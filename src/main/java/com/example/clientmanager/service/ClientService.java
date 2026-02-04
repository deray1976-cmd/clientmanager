package com.example.clientmanager.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.dto.ClientMapper;
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

    // -------------------------
    // CREAR CLIENT
    // -------------------------
    @Transactional
    public ClientDto createClient(ClientDto clientDto) {
        Objects.requireNonNull(clientDto, "ClientDto no pot ser null");

        // Transformar DTO a entitat
        Client clientEntity = ClientMapper.toEntity(clientDto);

        // Guardar a la base de dades
        Client savedClient = clientRepository.save(Objects.requireNonNull(clientEntity));

        // Transformar a DTO per retornar
        return ClientMapper.toDto(savedClient);
    }

  
    // Mètode auxiliar per crear client a partir de dades individuals
    @Transactional
    public ClientDto createClient(String name, String email) {
        Objects.requireNonNull(name, "El nom no pot ser null");
        Objects.requireNonNull(email, "L'email no pot ser null");

        ClientDto dto = new ClientDto(null, name, email, null);
        return createClient(dto);
    }

    // -------------------------
    // TROBAR TOTS ELS CLIENTS
    // -------------------------
    public List<ClientDto> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(ClientMapper::toDto)
                .collect(Collectors.toList());
    }

    // -------------------------
    // TROBAR PER ID
    // -------------------------
    public ClientDto findById(Long id) {
        Objects.requireNonNull(id, "L'id no pot ser null");

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        return ClientMapper.toDto(client);
    }

     // -------------------------
    // ACTUALITZAR CLIENT
    // -------------------------
    @Transactional
    public ClientDto update(Long id, ClientDto clientDto) {
        Objects.requireNonNull(id, "L'id no pot ser null");
        Objects.requireNonNull(clientDto, "ClientDto no pot ser null");

        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        // Actualitzar camps
        existingClient.setName(clientDto.name());
        existingClient.setEmail(clientDto.email());

        // Guardar entitat actualitzada
        Client updatedClient = clientRepository.save(existingClient);

        return ClientMapper.toDto(updatedClient);
    }
    
    // -------------------------
    // ELIMINAR CLIENT
    // -------------------------
    @Transactional
    public void delete(Long id) {
        
        Objects.requireNonNull(id, "L'id no pot ser null");

        Client client = Objects.requireNonNull(
                clientRepository.findById(id)
                                .orElseThrow(() -> new ClientNotFoundException(id)));
        clientRepository.delete(client);
    }
}