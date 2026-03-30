package com.example.clientmanager.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.clientmanager.model.ClientModel;
import com.example.clientmanager.model.AddressModel;

@Component
public class ClientMapper {

    private final AddressDtoModelMapper addressMapper;

    public ClientMapper(AddressDtoModelMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    // -------------------
    // MODEL -> DTO
    // -------------------
    public ClientDto toDto(ClientModel client) {
        List<AddressDto> addresses = client.getAddresses() == null
                ? List.of()
                : client.getAddresses()
                        .stream()
                        .map(addressMapper::toDto)
                        .collect(Collectors.toList());

        return new ClientDto(
                client.getId(),
                client.getName(),
                client.getSurname(),
                client.getEdat(),
                client.getDni(),
                client.getEmail(),
                addresses
        );
    }

    // -------------------
    // DTO -> MODEL
    // -------------------
    public ClientModel toModel(ClientDto dto) {
        ClientModel client = new ClientModel();
        client.setId(dto.id());
        client.setName(dto.name());
        client.setSurname(dto.surname());
        client.setEdat(dto.edat());
        client.setDni(dto.dni());
        client.setEmail(dto.email());

        if (dto.addresses() != null) {
            List<AddressModel> addressModels = dto.addresses()
                    .stream()
                    .map(addressDto -> {
                        AddressModel model = addressMapper.toModel(addressDto);
                        model.setClientId(client.getId()); // assignem només l’ID
                        return model;
                    })
                    .collect(Collectors.toList());
            client.setAddresses(addressModels);
        }

        return client;
    }

    // -------------------
    // MODEL -> RESUM DTO
    // -------------------
    public ClientSummaryDto toSummaryDto(ClientModel client) {
        return new ClientSummaryDto(
                client.getId(),
                client.getName(),
                client.getSurname(),
                client.getEdat(),
                client.getDni(),
                client.getEmail()
        );
    }
}