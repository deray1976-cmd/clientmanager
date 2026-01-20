package com.example.clientmanager.controller;


import com.example.clientmanager.dto.ClientDto;
import com.example.clientmanager.service.ClientService;

import java.util.List;

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
    public ClientDto createClient(@RequestBody ClientDto clientDto) {
        // Cridem al Service que ja gestiona l'addició al Repository
        return clientService.createClient(clientDto);
    }

    // GET per llistar clients
    @GetMapping
    public List<ClientDto> getClients() {
        return clientService.getAllClients();
    }
}
