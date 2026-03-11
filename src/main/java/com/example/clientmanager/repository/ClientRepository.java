package com.example.clientmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.clientmanager.entity.ClientEntity;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClientRepository 
        extends JpaRepository<ClientEntity, Long>, JpaSpecificationExecutor<ClientEntity> {   // mètode definit per Spring Data JPA
    List<ClientEntity> findByNameIgnoreCaseContainingOrEmailIgnoreCaseContaining(String name, String email);

       @Query("SELECT c FROM ClientEntity c WHERE " +
       "LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(c.surname) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "c.dni = :query OR " +
       "(FUNCTION('TO_CHAR', c.edat) = :query)")
    List<ClientEntity> searchByAllFields(@Param("query") String query);
}