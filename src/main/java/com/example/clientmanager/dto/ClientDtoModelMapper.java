package com.example.clientmanager.dto;

import com.example.clientmanager.model.ClientModel;
import com.example.clientmanager.model.AddressModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientDtoModelMapper {

    private final AddressDtoModelMapper addressMapper;

    public ClientDtoModelMapper(AddressDtoModelMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    // -------------------
    // DTO -> MODEL
    // -------------------
    public ClientModel toModel(ClientDto dto) {
        if (dto == null) return null;

        List<AddressModel> addresses = dto.addresses() != null
                ? dto.addresses().stream()
                    .map(addressDto -> {
                        AddressModel model = addressMapper.toModel(addressDto);
                        model.setClientId(dto.id()); // assignem només l'ID del client
                        return model;
                    })
                    .collect(Collectors.toList())
                : null;

        return new ClientModel(
                dto.id(),
                dto.name(),
                dto.surname(),
                dto.edat(),
                dto.dni(),
                dto.email(),
                addresses
        );
    }

    // -------------------
    // MODEL -> DTO
    // -------------------
    public ClientDto toDto(ClientModel model) {
        if (model == null) return null;

        List<AddressDto> addresses = model.getAddresses() != null
                ? model.getAddresses().stream()
                      .map(addressMapper::toDto)
                      .collect(Collectors.toList())
                : null;

        return new ClientDto(
                model.getId(),
                model.getName(),
                model.getSurname(),
                model.getEdat(),
                model.getDni(),
                model.getEmail(),
                addresses
        );
    }
}