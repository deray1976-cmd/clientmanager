package com.example.clientmanager.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.example.clientmanager.model.Address;
import com.example.clientmanager.model.Client;

public class ClientMapper {

    public static ClientDto toDto(Client client) {
        List<AddressDto> addresses = client.getAddresses().stream()
                .map(ClientMapper::toDto)
                .collect(Collectors.toList());

        return new ClientDto(
                client.getId(),
                client.getName(),
                client.getEmail(),
                addresses
        );
    }

   public static AddressDto toDto(Address address) {
        return new AddressDto(
                address.getId(),
                address.getStreet(),
                address.getCity()
        );
    }

    // DTO -> Entity
    public static Client toEntity(ClientDto dto) {
        Client client = new Client();
        client.setId(dto.id());
        client.setName(dto.name());
        client.setEmail(dto.email());

        if (dto.addresses() != null) {
            dto.addresses().stream()
                    .map(ClientMapper::toEntity)
                    .forEach(client::addAddress);
        }
        return client;
    }

    public static Address toEntity(AddressDto dto) {
        Address address = new Address();
        address.setId(dto.id());
        address.setStreet(dto.street());
        address.setCity(dto.city());
        return address;
    }
}
