package com.example.clientmanager.entity;

import com.example.clientmanager.model.ClientModel;
import com.example.clientmanager.model.AddressModel;
import com.example.clientmanager.entity.ClientEntity;
import com.example.clientmanager.entity.AddressEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientEntityModelMapper {

    private final AddressEntityModelMapper addressEntityMapper;

    public ClientEntityModelMapper(AddressEntityModelMapper addressEntityMapper) {
        this.addressEntityMapper = addressEntityMapper;
    }

    public ClientModel toModel(ClientEntity entity) {
        if (entity == null) return null;

        ClientModel model = new ClientModel(
            entity.getId(),
            entity.getName(),
            entity.getSurname(),
            entity.getEdat(),
            entity.getDni(),
            entity.getEmail(),
            List.of() // <-- llista buida per evitar el constructor undefined
        );

        // Mappejar adreces però sense referència al client dins cada adreça
        List<AddressModel> addresses = entity.getAddresses().stream()
            .map(addressEntityMapper::toModel)
            .collect(Collectors.toList());

        model.setAddresses(addresses);
        return model;
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
            List<AddressEntity> addressEntities = model.getAddresses().stream()
                .map(addressEntityMapper::toEntity)
                .collect(Collectors.toList());
            // assignar client a cada adreça
            addressEntities.forEach(a -> a.setClient(entity));
            entity.setAddresses(addressEntities);
        }

        return entity;
    }
}