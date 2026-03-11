package com.example.clientmanager.dto;

import org.springframework.stereotype.Component;

import com.example.clientmanager.entity.AddressEntity;

@Component
public class AddressMapper {

    // ENTITY -> DTO
    public AddressDto toDto(AddressEntity address) {
        if (address == null) return null;
        return new AddressDto(
                address.getId(),
                address.getStreet(),
                address.getCity()
        );
    }

    // DTO -> ENTITY
    public AddressEntity toEntity(AddressDto dto) {
        if (dto == null) return null;
        AddressEntity address = new AddressEntity();
        address.setId(dto.id());
        address.setStreet(dto.street());
        address.setCity(dto.city());
        return address;
    }
}
