package com.example.clientmanager.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.clientmanager.entity.ClientEntity;

@Component
public class ClientMapper {

    private final AddressMapper addressMapper;

    public ClientMapper(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    // ENTITY -> DTO
    public ClientDto toDto(ClientEntity client) {
        List<AddressDto> addresses = client.getAddresses() == null
                ? List.of()
                : client.getAddresses()
                        .stream()
                        .map(addressMapper::toDto)
                        .collect(Collectors.toList());

           return new ClientDto(
                client.getId(),
                client.getName(),
                client.getSurname(),   // nou camp cognom
                client.getEdat(),       // nou camp edat
                client.getDni(),       // nou camp dni
                client.getEmail(),
                addresses
        );
    }

    // DTO -> ENTITY
    public ClientEntity toEntity(ClientDto dto) {
        ClientEntity client = new ClientEntity();
        client.setId(dto.id());
        client.setName(dto.name());
        client.setEmail(dto.email());
        client.setSurname(dto.surname());   // nou camp cognom
        client.setEdat(dto.edat());           // nou camp edat
        client.setDni(dto.dni());           // nou camp dni
        client.setEmail(dto.email());

        if (dto.addresses() != null) {
            dto.addresses()
                    .stream()
                    .map(addressMapper::toEntity)
                    .forEach(client::addAddress); // suposant que addAddress estableix client a Address
        }

        return client;
    }

    public ClientSummaryDto toSummaryDto(ClientEntity client) {
    return new ClientSummaryDto(
        client.getId(),
        client.getName(),
        client.getSurname(),  // afegim cognom al resum si volem
        client.getEdat(),
        client.getDni(),
        client.getEmail()
    );
}
}