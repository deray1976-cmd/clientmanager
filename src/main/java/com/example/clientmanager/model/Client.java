package com.example.clientmanager.model;
import jakarta.persistence.*; // molt important: JPA 3 + Spring Boot 3 requereix jakarta.persistence.*

@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // <-- clau primària auto-generada
    private String name;
    private String email;

    public Client(){}
    public Client(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters i Setters
   public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
