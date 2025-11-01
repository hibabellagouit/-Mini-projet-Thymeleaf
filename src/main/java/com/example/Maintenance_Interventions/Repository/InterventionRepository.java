package com.example.Maintenance_Interventions.Repository;

import com.example.Maintenance_Interventions.entities.Intervention;
import com.example.Maintenance_Interventions.entities.InterventionPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InterventionRepository extends JpaRepository<Intervention, InterventionPk> {

    @Query("SELECT DISTINCT i FROM Intervention i JOIN FETCH i.equipement e JOIN FETCH i.technicien t " +
            "WHERE (:priorite IS NULL OR i.priorite = :priorite) " +
            "AND (:statut IS NULL OR i.statut = :statut) " +
            "AND (:technicienId IS NULL OR t.id = :technicienId) " +
            "AND (:site IS NULL OR e.site = :site)")
    List<Intervention> findByFilters(@Param("priorite") String priorite,
                                     @Param("statut") String statut,
                                     @Param("technicienId") Long technicienId,
                                     @Param("site") String site);

    @Query("SELECT AVG(cast(timestampdiff(DAY, i.interventionPk.dateOuverture, i.dateCloture) as double)) " +
            "FROM Intervention i " +
            "WHERE i.dateCloture IS NOT NULL " +
            "AND (:from IS NULL OR i.interventionPk.dateOuverture >= :from) " +
            "AND (:to IS NULL OR i.interventionPk.dateOuverture <= :to)")
    Double averageResolutionDays(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT function('month', i.interventionPk.dateOuverture) as month, COUNT(i) as count " +
            "FROM Intervention i " +
            "WHERE (:year IS NULL OR function('year', i.interventionPk.dateOuverture) = :year) " +
            "GROUP BY function('month', i.interventionPk.dateOuverture) " +
            "ORDER BY month")
    List<Object[]> incidentsPerMonth(@Param("year") Integer year);
}
