package com.example.clientmanager.entity;

import com.example.clientmanager.entity.ClientEntity;
import com.example.clientmanager.model.ClientModel;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ClientEntityModelMapper {

    private final AddressEntityModelMapper addressMapper;

    public ClientEntityModelMapper(AddressEntityModelMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    public ClientModel toModel(ClientEntity entity) {
        if (entity == null) return null;

        return new ClientModel(
                entity.getId(),
                entity.getName(),
                entity.getSurname(),
                entity.getEdat(),
                entity.getDni(),
                entity.getEmail(),
                entity.getAddresses() != null
                        ? entity.getAddresses().stream()
                              .map(addressMapper::toModel)
                              .collect(Collectors.toList())
                        : null
        );
    }

    public ClientEntity toEntity(ClientModel model) {
        if (model == null) return null;

        ClientEntity entity = new ClientEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setSurname(model.getSurname());
        entity.setEdat(model.getEdat());
        entity.setDni(model.getDni());
        entity.setEmail(model.getEmail());

        if (model.getAddresses() != null) {
            entity.addAddresses(
                    model.getAddresses().stream()
                            .map(addressMapper::toEntity)
                            .collect(Collectors.toList())
            );

            // 🔴 IMPORTANT: mantenir la relació bidireccional
            for (AddressEntity address : entity.getAddresses()) {
                address.setClient(entity);
            }
        }

        return entity;
    }
}
