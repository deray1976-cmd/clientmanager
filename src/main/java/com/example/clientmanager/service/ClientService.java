package com.example.clientmanager.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.clientmanager.dto.AddressDto;
import com.example.clientmanager.dto.AddressMapper;
import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.dto.ClientMapper;
import com.example.clientmanager.dto.ClientSummaryDto;
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
        Objects.requireNonNull(clientEntity, "clientEntity no pot ser null");

        Client savedClient = clientRepository.save(clientEntity);
        log.info("Client creat correctament amb id={}", savedClient.getId());

        return clientMapper.toDto(savedClient);
    }

    // -------------------------
    // CERCA GLOBAL (nom, cognom, edat, dni, email)
    // -------------------------
    public List<ClientSummaryDto> searchClientsSummary(String query) {
        log.debug("Buscant resum clients amb query={}", query);
        Objects.requireNonNull(query, "La consulta no pot ser null");

        String value = "%" + query.trim().toLowerCase() + "%";

        Specification<Client> spec = (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), value),
                cb.like(cb.lower(root.get("surname")), value),
                cb.like(cb.lower(root.get("email")), value),
                cb.like(cb.lower(root.get("dni")), value),
                cb.equal(root.get("edat").as(String.class), query.trim())
        );

        return clientRepository.findAll(spec)
                .stream()
                .map(clientMapper::toSummaryDto)
                .toList();
    }

    // -------------------------
    // CERCA AVANÇADA PER CAMPS
    // -------------------------
    public List<ClientSummaryDto> searchByFields(
            String name,
            String surname,
            Integer edat,
            String dni,
            String email) {

        Specification<Client> spec = (root, query, cb) -> cb.conjunction();

        if (name != null && !name.trim().isEmpty()) {
            String value = "%" + name.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), value));
        }

        if (surname != null && !surname.trim().isEmpty()) {
            String value = "%" + surname.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("surname")), value));
        }

        if (edat != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("edat"), edat));
        }

        if (dni != null && !dni.trim().isEmpty()) {
            String value = "%" + dni.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("dni")), value));
        }

        if (email != null && !email.trim().isEmpty()) {
            String value = "%" + email.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("email")), value));
        }

        return clientRepository.findAll(spec)
                .stream()
                .map(clientMapper::toSummaryDto)
                .toList();
    }

    // -------------------------
    // TROBAR CLIENT PER ID
    // -------------------------
    public ClientDto findById(Long id) {
        log.debug("Buscant client amb id={}", id);
        Objects.requireNonNull(id, "L'id no pot ser null");

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        return clientMapper.toDto(client);
    }

    public List<ClientSummaryDto> getAllClientslite() {
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toSummaryDto)
                .toList();
    }

    public List<ClientDto> getAllClients() {
        log.debug("Buscant tots els clients");
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toDto)
                .toList();
    }

    // -------------------------
    // ACTUALITZAR CLIENT
    // -------------------------
    @Transactional
    public ClientDto update(Long id, ClientDto clientDto) {
        log.debug("Modificant client amb id={}", id);

        Objects.requireNonNull(id, "L'id no pot ser null");
        Objects.requireNonNull(clientDto, "ClientDto no pot ser null");

        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        existingClient.setName(clientDto.name());
        existingClient.setSurname(clientDto.surname());
        existingClient.setEdat(clientDto.edat());
        existingClient.setDni(clientDto.dni());
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
        Objects.requireNonNull(clientId, "clientId no pot ser null");

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        client.getAddresses().clear();

        if (addressDtos != null) {
            addressDtos.forEach(addrDto -> {
                Address addr = addressMapper.toEntity(addrDto);
                addr.setClient(client);
                client.getAddresses().add(addr);
            });
        }

        clientRepository.save(client);

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
        Objects.requireNonNull(clientId, "clientId no pot ser null");

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
