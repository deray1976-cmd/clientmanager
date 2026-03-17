package com.example.clientmanager.controller;

import com.example.clientmanager.dto.AddressDto;
import com.example.clientmanager.dto.AddressDtoModelMapper;
import com.example.clientmanager.model.AddressModel;
import com.example.clientmanager.service.AddressService;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);

    private final AddressService addressService;
    private final AddressDtoModelMapper addressDtoMapper;

    public AddressController(AddressService addressService,
                             AddressDtoModelMapper addressDtoMapper) {
        this.addressService = addressService;
        this.addressDtoMapper = addressDtoMapper;
    }

    // GET adreces d’un client
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AddressDto>> getAddressesByClient(@PathVariable Long clientId) {
        log.info("Obtenir adreces del client {}", clientId);
        List<AddressModel> models = addressService.findByClientId(clientId);
        List<AddressDto> result = models.stream()
                                        .map(addressDtoMapper::toDto)
                                        .toList();
        return ResponseEntity.ok(result);
    }

    // POST nova adreça
    @PostMapping("/client/{clientId}")
    public ResponseEntity<AddressDto> createAddress(
            @PathVariable Long clientId,
            @Valid @RequestBody AddressDto dto) {

        log.info("Crear adreça pel client {}", clientId);
        AddressModel model = addressDtoMapper.toModel(dto);
        AddressModel saved = addressService.createAddress(clientId, model);
        AddressDto response = addressDtoMapper.toDto(saved);
        return ResponseEntity.ok(response);
    }

    // PUT actualitza una adreça
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressDto dto) {

        log.info("Actualitzar adreça {}", addressId);
        AddressModel model = addressDtoMapper.toModel(dto);
        AddressModel updated = addressService.updateAddress(addressId, model);
        AddressDto response = addressDtoMapper.toDto(updated);
        return ResponseEntity.ok(response);
    }

    // DELETE elimina una adreça
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        log.info("Eliminar adreça {}", addressId);
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }
}