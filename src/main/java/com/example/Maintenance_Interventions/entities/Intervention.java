package com.example.Maintenance_Interventions.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity

public class Intervention {

    @EmbeddedId // Utilise la clé composite définie dans InterventionPk
    private InterventionPk interventionPk;

    @NotNull(message = "La date de clôture est obligatoire")
    private LocalDate dateCloture; // Champ supplémentaire, non inclus dans la PK

    @NotBlank(message = "La priorité est obligatoire")
    private String priorite;

    @NotBlank(message = "Le statut est obligatoire")
    private String statut;

    // -------------------------------------------------------------------
    // Relations (Mappage des IDs de la clé composite aux entités)
    // -------------------------------------------------------------------

    // Relation Many-to-One avec Equipement
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("equipementId") // Mappe l'attribut 'equipementId' de InterventionPk
    @JoinColumn(name = "equipement_id", nullable = false)
    private Equipement equipement;

    // Relation Many-to-One avec Technicien
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("technicienId") // Mappe l'attribut 'technicienId' de InterventionPk
    @JoinColumn(name = "technicien_id", nullable = false)
    private Technicien technicien;

    // Constructeur par défaut
    public Intervention() {
        this.interventionPk = new InterventionPk(); // Initialisation pour éviter NullPointerException
    }

    // Constructeur avec champs nécessaires
    public Intervention(Equipement equipement, Technicien technicien, LocalDate dateOuverture) {
        this.equipement = equipement;
        this.technicien = technicien;
        this.interventionPk = new InterventionPk(equipement.getId(), technicien.getId(), dateOuverture);
    }

    // --- Getters et Setters ---

    public InterventionPk getInterventionPk() { return interventionPk; }
    public void setInterventionPk(InterventionPk interventionPk) { this.interventionPk = interventionPk; }

    // Raccourcis pour accéder aux champs de la clé directement:
    public LocalDate getDateOuverture() {
        return this.interventionPk != null ? this.interventionPk.getDateOuverture() : null;
    }
    public void setDateOuverture(LocalDate dateOuverture) {
        if (this.interventionPk == null) this.interventionPk = new InterventionPk();
        this.interventionPk.setDateOuverture(dateOuverture);
    }

    // ... (Getters/Setters pour les autres champs) ...
    public LocalDate getDateCloture() { return dateCloture; }
    public void setDateCloture(LocalDate dateCloture) { this.dateCloture = dateCloture; }

    public String getPriorite() { return priorite; }
    public void setPriorite(String priorite) { this.priorite = priorite; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Equipement getEquipement() { return equipement; }
    public void setEquipement(Equipement equipement) {
        this.equipement = equipement;
        if (this.interventionPk == null) this.interventionPk = new InterventionPk();
        this.interventionPk.setEquipementId(equipement != null ? equipement.getId() : null);
    }

    public Technicien getTechnicien() { return technicien; }
    public void setTechnicien(Technicien technicien) {
        this.technicien = technicien;
        if (this.interventionPk == null) this.interventionPk = new InterventionPk();
        this.interventionPk.setTechnicienId(technicien != null ? technicien.getId() : null);
    }
}