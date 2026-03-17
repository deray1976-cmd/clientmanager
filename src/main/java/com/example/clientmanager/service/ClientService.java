package com.example.clientmanager.service;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.clientmanager.dto.AddressDto;
import com.example.clientmanager.entity.AddressEntity;
import com.example.clientmanager.entity.ClientEntity;
import com.example.clientmanager.exception.ClientNotFoundException;
import com.example.clientmanager.exception.InvalidClientDataException;
import com.example.clientmanager.model.ClientModel;
import com.example.clientmanager.repository.AddressRepository;
import com.example.clientmanager.repository.ClientRepository;
import com.example.clientmanager.entity.ClientEntityModelMapper;

@Transactional(readOnly = true)
@Service
public class ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;
    private final ClientEntityModelMapper clientEntityMapper;
    private final AddressRepository addressRepository;

    public ClientService(ClientRepository clientRepository,
                         ClientEntityModelMapper clientEntityMapper,
                         AddressRepository addressRepository) {
        this.clientRepository = clientRepository;
        this.clientEntityMapper = clientEntityMapper;
        this.addressRepository = addressRepository;
    }

    // -------------------------
    // VALIDACIÓ CENTRALITZADA
    // -------------------------
    private void validateClientModel(ClientModel clientModel) {
        if (clientModel.getDni() == null || clientModel.getDni().trim().isEmpty()) {
            throw new InvalidClientDataException("El DNI és obligatori i no pot estar buit");
        }
        if (clientModel.getEdat() == null) {
            throw new InvalidClientDataException("L'edat és obligatòria i no pot estar buida");
        }
    }
    // -------------------------
    // CREAR CLIENT
    // -------------------------
    @Transactional
    public ClientModel createClient(ClientModel clientModel) {
        Objects.requireNonNull(clientModel, "ClientModel no pot ser null");
        ClientEntity entity = clientEntityMapper.toEntity(clientModel);
        ClientEntity saved = clientRepository.save(entity);
        return clientEntityMapper.toModel(saved);
    }

    // -------------------------
    // CERCA GLOBAL
    // -------------------------
    public List<ClientModel> searchClients(String query) {
        Objects.requireNonNull(query, "La consulta no pot ser null");
        String value = "%" + query.trim().toLowerCase() + "%";

        Specification<ClientEntity> spec = (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), value),
                cb.like(cb.lower(root.get("surname")), value),
                cb.like(cb.lower(root.get("email")), value),
                cb.like(cb.lower(root.get("dni")), value),
                cb.equal(root.get("edat").as(String.class), query.trim())
        );

        return clientRepository.findAll(spec).stream()
                .map(clientEntityMapper::toModel)
                .toList();
    }

    // -------------------------
    // CERCA PER CAMPS
    // -------------------------
    public List<ClientModel> searchByFields(String name, String surname, Integer edat, String dni, String email) {
        Specification<ClientEntity> spec = (root, query, cb) -> cb.conjunction();

        if (name != null && !name.isBlank()) {
            String value = "%" + name.trim().toLowerCase() + "%";
            spec = spec.and((root1, q, cb) -> cb.like(cb.lower(root1.get("name")), value));
        }
        if (surname != null && !surname.isBlank()) {
            String value = "%" + surname.trim().toLowerCase() + "%";
            spec = spec.and((root1, q, cb) -> cb.like(cb.lower(root1.get("surname")), value));
        }
        if (edat != null) {
            spec = spec.and((root1, q, cb) -> cb.equal(root1.get("edat"), edat));
        }
        if (dni != null && !dni.isBlank()) {
            String value = "%" + dni.trim().toLowerCase() + "%";
            spec = spec.and((root1, q, cb) -> cb.like(cb.lower(root1.get("dni")), value));
        }
        if (email != null && !email.isBlank()) {
            String value = "%" + email.trim().toLowerCase() + "%";
            spec = spec.and((root1, q, cb) -> cb.like(cb.lower(root1.get("email")), value));
        }

        return clientRepository.findAll(spec).stream()
                .map(clientEntityMapper::toModel)
                .toList();
    }

    // -------------------------
    // TROBAR CLIENT PER ID AMB ADDRESSES
    // -------------------------
    public ClientModel findByIdWithAddresses(Long id) {
        ClientEntity entity = clientRepository.findByIdWithAddresses(id)
                .orElseThrow(() -> new EntityNotFoundException("Client no trobat amb id=" + id));
        return clientEntityMapper.toModel(entity);
    }

    // -------------------------
    // TROBAR CLIENT PER ID
    // -------------------------
    public ClientModel findById(Long id) {
        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        return clientEntityMapper.toModel(entity);
    }

    // -------------------------
    // OBTENIR TOTS ELS CLIENTS
    // -------------------------
    public List<ClientModel> getAllClients() {
        return clientRepository.findAll().stream()
                .map(clientEntityMapper::toModel)
                .toList();
    }

    // -------------------------
    // ACTUALITZAR CLIENT
    // -------------------------
    @Transactional
    public ClientModel update(Long id, ClientModel clientModel) {
        Objects.requireNonNull(clientModel, "ClientModel no pot ser null");
        validateClientModel(clientModel);  // <-- Validació aquí

        ClientEntity existing = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        existing.setName(clientModel.getName());
        existing.setSurname(clientModel.getSurname());
        existing.setEdat(clientModel.getEdat());
        existing.setDni(clientModel.getDni());
        existing.setEmail(clientModel.getEmail());

        return clientEntityMapper.toModel(clientRepository.save(existing));
    }

    // -------------------------
    // ACTUALITZAR ADDRESSES
    // -------------------------
    @Transactional
    public ClientModel updateAddresses(Long clientId, List<AddressDto> addressesDto) {
        ClientEntity client = clientRepository.findByIdWithAddresses(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client no trobat amb id=" + clientId));

        for (AddressDto dto : addressesDto) {
            if (dto.id() != null) {
                AddressEntity existing = addressRepository.findById(dto.id())
                        .orElseThrow(() -> new EntityNotFoundException("Address no trobada amb id=" + dto.id()));

                if (!existing.getClient().getId().equals(clientId)) {
                    throw new IllegalArgumentException("L'adreça no pertany al client");
                }

                existing.setStreet(dto.street());
                existing.setCity(dto.city());
            } else {
                AddressEntity newAddress = new AddressEntity();
                newAddress.setStreet(dto.street());
                newAddress.setCity(dto.city());
                newAddress.setClient(client);
                client.getAddresses().add(newAddress);
            }
        }

        return clientEntityMapper.toModel(clientRepository.save(client));
    }

    // -------------------------
    // ELIMINAR CLIENT
    // -------------------------
    @Transactional
    public void delete(Long id) {
        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        clientRepository.delete(entity);
    }

    // -------------------------
    // ELIMINAR ADDRESS
    // -------------------------
    @Transactional
    public void deleteAddress(Long clientId, Long addressId) {
        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address no trobada amb id=" + addressId));

        if (!address.getClient().getId().equals(clientId)) {
            throw new IllegalArgumentException("L'adreça no pertany al client");
        }

        addressRepository.delete(address);
    }
}