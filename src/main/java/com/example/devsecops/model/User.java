package com.example.devsecops.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String email;
    
    // Constructeur par défaut (requis par JPA)
    public User() {
    }
    
    // Constructeur avec paramètres
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
    
    // Constructeur avec ID
    public User(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
