package com.example.Maintenance_Interventions.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable // Indique à JPA que cette classe est une clé composite embarquée.
public class InterventionPk implements Serializable {

    @Column(name = "equipement_id")
    private Long equipementId;

    @Column(name = "technicien_id")
    private Long technicienId;

    @Column(name = "date_ouverture")
    private LocalDate dateOuverture;

    // Constructeur par défaut obligatoire pour JPA
    public InterventionPk() {}

    public InterventionPk(Long equipementId, Long technicienId, LocalDate dateOuverture) {
        this.equipementId = equipementId;
        this.technicienId = technicienId;
        this.dateOuverture = dateOuverture;
    }

    // --- Getters & Setters ---

    public Long getEquipementId() {
        return equipementId;
    }

    public void setEquipementId(Long equipementId) {
        this.equipementId = equipementId;
    }

    public Long getTechnicienId() {
        return technicienId;
    }

    public void setTechnicienId(Long technicienId) {
        this.technicienId = technicienId;
    }

    public LocalDate getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(LocalDate dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    // --- equals & hashCode (Obligatoires pour EmbeddedId) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterventionPk that)) return false;
        return Objects.equals(equipementId, that.equipementId)
                && Objects.equals(technicienId, that.technicienId)
                && Objects.equals(dateOuverture, that.dateOuverture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipementId, technicienId, dateOuverture);
    }
}