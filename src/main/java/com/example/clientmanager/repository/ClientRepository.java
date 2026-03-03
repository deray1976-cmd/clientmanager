package com.example.clientmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.clientmanager.model.Client;

public interface ClientRepository 
        extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {   // mètode definit per Spring Data JPA
    List<Client> findByNameIgnoreCaseContainingOrEmailIgnoreCaseContaining(String name, String email);

       @Query("SELECT c FROM Client c WHERE " +
       "LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(c.surname) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "c.dni = :query OR " +
       "(FUNCTION('TO_CHAR', c.edat) = :query)")
    List<Client> searchByAllFields(@Param("query") String query);
}