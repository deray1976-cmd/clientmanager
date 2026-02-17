package com.example.clientmanager.dto;

import org.springframework.stereotype.Component;

import com.example.clientmanager.model.Address;

@Component
public class AddressMapper {

    // ENTITY -> DTO
    public AddressDto toDto(Address address) {
        if (address == null) return null;
        return new AddressDto(
                address.getId(),
                address.getStreet(),
                address.getCity()
        );
    }

    // DTO -> ENTITY
    public Address toEntity(AddressDto dto) {
        if (dto == null) return null;
        Address address = new Address();
        address.setId(dto.id());
        address.setStreet(dto.street());
        address.setCity(dto.city());
        return address;
    }
}
