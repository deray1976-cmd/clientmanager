package com.example.clientmanager.controller;


import com.example.clientmanager.service.ClientService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/clients")
public class ClientController {
private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @PostMapping
    public String create(@RequestParam String nom) {
        service.createClient(nom);
        return "OK";
    }
}
