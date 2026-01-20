package com.example.clientmanager.repository;

import org.springframework.stereotype.Repository;

@Repository
public class ClientRepository {

    public void save(String nom) {
        System.out.println("Client guardat: " + nom);
    }
}