package com.example.clientmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.clientmanager.entity.ClientEntity;

public interface ClientRepository 
        extends JpaRepository<ClientEntity, Long>, JpaSpecificationExecutor<ClientEntity> {

    // Buscador existent
    List<ClientEntity> findByNameIgnoreCaseContainingOrEmailIgnoreCaseContaining(String name, String email);

    @Query("SELECT c FROM ClientEntity c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.surname) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "c.dni = :query OR " +
           "(FUNCTION('TO_CHAR', c.edat) = :query)")
    List<ClientEntity> searchByAllFields(@Param("query") String query);

    // =========================
    // Nous mètodes per evitar N+1
    // =========================

    // Carregar client amb totes les adreces a partir de l'ID
    @Query("SELECT c FROM ClientEntity c LEFT JOIN FETCH c.addresses WHERE c.id = :id")
    Optional<ClientEntity> findByIdWithAddresses(@Param("id") Long id);

    // Cercar client per adreça (carrer + ciutat)
    @Query("SELECT c FROM ClientEntity c LEFT JOIN FETCH c.addresses a WHERE a.street = :street AND a.city = :city")
    Optional<ClientEntity> findByAddress(@Param("street") String street, @Param("city") String city);
}