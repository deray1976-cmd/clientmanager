package com.example.clientmanager.dto;

import com.example.clientmanager.model.Client;

public class ClientDto {
    private Client client;

    public ClientDto() {
        client = new Client();
    }

    public ClientDto(String name, String email) {
        this();
        client.setName(name);
        client.setEmail(email);
    }

    public ClientDto(Client client){
        client.setName(client.getName());
        client.setEmail(client.getEmail());   
    }

    public Client getClient(){
        return this.client;
    }

}
