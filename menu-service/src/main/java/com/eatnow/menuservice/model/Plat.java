package com.eatnow.menuservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Plat {

    @Id
    @GeneratedValue
    private UUID id;
    private String nom;
    private String description;
    private BigDecimal prix;

    // Constructeur sans arguments requis par JPA
    public Plat() {
    }

    public Plat(UUID id, String nom, String description, BigDecimal prix) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }
}