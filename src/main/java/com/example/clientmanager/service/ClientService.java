package com.example.clientmanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        validateClientModel(clientModel);
        log.info("Creant client: {}", clientModel.getDni());
        ClientEntity entity = clientEntityMapper.toEntity(clientModel);
        ClientEntity saved = clientRepository.save(entity);
        log.info("Client creat correctament amb id={}", saved.getId());
        return clientEntityMapper.toModel(saved);
    }

    // -------------------------
    // CERCA GLOBAL
    // -------------------------
    public List<ClientModel> searchClients(String query) {
        Objects.requireNonNull(query, "La consulta no pot ser null");
        String value = "%" + query.trim().toLowerCase() + "%";

        Specification<ClientEntity> spec = (root, q, cb) -> {
            List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(cb.lower(root.get("name")), value));
            predicates.add(cb.like(cb.lower(root.get("surname")), value));
            predicates.add(cb.like(cb.lower(root.get("email")), value));
            predicates.add(cb.like(cb.lower(root.get("dni")), value));
            
            if (query.trim().matches("\\d+")) {
                predicates.add(cb.equal(root.get("edat"), Integer.parseInt(query.trim())));
            }
            
            return cb.or(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        };

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
        log.info("Actualitzant client amb id={}", id);

        ClientEntity existing = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        existing.setName(clientModel.getName());
        existing.setSurname(clientModel.getSurname());
        existing.setEdat(clientModel.getEdat());
        existing.setDni(clientModel.getDni());
        existing.setEmail(clientModel.getEmail());

        log.info("Client {} actualitzat correctament", id);
        return clientEntityMapper.toModel(clientRepository.save(existing));
    }

    // -------------------------
    // ACTUALITZAR ADDRESSES
    // -------------------------
   @Transactional
public ClientModel updateAddresses(Long clientId, List<AddressDto> addressesDto) {
    ClientEntity client = clientRepository.findByIdWithAddresses(clientId)
            .orElseThrow(() -> new EntityNotFoundException("Client no trobat amb id=" + clientId));

    Map<Long, AddressEntity> existingMap = client.getAddresses().stream()
            .filter(a -> a.getId() != null)
            .collect(Collectors.toMap(AddressEntity::getId, a -> a));

    for (AddressDto dto : addressesDto) {

        // 🔹 ACTUALITZAR
        if (dto.id() != null && existingMap.containsKey(dto.id())) {
            AddressEntity existing = existingMap.get(dto.id());
            existing.setStreet(dto.street());
            existing.setCity(dto.city());
        }

        // 🔹 AFEGIR NOVA (evitant duplicats)
        else if (dto.id() == null) {

            boolean exists = client.getAddresses().stream()
                    .anyMatch(a ->
                            a.getStreet().equals(dto.street()) &&
                            a.getCity().equals(dto.city())
                    );

            if (!exists) {
                AddressEntity newAddress = new AddressEntity();
                newAddress.setStreet(dto.street());
                newAddress.setCity(dto.city());
                newAddress.setClient(client);

                client.getAddresses().add(newAddress);
            }
        }
    }

    return clientEntityMapper.toModel(clientRepository.save(client));
}


    // -------------------------
    // ELIMINAR CLIENT
    // -------------------------
    @Transactional
    public void delete(Long id) {
        log.info("Eliminant client amb id={}", id);
        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        clientRepository.delete(entity);
        log.info("Client {} eliminat correctament", id);
    }

    // -------------------------
    // ELIMINAR ADDRESS
    // -------------------------
    @Transactional
    public void deleteAddress(Long clientId, Long addressId) {
        ClientEntity client = clientRepository.findByIdWithAddresses(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client no trobat amb id=" + clientId));

        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address no trobada amb id=" + addressId));

        if (!address.getClient().getId().equals(clientId)) {
            throw new IllegalArgumentException("L'adreça no pertany al client amb id=" + clientId);
        }

        // Trencar relació amb el client
        client.getAddresses().remove(address);
        address.setClient(null);

        // Eliminar de la base de dades
        addressRepository.delete(address);
    }
}