package com.example.clientmanager.model;

import java.util.ArrayList;
import java.util.List;

public class ClientModel {

    private Long id;
    private String name;
    private String surname;
    private Integer edat;
    private String dni;
    private String email;
    private List<AddressModel> addresses;

    public ClientModel() {
        this.addresses = new ArrayList<>();
    }

    // Constructor complet amb llista d'adreces
    public ClientModel(Long id, String name, String surname, Integer edat, String dni, String email, List<AddressModel> addresses) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.edat = edat;
        this.dni = dni;
        this.email = email;
        // si la llista és null, inicialitzem una llista buida
        this.addresses = addresses != null ? addresses : new ArrayList<>();
    }

    // Constructor sense adreces (opcjonal)
    public ClientModel(Long id, String name, String surname, Integer edat, String dni, String email) {
        this(id, name, surname, edat, dni, email, new ArrayList<>());
    }

    // Getters i setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public Integer getEdat() { return edat; }
    public void setEdat(Integer edat) { this.edat = edat; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<AddressModel> getAddresses() { return addresses; }
    public void setAddresses(List<AddressModel> addresses) { this.addresses = addresses; }

    // Afegir una adreça individual
    public void addAddress(AddressModel address) {
        if (this.addresses == null) this.addresses = new ArrayList<>();
        this.addresses.add(address);
    }
}