package com.example.clientmanager.controller;

import com.example.clientmanager.advisors.AdviseController;
import com.example.clientmanager.dto.AddressDto;
import com.example.clientmanager.dto.AddressDtoModelMapper;
import com.example.clientmanager.dto.ErrorResponse;
import com.example.clientmanager.model.AddressModel;
import com.example.clientmanager.model.ClientModel;
import com.example.clientmanager.service.AddressService;
import com.example.clientmanager.service.ClientService;

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
    private final ClientService clientService;

    private final AddressDtoModelMapper addressDtoMapper;

    private final AdviseController adviseController;

    public AddressController(AddressService addressService,
                         AddressDtoModelMapper addressDtoMapper,
                         ClientService clientService,
                         AdviseController adviseController) {
    this.addressService = addressService;
    this.addressDtoMapper = addressDtoMapper;
    this.clientService = clientService;
    this.adviseController = adviseController;
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

    @PostMapping("/client/{clientId}")
public ResponseEntity<?> createAddress(
        @PathVariable Long clientId,
        @Valid @RequestBody AddressDto dto) {

    if (dto == null) {
        log.error("AddressDto no pot ser null");
        return ResponseEntity.badRequest().build();
    }

    ClientModel client = clientService.findByIdWithAddresses(clientId);
    if (client == null) {
        log.error("Client no trobat amb id={}", clientId);
        return ResponseEntity.notFound().build();
    }

    // Validar amb AdviseController (specific per a creació)
    if (!adviseController.adviseCreateAddress(client, dto)) {
        String errorMsg = adviseController.getLastErrorMessage();
        log.warn("Adreça no compleix les regles de negoci: {}", errorMsg);
        return ResponseEntity.badRequest().body(ErrorResponse.badRequest(
                "No es pot crear l'adreça", errorMsg));
    }

    AddressModel model = addressDtoMapper.toModel(dto);
    if (model == null) {
        log.error("AddressModel no pot ser null");
        return ResponseEntity.badRequest().build();
    }

    // assignar client al model
    model.setClientId(client.getId());

    AddressModel saved = addressService.createAddress(clientId, model);
    AddressDto response = addressDtoMapper.toDto(saved);
    log.info("Adreça creada correctament per al client {}", clientId);
    return ResponseEntity.ok(response);
}

@PutMapping("/{addressId}")
public ResponseEntity<AddressDto> updateAddress(
        @PathVariable Long addressId,
        @Valid @RequestBody AddressDto dto) {

    if (dto == null) {
        log.error("AddressDto no pot ser null");
        return ResponseEntity.badRequest().build();
    }

    if (dto.clientId() == null) {
        log.error("ClientId no pot ser null en AddressDto");
        return ResponseEntity.badRequest().build();
    }

    AddressModel model = addressDtoMapper.toModel(dto);
    if (model == null) {
        log.error("AddressModel no pot ser null");
        return ResponseEntity.badRequest().build();
    }

    ClientModel client = clientService.findByIdWithAddresses(dto.clientId());
    if (client == null) {
        log.error("Client no trobat amb id={}", dto.clientId());
        return ResponseEntity.notFound().build();
    }

    if (!adviseController.adviseUpdateAddresses(client, List.of(dto))) {
        return ResponseEntity.badRequest().build();
    }

    AddressModel updated = addressService.updateAddress(addressId, model);
    return ResponseEntity.ok(addressDtoMapper.toDto(updated));
}

    // DELETE elimina una adreça
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        log.info("Intent d'eliminar adreça {}", addressId);

        // Recuperar l'adreça
        AddressModel addressModel = addressService.findById(addressId);
        if (addressModel == null) {
            log.error("Adreça no trobada amb id={}", addressId);
            return ResponseEntity.notFound().build();
        }

        if (addressModel.getClientId() == null) {
            log.error("ClientId no trobat per a l'adreça {}", addressId);
            return ResponseEntity.badRequest().build();
        }

        // Recuperar el client a partir del clientId
        ClientModel client = clientService.findByIdWithAddresses(addressModel.getClientId());
        if (client == null) {
            log.error("Client no trobat amb id={}", addressModel.getClientId());
            return ResponseEntity.notFound().build();
        }

        // Validació amb AdviseController
        if (!adviseController.adviseDeleteAddress(client, addressId)) {
            log.warn("Adreça {} no compleix les condicions per a eliminació", addressId);
            return ResponseEntity.badRequest().build();
        }

        addressService.deleteAddress(addressId);
        log.info("Adreça {} eliminada correctament", addressId);
        return ResponseEntity.noContent().build();
    }
}