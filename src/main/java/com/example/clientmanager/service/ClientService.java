package com.example.clientmanager.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.clientmanager.entity.ClientEntity;
import com.example.clientmanager.exception.ClientNotFoundException;
import com.example.clientmanager.model.ClientModel;
import com.example.clientmanager.repository.ClientRepository;
import com.example.clientmanager.entity.ClientEntityModelMapper;

@Transactional(readOnly = true)
@Service
public class ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;
    private final ClientEntityModelMapper clientEntityMapper;

    public ClientService(ClientRepository clientRepository,
                         ClientEntityModelMapper clientEntityMapper) {
        this.clientRepository = clientRepository;
        this.clientEntityMapper = clientEntityMapper;
    }

    // -------------------------
    // CREAR CLIENT
    // -------------------------
    @Transactional
    public ClientModel createClient(ClientModel clientModel) {

        log.debug("Intentant crear client model: {}", clientModel);
        Objects.requireNonNull(clientModel, "ClientModel no pot ser null");

        ClientEntity entity = clientEntityMapper.toEntity(clientModel);
        ClientEntity saved = clientRepository.save(entity);

        log.info("Client creat correctament amb id={}", saved.getId());

        return clientEntityMapper.toModel(saved);
    }

    // -------------------------
    // CERCA GLOBAL
    // -------------------------
    public List<ClientModel> searchClients(String query) {

        log.debug("Buscant clients amb query={}", query);
        Objects.requireNonNull(query, "La consulta no pot ser null");

        String value = "%" + query.trim().toLowerCase() + "%";

        Specification<ClientEntity> spec = (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), value),
                cb.like(cb.lower(root.get("surname")), value),
                cb.like(cb.lower(root.get("email")), value),
                cb.like(cb.lower(root.get("dni")), value),
                cb.equal(root.get("edat").as(String.class), query.trim())
        );

        return clientRepository.findAll(spec)
                .stream()
                .map(clientEntityMapper::toModel)
                .toList();
    }

    // -------------------------
    // CERCA AVANÇADA
    // -------------------------
    public List<ClientModel> searchByFields(
            String name,
            String surname,
            Integer edat,
            String dni,
            String email) {

        Specification<ClientEntity> spec = (root, query, cb) -> cb.conjunction();

        if (name != null && !name.trim().isEmpty()) {
            String value = "%" + name.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), value));
        }

        if (surname != null && !surname.trim().isEmpty()) {
            String value = "%" + surname.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("surname")), value));
        }

        if (edat != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("edat"), edat));
        }

        if (dni != null && !dni.trim().isEmpty()) {
            String value = "%" + dni.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("dni")), value));
        }

        if (email != null && !email.trim().isEmpty()) {
            String value = "%" + email.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("email")), value));
        }

        return clientRepository.findAll(spec)
                .stream()
                .map(clientEntityMapper::toModel)
                .toList();
    }

    // -------------------------
    // TROBAR PER ID
    // -------------------------
    public ClientModel findById(Long id) {

        log.debug("Buscant client amb id={}", id);
        Objects.requireNonNull(id, "L'id no pot ser null");

        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        return clientEntityMapper.toModel(entity);
    }

    // -------------------------
    // OBTENIR TOTS
    // -------------------------
    public List<ClientModel> getAllClients() {

        log.debug("Buscant tots els clients");

        return clientRepository.findAll()
                .stream()
                .map(clientEntityMapper::toModel)
                .toList();
    }

    // -------------------------
    // ACTUALITZAR
    // -------------------------
    @Transactional
    public ClientModel update(Long id, ClientModel clientModel) {

        log.debug("Modificant client amb id={}", id);

        Objects.requireNonNull(id, "L'id no pot ser null");
        Objects.requireNonNull(clientModel, "ClientModel no pot ser null");

        ClientEntity existing = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        existing.setName(clientModel.getName());
        existing.setSurname(clientModel.getSurname());
        existing.setEdat(clientModel.getEdat());
        existing.setDni(clientModel.getDni());
        existing.setEmail(clientModel.getEmail());

        ClientEntity updated = clientRepository.save(existing);

        return clientEntityMapper.toModel(updated);
    }

    // -------------------------
    // ELIMINAR
    // -------------------------
    @Transactional
    public void delete(Long id) {

        log.debug("Suprimint client amb id={}", id);
        Objects.requireNonNull(id, "L'id no pot ser null");

        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        clientRepository.delete(entity);
    }
}
