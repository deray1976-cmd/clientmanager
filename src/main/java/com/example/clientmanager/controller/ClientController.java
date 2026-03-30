package com.example.clientmanager.controller;

import com.example.clientmanager.advisors.AdviseController;
import com.example.clientmanager.dto.AddressDto;
import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.dto.ClientSummaryDto;
import com.example.clientmanager.dto.ErrorResponse;
import com.example.clientmanager.exception.ClientNotFoundException;
import com.example.clientmanager.model.ClientModel;
import com.example.clientmanager.service.ClientService;
import com.example.clientmanager.dto.ClientDtoModelMapper;

import java.util.List;

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
    private final AdviseController adviseController;

   public ClientController(ClientService clientService,
                        ClientDtoModelMapper clientDtoMapper,
                        AdviseController adviseController) {
    this.clientService = clientService;
    this.clientDtoMapper = clientDtoMapper;
    this.adviseController = adviseController;
}

    @PostMapping
    public ResponseEntity<?> createClient(@Valid @RequestBody ClientDto clientDto) {
        if (clientDto == null) {
            log.error("ClientDto no pot ser null");
            return ResponseEntity.badRequest().build();
        }

        // En creació, adreces obligatories
        if (clientDto.addresses() == null || clientDto.addresses().isEmpty()) {
            log.error("Cal indicar almenys una adreça per a la creació");
            return ResponseEntity.badRequest().body(ErrorResponse.badRequest(
                    "No es pot crear el client", "Cal indicar almenys una adreça"));
        }

        ClientModel model = clientDtoMapper.toModel(clientDto);
        if (model == null) {
            log.error("ClientModel no pot ser null");
            return ResponseEntity.badRequest().build();
        }

        // Validar amb AdviseController
        if (!adviseController.adviseCreateClient(model)) {
            String errorMsg = adviseController.getLastErrorMessage();
            log.warn("Client no compleix les regles de negoci: {}", errorMsg);
            return ResponseEntity.badRequest().body(ErrorResponse.badRequest(
                    "No es pot crear el client", errorMsg));
        }

        ClientModel saved = clientService.createClient(model);
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
        } catch (ClientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<?> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientDto clientDto) {

        try {
            if (clientDto == null) {
                log.error("ClientDto no pot ser null");
                return ResponseEntity.badRequest().build();
            }

            // Cargar el cliente existente para obtener las adreces si no se proporcionan
            ClientModel existingClient = clientService.findByIdWithAddresses(id);
            if (existingClient == null) {
                log.error("Client no trobat amb id={}", id);
                return ResponseEntity.notFound().build();
            }

            // Convertir DTO a Model
            ClientModel client = clientDtoMapper.toModel(clientDto);
            if (client == null) {
                log.error("ClientModel no pot ser null");
                return ResponseEntity.badRequest().build();
            }

            // Si no se proporcionan adreces, usar las existentes
            if (client.getAddresses() == null || client.getAddresses().isEmpty()) {
                log.info("No se proporcionaron adreces, usando las existentes para el cliente {}", id);
                client.setAddresses(existingClient.getAddresses());
            }

            // Validar con AdviseController
            if (!adviseController.adviseUpdateClient(client)) {
                String errorMsg = adviseController.getLastErrorMessage();
                log.warn("Client no compleix les regles de negoci: {}", errorMsg);
                return ResponseEntity.badRequest().body(ErrorResponse.badRequest(
                        "No es pot actualitzar el client", errorMsg));
            }
            ClientModel updated = clientService.update(id, client);
            return ResponseEntity.ok(clientDtoMapper.toDto(updated));
        } catch (ClientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        try {
            log.info("Intent d'eliminar client {}", id);
            ClientModel client = clientService.findById(id);
            if (client == null) {
                log.error("Client no trobat amb id={}", id);
                return ResponseEntity.notFound().build();
            }

            // Cridem l'AdviseController abans d'eliminar
            if (!adviseController.adviseDeleteClient(client)) {
                String errorMsg = adviseController.getLastErrorMessage();
                log.warn("Client {} no compleix les condicions per a eliminació: {}", id, errorMsg);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.forbidden(
                        "No es pot eliminar el client", errorMsg));
            }

            clientService.delete(id);
            log.info("Client {} eliminat correctament", id);
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
            if (addresses == null || addresses.isEmpty()) {
                log.error("Llista d'adreces no pot ser nulla o buida");
                return ResponseEntity.badRequest().build();
            }
            ClientModel client = clientService.findByIdWithAddresses(clientId);
            if (client == null) {
                log.error("Client no trobat amb id={}", clientId);
                return ResponseEntity.notFound().build();
            }

            // Cridem AdviseController abans d'actualitzar
            if (!adviseController.adviseUpdateAddresses(client, addresses)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            ClientModel updated = clientService.updateAddresses(clientId, addresses);
            return ResponseEntity.ok(clientDtoMapper.toDto(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{clientId:\\d+}/addresses/{addressId:\\d+}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long clientId,
            @PathVariable Long addressId) {

        try {
            ClientModel client = clientService.findByIdWithAddresses(clientId);
            if (client == null) {
                log.error("Client no trobat amb id={}", clientId);
                return ResponseEntity.notFound().build();
            }

            // Cridem AdviseController abans d'eliminar
            if (!adviseController.adviseDeleteAddress(client, addressId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            clientService.deleteAddress(clientId, addressId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}