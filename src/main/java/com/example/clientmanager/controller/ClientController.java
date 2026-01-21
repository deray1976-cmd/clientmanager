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

     // ✅ POST per crear un client
    @PostMapping
    public ResponseEntity<ClientDto> createClient(@RequestBody ClientDto clientDto) {
    ClientDto created = clientService.createClient(clientDto);
    //return ResponseEntity.ok(created);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);

}

    // GET per llistar clients
    @GetMapping
    public ResponseEntity<List<ClientDto>> getClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        return clientService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(
        @PathVariable Long id,
        @RequestBody ClientDto clientDto) {
        return clientService.update(id, clientDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        return clientService.delete(id)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
    }
}
