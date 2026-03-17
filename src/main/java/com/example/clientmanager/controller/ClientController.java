package com.example.clientmanager.controller;

import com.example.clientmanager.dto.AddressDto;
import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.dto.ClientSummaryDto;
import com.example.clientmanager.exception.ClientNotFoundException;
import com.example.clientmanager.model.ClientModel;
import com.example.clientmanager.service.ClientService;
import com.example.clientmanager.dto.ClientDtoModelMapper;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;
    private final ClientDtoModelMapper clientDtoMapper;

    public ClientController(ClientService clientService, ClientDtoModelMapper clientDtoMapper) {
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
    }

    @PostMapping
    public ResponseEntity<ClientDto> createClient(@Valid @RequestBody ClientDto clientDto) {
        ClientModel saved = clientService.createClient(clientDtoMapper.toModel(clientDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(clientDtoMapper.toDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<ClientSummaryDto>> getClients(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) Integer edat,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String email) {

        List<ClientModel> models = (query != null && !query.isBlank())
                ? clientService.searchClients(query)
                : (name != null || surname != null || edat != null || dni != null || email != null)
                ? clientService.searchByFields(name, surname, edat, dni, email)
                : clientService.getAllClients();

        List<ClientSummaryDto> result = models.stream()
                .map(m -> new ClientSummaryDto(m.getId(), m.getName(), m.getSurname(), m.getEdat(), m.getDni(), m.getEmail()))
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ClientDto> getClientById(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") boolean withAddresses) {

        try {
            ClientModel model = withAddresses
                    ? clientService.findByIdWithAddresses(id)
                    : clientService.findById(id);
            return ResponseEntity.ok(clientDtoMapper.toDto(model));
        } catch (ClientNotFoundException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ClientDto> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientDto clientDto) {

        try {
            ClientModel updated = clientService.update(id, clientDtoMapper.toModel(clientDto));
            return ResponseEntity.ok(clientDtoMapper.toDto(updated));
        } catch (ClientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        try {
            clientService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ClientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{clientId:\\d+}/addresses")
    public ResponseEntity<ClientDto> saveAddresses(
            @PathVariable Long clientId,
            @RequestBody List<AddressDto> addresses) {

        try {
            ClientModel updated = clientService.updateAddresses(clientId, addresses);
            return ResponseEntity.ok(clientDtoMapper.toDto(updated));
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{clientId:\\d+}/addresses/{addressId:\\d+}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long clientId,
            @PathVariable Long addressId) {

        try {
            clientService.deleteAddress(clientId, addressId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}