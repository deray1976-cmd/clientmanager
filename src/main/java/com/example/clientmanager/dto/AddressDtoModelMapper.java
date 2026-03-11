package com.example.clientmanager.dto;


import com.example.clientmanager.model.AddressModel;
import org.springframework.stereotype.Component;

@Component
public class AddressDtoModelMapper {

    public AddressModel toModel(AddressDto dto) {
        if (dto == null) return null;

        return new AddressModel(
                dto.id(),
                dto.street(),
                dto.city()
        );
    }

    public AddressDto toDto(AddressModel model) {
        if (model == null) return null;

        return new AddressDto(
                model.getId(),
                model.getStreet(),
                model.getCity()
        );
    }
}
