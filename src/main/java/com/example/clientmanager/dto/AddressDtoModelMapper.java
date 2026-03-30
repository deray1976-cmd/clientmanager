package com.example.clientmanager.dto;

import com.example.clientmanager.model.AddressModel;
import com.example.clientmanager.model.ClientModel;
import org.springframework.stereotype.Component;

@Component
public class AddressDtoModelMapper {

    // DTO -> MODEL
    public AddressModel toModel(AddressDto dto) {
        if (dto == null) return null;

        AddressModel model = new AddressModel(
                dto.id(),
                dto.street(),
                dto.city()
        );
        // Assignem només clientId del DTO (si existeix)
        model.setClientId(dto.clientId());
        return model;
    }

    // MODEL -> DTO
    public AddressDto toDto(AddressModel model) {
        if (model == null) return null;

        return new AddressDto(
                model.getId(),
                model.getStreet(),
                model.getCity(),
                model.getClientId() // només l'ID del client
        );
    }

    // Opcional: DTO -> MODEL amb ClientModel associat
    public AddressModel toModel(AddressDto dto, ClientModel client) {
        if (dto == null) return null;

        AddressModel model = new AddressModel(
                dto.id(),
                dto.street(),
                dto.city()
        );
        if (client != null) {
            model.setClientId(client.getId()); // només assignem ID
        }
        return model;
    }
}