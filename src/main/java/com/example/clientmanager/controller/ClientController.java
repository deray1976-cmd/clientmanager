package com.example.clientmanager.controller;


import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.service.ClientService;

//import jakarta.validation.Valid;

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

     // POST per crear un client
    @PostMapping
    public ResponseEntity<ClientDto> createClient(@Valid @RequestBody ClientDto clientDto) {
        ClientDto created = clientService.createClient(clientDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /*@GetMapping("/search")
    public ResponseEntity<List<ClientDto>> searchClient(@RequestParam String query) {
        List<ClientDto> clients = clientService.findByNameOrEmail(query);
        return ResponseEntity.ok(clients);
    }   

    // GET per llistar clients
    @GetMapping
    public ResponseEntity<List<ClientDto>> getClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }*/

        // GET per llistar clients (amb o sense filtre)
@GetMapping
public ResponseEntity<List<ClientDto>> getClients(
        @RequestParam(required = false) String query) {

    if (query == null || query.trim().isEmpty()) {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    return ResponseEntity.ok(clientService.findByNameOrEmail(query));
}

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        ClientDto client = clientService.findById(id); // llença ClientNotFoundException si no existeix
        return ResponseEntity.ok(client);
    }
    
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ClientDto> updateClient(
        @PathVariable Long id,
        @Valid @RequestBody ClientDto clientDto) {
        
            ClientDto updated = clientService.update(id, clientDto);
            return ResponseEntity.ok(updated);
        
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        
            clientService.delete(id);
            return ResponseEntity.noContent().build();
        
    }
}
