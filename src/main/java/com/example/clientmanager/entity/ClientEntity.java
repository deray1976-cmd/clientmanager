package com.example.clientmanager.entity;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "clients")
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "El nom no pot estar buit")
    @Column(name = "NAME")
    private String name;

    @NotBlank(message = "El cognom no pot estar buit")
    @Column(name = "SURNAME")
    private String surname;

    @NotNull(message = "L'edat no pot estar buida")
    @Column(name = "EDAT", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer edat;

    @NotBlank(message = "DNI no pot estar buit")
    @Column(name = "DNI")
    private String dni;

    @NotBlank(message = "L'email no pot estar buit")
    @Email(message = "Format d'email no vàlid")
    @Column(name = "EMAIL")
    private String email;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AddressEntity> addresses = new ArrayList<>();

    public ClientEntity() {}

    public ClientEntity(String name, String surname, Integer edat, String dni, String email) {
        this.name = name;
        this.surname = surname;
        this.edat = edat;
        this.dni = dni;
        this.email = email;
    }

    // Getters i Setters
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

    public void addAddress(AddressEntity address) {
        addresses.add(address);
        address.setClient(this);
    }

    public void addAddresses(List<AddressEntity> addresses) {
        this.addresses.clear();
        for (AddressEntity address : addresses) {
            addAddress(address);
        }
    }

    public void removeAddress(AddressEntity address) {
        addresses.remove(address);
        address.setClient(null);
    }
}
