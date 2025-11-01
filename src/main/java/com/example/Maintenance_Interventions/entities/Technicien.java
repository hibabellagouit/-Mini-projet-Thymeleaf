package com.example.Maintenance_Interventions.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Technicien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "nom is mandatory")
    private String nom;

    @NotBlank(message = "specialite is mandatory")
    private String specialite;


    @OneToMany(mappedBy = "technicien",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Intervention> interventions = new ArrayList<>();


    // IMPORTANT: Constructeur par défaut OBLIGATOIRE
    public Technicien() {
    }

    // Constructeur pour faciliter la création d'un Technicien
    public Technicien(String nom, String specialite) {
        this.nom = nom;
        this.specialite = specialite;
    }


    // --- Getters et Setters ---
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    public List<Intervention> getInterventions() { return interventions; }
    public void setInterventions(List<Intervention> interventions) { this.interventions = interventions; }
}