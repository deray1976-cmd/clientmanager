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

    // -------------------------
    // ACTUALITZAR ADRECES D'UN CLIENT
    // -------------------------
    @Transactional
    public List<AddressModel> updateAddresses(Long clientId, List<AddressModel> addressModels) {

        log.debug("Actualitzant adreces client id={} amb {} adreces",
                clientId,
                addressModels != null ? addressModels.size() : 0);

        Objects.requireNonNull(clientId, "clientId no pot ser null");

        ClientEntity clientEntity = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        // 🔴 Esborrar adreces existents
        List<AddressEntity> existingAddresses = clientEntity.getAddresses();
        existingAddresses.forEach(addr -> addr.setClient(null));
        addressRepository.deleteAll(existingAddresses);

        clientEntity.getAddresses().clear();

        // 🔴 Afegir noves adreces
        if (addressModels != null && !addressModels.isEmpty()) {

            List<AddressEntity> newEntities = addressModels.stream()
                    .map(addressEntityMapper::toEntity)
                    .peek(addr -> addr.setClient(clientEntity))
                    .collect(Collectors.toList());

            addressRepository.saveAll(newEntities);

            clientEntity.getAddresses().addAll(newEntities);
        }

        log.info("Adreces client id={} actualitzades: {}",
                clientId,
                clientEntity.getAddresses().size());

        return clientEntity.getAddresses().stream()
                .map(addressEntityMapper::toModel)
                .collect(Collectors.toList());
    }

    // -------------------------
    // ELIMINAR ADREÇA
    // -------------------------
    @Transactional
    public void deleteAddress(Long clientId, Long addressId) {

        log.debug("Eliminant adreça id={} del client id={}",
                addressId, clientId);

        Objects.requireNonNull(clientId, "clientId no pot ser null");
        Objects.requireNonNull(addressId, "addressId no pot ser null");

        ClientEntity clientEntity = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        AddressEntity addressEntity = clientEntity.getAddresses().stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        clientEntity.getAddresses().remove(addressEntity);
        addressRepository.delete(addressEntity);

        log.info("Adreça id={} eliminada del client id={}",
                addressId, clientId);
    }
}
