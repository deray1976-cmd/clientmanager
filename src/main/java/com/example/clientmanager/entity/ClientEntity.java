package com.example.clientmanager.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    private Integer edat;
    private String dni;
    private String email;


    // ----- CONSTRUCTORS -----
    public ClientEntity() {} // obligatori per JPA

    // Constructor de conveniència per tests i instàncies ràpides
    public ClientEntity(String name, String surname, Integer edat, String dni, String email) {
        this.name = name;
        this.surname = surname;
        this.edat = edat;
        this.dni = dni;
        this.email = email;
    }
    
    // Aquesta és la clau: una llista d'adreces
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AddressEntity> addresses = new ArrayList<>();

    // ----- GETTERS I SETTERS -----
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

    public List<AddressEntity> getAddresses() { return addresses; }
    public void setAddresses(List<AddressEntity> addresses) { this.addresses = addresses; }
}