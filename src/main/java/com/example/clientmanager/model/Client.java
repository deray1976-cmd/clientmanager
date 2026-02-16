package com.example.clientmanager.model;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

//import jakarta.persistence.*; // molt important: JPA 3 + Spring Boot 3 requereix jakarta.persistence.*
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // <-- clau primària auto-generada
    @NotBlank(message = "El nom no pot estar buit")
    private String name;
    @NotBlank(message = "L'email no pot estar buit")
    @Email(message = "Format d'email no vàlid")
    private String email;

    public Client(){}
    public Client(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters i Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
   public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @OneToMany(
        mappedBy = "client",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Address> addresses = new ArrayList<>();

public void addAddress(Address address) {
    addresses.add(address);
    address.setClient(this);
}
public void addAddresses(List<Address> addresses) {
    this.addresses.clear();
    for (Address address : addresses) {
        addAddress(address);
    }
}

public void removeAddress(Address address) {
    addresses.remove(address);
    address.setClient(null);
}

    public List<Address> getAddresses(){
        return addresses;
    }
}
