package com.example.clientmanager.entity;

import com.example.clientmanager.model.AddressModel;
import org.springframework.stereotype.Component;

@Component
public class AddressEntityModelMapper {

    public AddressModel toModel(AddressEntity entity) {
        if (entity == null) return null;

        return new AddressModel(
                entity.getId(),
                entity.getStreet(),
                entity.getCity()
        );
    }

    public AddressEntity toEntity(AddressModel model) {
        if (model == null) return null;

        AddressEntity entity = new AddressEntity();
        entity.setId(model.getId());
        entity.setStreet(model.getStreet());
        entity.setCity(model.getCity());
        return entity;
    }
}
