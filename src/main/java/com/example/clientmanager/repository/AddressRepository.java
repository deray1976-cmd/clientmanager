package com.example.clientmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.clientmanager.entity.AddressEntity;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    // Pots afegir mètodes específics si cal, per exemple:
    // List<Address> findByClientId(Long clientId);
}