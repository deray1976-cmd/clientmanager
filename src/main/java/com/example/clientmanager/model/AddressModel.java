package com.example.clientmanager.model;

public class AddressModel {

    private Long id;
    private String street;
    private String city;

    // Només guardem l'id del client, no l'objecte complet
    private Long clientId;

    public AddressModel() {}

    public AddressModel(Long id, String street, String city) {
        this.id = id;
        this.street = street;
        this.city = city;
    }

    // getters i setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    // opcional: mètode de conveniència per saber si l'adreça té client assignat
    public boolean hasClient() { 
        return clientId != null; 
    }
}