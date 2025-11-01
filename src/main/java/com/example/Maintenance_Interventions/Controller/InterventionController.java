package com.example.Maintenance_Interventions.Controller;

import com.example.Maintenance_Interventions.Repository.InterventionRepository;
import com.example.Maintenance_Interventions.entities.Intervention;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/interventions")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class InterventionController {

    @Autowired
    private InterventionRepository interventionRepository;

    @GetMapping
    public List<Intervention> getInterventions(
            @RequestParam(required = false) String priorite,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) Long technicienId,
            @RequestParam(required = false) String site
    ) {
        return interventionRepository.findByFilters(priorite, statut, technicienId, site);
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats(@RequestParam(required = false) Integer year,
                                        @RequestParam(required = false) String from,
                                        @RequestParam(required = false) String to) {
        LocalDate fromDate = from != null && !from.isEmpty() ? LocalDate.parse(from) : null;
        LocalDate toDate = to != null && !to.isEmpty() ? LocalDate.parse(to) : null;
        Double avgDays = interventionRepository.averageResolutionDays(fromDate, toDate);

        List<Object[]> rows = interventionRepository.incidentsPerMonth(year);
        List<Map<String, Object>> monthly = new ArrayList<>();
        for (Object[] row : rows) {
            Integer month = (Integer) row[0];
            Long count = ((Number) row[1]).longValue();
            Map<String, Object> item = new HashMap<>();
            item.put("month", month);
            item.put("count", count);
            monthly.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("mttrDays", avgDays != null ? avgDays : 0.0);
        result.put("incidentsPerMonth", monthly);
        return result;
    }

    @PostMapping
    public ResponseEntity<Intervention> create(@Valid @RequestBody Intervention intervention) {
        return ResponseEntity.ok(interventionRepository.save(intervention));
    }
}