package com.example.clientmanager.dto;

import com.example.clientmanager.model.ClientModel;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ClientDtoModelMapper {

    private final AddressDtoModelMapper addressMapper;

    public ClientDtoModelMapper(AddressDtoModelMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    public ClientModel toModel(ClientDto dto) {
        if (dto == null) return null;

        return new ClientModel(
                dto.id(),
                dto.name(),
                dto.surname(),
                dto.edat(),
                dto.dni(),
                dto.email(),
                dto.addresses() != null
                        ? dto.addresses().stream()
                              .map(addressMapper::toModel)
                              .collect(Collectors.toList())
                        : null
        );
    }

    public ClientDto toDto(ClientModel model) {
        if (model == null) return null;

        return new ClientDto(
                model.getId(),
                model.getName(),
                model.getSurname(),
                model.getEdat(),
                model.getDni(),
                model.getEmail(),
                model.getAddresses() != null
                        ? model.getAddresses().stream()
                              .map(addressMapper::toDto)
                              .collect(Collectors.toList())
                        : null
        );
    }
}
