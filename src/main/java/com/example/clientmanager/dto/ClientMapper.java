package com.example.clientmanager.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.clientmanager.model.Address;
import com.example.clientmanager.model.Client;

@Component
public class ClientMapper {

    // ENTITY -> DTO
    public ClientDto toDto(Client client) {
        List<AddressDto> addresses = client.getAddresses() == null
                ? List.of()
                : client.getAddresses()
                        .stream()
                        .map(this::toDto)
                        .toList();

        return new ClientDto(
                client.getId(),
                client.getName(),
                client.getEmail(),
                addresses
        );
    }

    public AddressDto toDto(Address address) {
        return new AddressDto(
                address.getId(),
                address.getStreet(),
                address.getCity()
        );
    }

    // DTO -> ENTITY
    public Client toEntity(ClientDto dto) {
        Client client = new Client();
        client.setId(dto.id());
        client.setName(dto.name());
        client.setEmail(dto.email());

        if (dto.addresses() != null) {
            dto.addresses()
                    .stream()
                    .map(this::toEntity)
                    .forEach(client::addAddress);
        }

        return client;
    }

    public Address toEntity(AddressDto dto) {
        Address address = new Address();
        address.setId(dto.id());
        address.setStreet(dto.street());
        address.setCity(dto.city());
        return address;
    }
}