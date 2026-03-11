package com.example.clientmanager.controller;

import com.example.clientmanager.dto.AddressDto;
import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.dto.ClientSummaryDto;
import com.example.clientmanager.dto.ClientDtoModelMapper;
import com.example.clientmanager.dto.AddressDtoModelMapper;
import com.example.clientmanager.model.ClientModel;
import com.example.clientmanager.model.AddressModel;
import com.example.clientmanager.service.ClientService;
import com.example.clientmanager.service.AddressService;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
public class ApplicationController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

    private final ClientService clientService;
    private final AddressService addressService;
    private final ClientDtoModelMapper clientDtoMapper;
    private final AddressDtoModelMapper addressDtoMapper;

    public ApplicationController(ClientService clientService,
                                 AddressService addressService,
                                 ClientDtoModelMapper clientDtoMapper,
                                 AddressDtoModelMapper addressDtoMapper) {
        this.clientService = clientService;
        this.addressService = addressService;
        this.clientDtoMapper = clientDtoMapper;
        this.addressDtoMapper = addressDtoMapper;
    }

    // -------------------------
    // CREAR CLIENT
    // -------------------------
    @PostMapping
    public ResponseEntity<ClientDto> createClient(@Valid @RequestBody ClientDto clientDto) {

        log.info("Crear client: {}", clientDto);

        ClientModel model = clientDtoMapper.toModel(clientDto);
        ClientModel savedModel = clientService.createClient(model);
        ClientDto response = clientDtoMapper.toDto(savedModel);

        log.info("Client creat amb id: {}", response.id());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // -------------------------
    // LLISTAR / CERCAR CLIENTS
    // -------------------------
    @GetMapping
    public ResponseEntity<List<ClientSummaryDto>> getClients(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) Integer edat,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String email) {

        log.info("Cerca clients amb params: query={}, name={}, surname={}, edat={}, dni={}, email={}",
                query, name, surname, edat, dni, email);

        try {

            List<ClientModel> models;

            if (query != null && !query.trim().isEmpty()) {

                models = clientService.searchClients(query);

            } else if (name != null || surname != null || edat != null
                    || dni != null || email != null) {

                models = clientService.searchByFields(name, surname, edat, dni, email);

            } else {

                models = clientService.getAllClients();
            }

            List<ClientSummaryDto> result = models.stream()
                    .map(m -> new ClientSummaryDto(
                            m.getId(),
                            m.getName(),
                            m.getSurname(),
                            m.getEdat(),
                            m.getDni(),
                            m.getEmail()
                    ))
                    .collect(Collectors.toList());

            log.info("Resultats trobats: {}", result.size());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error al cercar clients", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // -------------------------
    // TROBAR CLIENT PER ID
    // -------------------------
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {

        log.info("Trobar client amb id: {}", id);

        ClientModel model = clientService.findById(id);
        ClientDto dto = clientDtoMapper.toDto(model);

        return ResponseEntity.ok(dto);
    }

    // -------------------------
    // ACTUALITZAR CLIENT
    // -------------------------
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ClientDto> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientDto clientDto) {

        log.info("Actualitzar client id={} amb dades: {}", id, clientDto);

        ClientModel model = clientDtoMapper.toModel(clientDto);
        ClientModel updatedModel = clientService.update(id, model);
        ClientDto response = clientDtoMapper.toDto(updatedModel);

        return ResponseEntity.ok(response);
    }

    // -------------------------
    // ELIMINAR CLIENT
    // -------------------------
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {

        log.info("Eliminar client amb id: {}", id);

        clientService.delete(id);

        return ResponseEntity.noContent().build();
    }

    // ==========================
    // ACTUALITZAR NOMÉS ADRECES
    // ==========================
    @PutMapping("/{id:\\d+}/addresses")
    public ResponseEntity<List<AddressDto>> updateAddresses(
            @PathVariable Long id,
            @RequestBody List<AddressDto> addresses) {

        log.info("Actualitzar adreces client id={} amb {} adreces",
                id, addresses != null ? addresses.size() : 0);

        List<AddressModel> models = addresses.stream()
                .map(addressDtoMapper::toModel)
                .toList();

        List<AddressModel> updated = addressService.updateAddresses(id, models);

        List<AddressDto> response = updated.stream()
                .map(addressDtoMapper::toDto)
                .toList();

        return ResponseEntity.ok(response);
    }

    // ==========================
    // ELIMINAR UNA ADREÇA
    // ==========================
    @DeleteMapping("/{clientId:\\d+}/addresses/{addressId:\\d+}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long clientId,
            @PathVariable Long addressId) {

        log.info("Eliminar adreça id={} del client id={}",
                addressId, clientId);

        addressService.deleteAddress(clientId, addressId);

        return ResponseEntity.noContent().build();
    }
}
