package com.example.clientmanager.dto;

import org.springframework.stereotype.Component;
import com.example.clientmanager.entity.AddressEntity;

@Component
public class AddressMapper {

    // ENTITY -> DTO
    public AddressDto toDto(AddressEntity address) {
        if (address == null) return null;

        Long clientId = address.getClient() != null ? address.getClient().getId() : null;

        return new AddressDto(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                clientId // només guardem l’ID del client
        );
    }

    // DTO -> ENTITY
    public AddressEntity toEntity(AddressDto dto) {
        if (dto == null) return null;

        AddressEntity address = new AddressEntity();
        address.setId(dto.id());
        address.setStreet(dto.street());
        address.setCity(dto.city());
        // el client es setejarà al mapper del ClientEntity o al service abans de persistir
        return address;
    }
}