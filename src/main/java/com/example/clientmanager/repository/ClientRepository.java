package com.example.clientmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.clientmanager.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
   // mètode definit per Spring Data JPA
    List<Client> findByNameIgnoreCaseContainingOrEmailIgnoreCaseContaining(String name, String email);
}