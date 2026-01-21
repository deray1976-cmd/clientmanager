package com.example.clientmanager.dto;

import com.example.clientmanager.model.Client;

public class ClientDto {
    private Client client;

    public ClientDto() {
        client = new Client();
    }

    public ClientDto(Client client){
        this();
        this.client.setId(client.getId());
        this.client.setName(client.getName());
        this.client.setEmail(client.getEmail());
    }
    public ClientDto(String name, String email) {
        this();
        client.setName(name);
        client.setEmail(email);
    }

   // ✅ Getters i setters directes pels camps
    public Long getId() { return client.getId(); }
    public String getName() { return client.getName(); }
    public String getEmail() { return client.getEmail(); }

    public void setId(Long id) { client.setId(id); }
    public void setName(String name) { client.setName(name); }
    public void setEmail(String email) { client.setEmail(email); }

    public Client getClient() { return client; } // opcional
   
}
