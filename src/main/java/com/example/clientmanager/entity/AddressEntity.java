package com.example.clientmanager.entity;
//import jakarta.persistence.*;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

//import jakarta.validation.constraints.NotBlank;


@Entity
@Table(name = "addresses")
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String street;

    @NotBlank
    private String city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    public AddressEntity() {}

    public AddressEntity(String street, String city) {
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

    public ClientEntity getClient() { return client; }

    public void setClient(ClientEntity client) {
        this.client = client;
    }

}
