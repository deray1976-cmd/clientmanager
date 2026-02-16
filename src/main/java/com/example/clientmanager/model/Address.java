package com.example.clientmanager.model;
//import jakarta.persistence.*;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

//import jakarta.validation.constraints.NotBlank;


@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String street;

    @NotBlank
    private String city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    public Address() {}

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) {  this.id=id; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Client getClient() { return client; }

    public void setClient(Client client) {
        this.client = client;
    }

}
