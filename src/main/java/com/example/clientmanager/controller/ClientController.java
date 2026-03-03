package com.example.clientmanager.controller;

import com.example.clientmanager.dto.AddressDto;
import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.dto.ClientSummaryDto;
import com.example.clientmanager.service.ClientService;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // -------------------------
    // CREAR CLIENT
    // -------------------------
    @PostMapping
    public ResponseEntity<ClientDto> createClient(@Valid @RequestBody ClientDto clientDto) {
        ClientDto created = clientService.createClient(clientDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
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

        try {

            // 🔎 PRIORITAT 1 → Cerca global
            if (query != null && !query.trim().isEmpty()) {
                return ResponseEntity.ok(
                        clientService.searchClientsSummary(query)
                );
            }

            // 🔎 PRIORITAT 2 → Cerca per camps específics
            if (name != null || surname != null || edat != null
                    || dni != null || email != null) {

                return ResponseEntity.ok(
                        clientService.searchByFields(name, surname, edat, dni, email)
                );
            }

            // 📋 Cas per defecte → retornar tots
            return ResponseEntity.ok(clientService.getAllClientslite());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // -------------------------
    // TROBAR CLIENT PER ID
    // -------------------------
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    // -------------------------
    // ACTUALITZAR CLIENT
    // -------------------------
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ClientDto> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientDto clientDto) {

        return ResponseEntity.ok(clientService.update(id, clientDto));
    }

    // -------------------------
    // ELIMINAR CLIENT
    // -------------------------
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
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

        return ResponseEntity.ok(
                clientService.updateAddresses(id, addresses)
        );
    }

    // ==========================
    // ELIMINAR UNA ADREÇA
    // ==========================
    @DeleteMapping("/{clientId:\\d+}/addresses/{addressId:\\d+}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long clientId,
            @PathVariable Long addressId) {

        clientService.deleteAddress(clientId, addressId);
        return ResponseEntity.noContent().build();
    }
}
