package com.example.Maintenance_Interventions.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "code is mandatory")
    private String code;

    @NotBlank(message = "type is mandatory")
    private String type;

    @NotBlank(message = "criticite is mandatory")
    private String criticite;

    @NotBlank(message = "site is mandatory")
    private String site;

    @OneToMany(mappedBy = "equipement",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Intervention> interventions = new ArrayList<>();

    // IMPORTANT: Constructeur par défaut sans argument OBLIGATOIRE
    public Equipement() {
    }

    // Constructeur avec quelques arguments
    public Equipement(String code, String type, String criticite, String site) {
        this.code = code;
        this.type = type;
        this.criticite = criticite;
        this.site = site;
    }

    // --- Getters et Setters ---
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCriticite() { return criticite; }
    public void setCriticite(String criticite) { this.criticite = criticite; }
    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }
    public List<Intervention> getInterventions() { return interventions; }
    public void setInterventions(List<Intervention> interventions) { this.interventions = interventions; }
}