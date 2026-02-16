package com.example.clientmanager.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }
    // -------------------------
    // CREAR CLIENT
    // -------------------------
    @Transactional
    public ClientDto createClient(ClientDto clientDto) {
        log.debug("Intentant crear client: {}", clientDto);

        Objects.requireNonNull(clientDto, "ClientDto no pot ser null");

        // Transformar DTO a entitat
        Client clientEntity = clientMapper.toEntity(clientDto);

        // Guardar a la base de dades
        Client savedClient = clientRepository.save(Objects.requireNonNull(clientEntity));
        log.info("Client creat correctament amb id={}", savedClient.getId());

        // Transformar a DTO per retornar
        return clientMapper.toDto(savedClient);
    }

  
    // Mètode auxiliar per crear client a partir de dades individuals
    @Transactional
    public ClientDto createClient(String name, String email) {
        Objects.requireNonNull(name, "El nom no pot ser null");
        Objects.requireNonNull(email, "L'email no pot ser null");
        log.debug("Intentant crear client: name={} email={}", name,email);

        return createClient(new ClientDto(null, name, email, null));
    }

    public List<ClientDto> findByNameOrEmail(String query) {
        log.debug("Buscant client amb query={}", query);

         List<Client> clients =
            clientRepository.findByNameIgnoreCaseContainingOrEmailIgnoreCaseContaining(query, query);

    return clients.stream()
            .map(clientMapper::toDto)
            .toList();
    }

    // -------------------------
    // TROBAR TOTS ELS CLIENTS
    // -------------------------
    public List<ClientDto> getAllClients() {
        log.debug("Buscant tots els clients");

        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toDto)
                .toList(); // Java 16+, si no Collectors.toList()
    }

    // -------------------------
    // TROBAR PER ID
    // -------------------------
    public ClientDto findById(Long id) {
        log.debug("Buscant client amb id={}", id);

        Objects.requireNonNull(id, "L'id no pot ser null");

        return clientRepository.findById(id)
                .map(clientMapper::toDto)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

     // -------------------------
    // ACTUALITZAR CLIENT
    // -------------------------
    @Transactional
    public ClientDto update(Long id, ClientDto clientDto) {
        Objects.requireNonNull(id, "L'id no pot ser null");
        Objects.requireNonNull(clientDto, "ClientDto no pot ser null");
        log.debug("Modificant client amb id={}", id);

        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        // Actualitzar camps
        existingClient.setName(clientDto.name());
        existingClient.setEmail(clientDto.email());

        // Guardar entitat actualitzada
        Client updatedClient = clientRepository.save(existingClient);

        return clientMapper.toDto(updatedClient);
    }
    
    // -------------------------
    // ELIMINAR CLIENT
    // -------------------------
    @Transactional
    public void delete(Long id) {
        log.debug("Suprimint client amb id={}", id);

        Objects.requireNonNull(id, "L'id no pot ser null");

        Client client = Objects.requireNonNull(
                clientRepository.findById(id)
                                .orElseThrow(() -> new ClientNotFoundException(id)));
        clientRepository.delete(client);
    }
}