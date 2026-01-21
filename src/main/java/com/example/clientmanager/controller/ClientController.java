package com.example.clientmanager.controller;


import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.service.ClientService;

import java.util.List;

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

     // POST per crear un client
    @PostMapping
    public ResponseEntity<ClientDto> createClient(@RequestBody ClientDto clientDto) {
        ClientDto created = clientService.createClient(clientDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET per llistar clients
    @GetMapping
    public ResponseEntity<List<ClientDto>> getClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        try {
            ClientDto client = clientService.findById(id);
            return ResponseEntity.ok(client);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(
        @PathVariable Long id,
        @RequestBody ClientDto clientDto) {
        try {
            ClientDto updated = clientService.update(id, clientDto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        try {
            clientService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
