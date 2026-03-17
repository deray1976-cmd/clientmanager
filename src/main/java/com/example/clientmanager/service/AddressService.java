package com.example.clientmanager.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.clientmanager.entity.AddressEntity;
import com.example.clientmanager.entity.ClientEntity;
import com.example.clientmanager.exception.AddressNotFoundException;
import com.example.clientmanager.exception.ClientNotFoundException;
import com.example.clientmanager.model.AddressModel;
import com.example.clientmanager.repository.AddressRepository;
import com.example.clientmanager.repository.ClientRepository;
import com.example.clientmanager.entity.AddressEntityModelMapper;

@Service
@Transactional(readOnly = true)
public class AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository addressRepository;
    private final ClientRepository clientRepository;
    private final AddressEntityModelMapper addressEntityMapper;

    public AddressService(AddressRepository addressRepository,
                          ClientRepository clientRepository,
                          AddressEntityModelMapper addressEntityMapper) {
        this.addressRepository = addressRepository;
        this.clientRepository = clientRepository;
        this.addressEntityMapper = addressEntityMapper;
    }

    // =====================================
    // OBTENIR ADRECES D'UN CLIENT
    // =====================================
    public List<AddressModel> findByClientId(Long clientId) {
        log.debug("Buscant adreces del client {}", clientId);
        Objects.requireNonNull(clientId, "clientId no pot ser null");

        List<AddressEntity> entities = addressRepository.findByClientId(clientId);

        return entities.stream()
                .map(addressEntityMapper::toModel)
                .collect(Collectors.toList());
    }

    // =====================================
    // CREAR ADREÇA
    // =====================================
    @Transactional
    public AddressModel createAddress(Long clientId, AddressModel model) {
        log.debug("Creant adreça pel client {}", clientId);
        Objects.requireNonNull(clientId, "clientId no pot ser null");
        Objects.requireNonNull(model, "addressModel no pot ser null");

        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        AddressEntity entity = addressEntityMapper.toEntity(model);
        entity.setClient(client);
        AddressEntity saved = addressRepository.save(entity);

        return addressEntityMapper.toModel(saved);
    }

    // =====================================
    // ACTUALITZAR ADREÇA
    // =====================================
    @Transactional
    public AddressModel updateAddress(Long addressId, AddressModel model) {
        log.debug("Actualitzant adreça {}", addressId);
        Objects.requireNonNull(addressId, "addressId no pot ser null");

        AddressEntity entity = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        entity.setStreet(model.getStreet());
        entity.setCity(model.getCity());

        AddressEntity updated = addressRepository.save(entity);
        return addressEntityMapper.toModel(updated);
    }

    // =====================================
    // ELIMINAR ADREÇA
    // =====================================
    @Transactional
    public void deleteAddress(Long addressId) {
        log.debug("Eliminant adreça {}", addressId);
        Objects.requireNonNull(addressId, "addressId no pot ser null");

        AddressEntity entity = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        addressRepository.delete(entity);
        log.info("Adreça {} eliminada", addressId);
    }

    // =====================================
    // ACTUALITZAR TOTES LES ADRECES
    // (edició massiva sense perdre IDs)
    // =====================================
    @Transactional
    public List<AddressModel> updateAddresses(Long clientId, List<AddressModel> addressModels) {
        log.debug("Actualitzant adreces client id={} amb {} adreces",
                clientId,
                addressModels != null ? addressModels.size() : 0);

        Objects.requireNonNull(clientId, "clientId no pot ser null");

        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        List<AddressEntity> existingAddresses = client.getAddresses();

        // Eliminar adreces que no existeixen a la llista nova
        List<Long> incomingIds = addressModels.stream()
                .map(AddressModel::getId)
                .filter(Objects::nonNull)
                .toList();

        List<AddressEntity> toDelete = existingAddresses.stream()
                .filter(addr -> !incomingIds.contains(addr.getId()))
                .toList();

        addressRepository.deleteAll(toDelete);
        existingAddresses.removeAll(toDelete);

        // Actualitzar o afegir noves adreces
        for (AddressModel model : addressModels) {
            if (model.getId() != null) {
                // Actualitzar si existeix
                existingAddresses.stream()
                        .filter(addr -> addr.getId().equals(model.getId()))
                        .findFirst()
                        .ifPresent(addr -> {
                            addr.setStreet(model.getStreet());
                            addr.setCity(model.getCity());
                        });
            } else {
                // Crear nova adreça
                AddressEntity newEntity = addressEntityMapper.toEntity(model);
                newEntity.setClient(client);
                addressRepository.save(newEntity);
                existingAddresses.add(newEntity);
            }
        }

        return existingAddresses.stream()
                .map(addressEntityMapper::toModel)
                .collect(Collectors.toList());
    }
}