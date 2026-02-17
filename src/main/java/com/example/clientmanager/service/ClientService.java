package com.example.clientmanager.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.clientmanager.dto.AddressDto;
import com.example.clientmanager.dto.AddressMapper;
import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.dto.ClientMapper;
import com.example.clientmanager.exception.AddressNotFoundException;
import com.example.clientmanager.exception.ClientNotFoundException;
import com.example.clientmanager.model.Address;
import com.example.clientmanager.model.Client;
import com.example.clientmanager.repository.ClientRepository;
import com.example.clientmanager.repository.AddressRepository;

@Transactional(readOnly = true)
@Service
public class ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final ClientMapper clientMapper;
    private final AddressMapper addressMapper;

    public ClientService(ClientRepository clientRepository,
                         AddressRepository addressRepository,
                         ClientMapper clientMapper,
                         AddressMapper addressMapper) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
        this.clientMapper = clientMapper;
        this.addressMapper = addressMapper;
    }

    // -------------------------
    // CREAR CLIENT
    // -------------------------
    @Transactional
    public ClientDto createClient(ClientDto clientDto) {
        log.debug("Intentant crear client: {}", clientDto);
        Objects.requireNonNull(clientDto, "ClientDto no pot ser null");

        Client clientEntity = clientMapper.toEntity(clientDto);
        Client savedClient = clientRepository.save(clientEntity);
        log.info("Client creat correctament amb id={}", savedClient.getId());

        return clientMapper.toDto(savedClient);
    }

    @Transactional
    public ClientDto createClient(String name, String email) {
        Objects.requireNonNull(name, "El nom no pot ser null");
        Objects.requireNonNull(email, "L'email no pot ser null");
        log.debug("Intentant crear client: name={} email={}", name,email);

        return createClient(new ClientDto(null, name, email, null));
    }

    // -------------------------
    // TROBAR CLIENTS
    // -------------------------
    public List<ClientDto> findByNameOrEmail(String query) {
        log.debug("Buscant client amb query={}", query);
        List<Client> clients = clientRepository
                .findByNameIgnoreCaseContainingOrEmailIgnoreCaseContaining(query, query);
        return clients.stream()
                .map(clientMapper::toDto)
                .toList();
    }

    public List<ClientDto> getAllClients() {
        log.debug("Buscant tots els clients");
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .toList();
    }

    public ClientDto findById(Long id) {
        log.debug("Buscant client amb id={}", id);
        Objects.requireNonNull(id, "L'id no pot ser null");

        return clientRepository.findById(id)
                .map(clientMapper::toDto)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    // -------------------------
    // ACTUALITZAR CLIENT (nom/email)
    // -------------------------
    @Transactional
    public ClientDto update(Long id, ClientDto clientDto) {
        log.debug("Modificant client amb id={}", id);

        
        Objects.requireNonNull(id, "L'id no pot ser null");
        Objects.requireNonNull(clientDto, "ClientDto no pot ser null");

        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        existingClient.setName(clientDto.name());
        existingClient.setEmail(clientDto.email());

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

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        clientRepository.delete(client);
    }

    // -------------------------
    // ACTUALITZAR ADRECES
    // -------------------------
    @Transactional
public List<AddressDto> updateAddresses(Long clientId, List<AddressDto> addressDtos) {
    Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new ClientNotFoundException(clientId));

    client.getAddresses().clear(); // elimina les adreces existents

    if (addressDtos != null) {
        addressDtos.forEach(addrDto -> {
            Address addr = addressMapper.toEntity(addrDto);
            addr.setClient(client);
            client.getAddresses().add(addr);
        });
    }

    clientRepository.save(client); // <- molt important

    return client.getAddresses()
            .stream()
            .map(addressMapper::toDto)
            .toList();
}

    // -------------------------
    // ELIMINAR ADREÇA
    // -------------------------
    @Transactional
    public void deleteAddress(Long clientId, Long addressId) {
        log.debug("Eliminant adreça id={} del client id={}", addressId, clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        Address address = client.getAddresses().stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        client.getAddresses().remove(address);
        addressRepository.delete(address);
    }
}