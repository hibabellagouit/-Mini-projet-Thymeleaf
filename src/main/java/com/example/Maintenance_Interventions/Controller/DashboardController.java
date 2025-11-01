package com.example.Maintenance_Interventions.Controller;

import com.example.Maintenance_Interventions.Repository.EquipementRepository;
import com.example.Maintenance_Interventions.Repository.InterventionRepository;
import com.example.Maintenance_Interventions.Repository.TechnicienRepository;
import com.example.Maintenance_Interventions.entities.Intervention;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final InterventionRepository interventionRepository;
    private final TechnicienRepository technicienRepository;
    private final EquipementRepository equipementRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DashboardController(InterventionRepository interventionRepository,
                               TechnicienRepository technicienRepository,
                               EquipementRepository equipementRepository) {
        this.interventionRepository = interventionRepository;
        this.technicienRepository = technicienRepository;
        this.equipementRepository = equipementRepository;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long technicianId,
            @RequestParam(required = false) String site,
            @RequestParam(required = false) Integer year,
            Model model
    ) {
        // Lists for filters (static enums for now)
        List<String> priorities = Arrays.asList("BASSE", "MOYENNE", "HAUTE");
        List<String> statuses = Arrays.asList("OUVERT", "EN_COURS", "RESOLU");
        model.addAttribute("priorities", priorities);
        model.addAttribute("statuses", statuses);
        model.addAttribute("technicians", technicienRepository.findAll());
        // Distinct sites from equipements
        List<String> sites = equipementRepository.findAll().stream()
                .map(e -> e.getSite())
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        model.addAttribute("sites", sites);

        // Selected filters
        model.addAttribute("selectedPriority", priority);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedTechnicianId", technicianId);
        model.addAttribute("selectedSite", site);

        // Interventions for table (filtered)
        List<Intervention> interventions = interventionRepository
                .findByFilters(priority, status, technicianId, site);
        model.addAttribute("interventions", interventions);

        // KPIs
        Double avgDays = interventionRepository.averageResolutionDays(null, null);
        double mttrHours = (avgDays != null ? avgDays : 0.0) * 24.0;
        model.addAttribute("mttr", Math.round(mttrHours * 10.0) / 10.0); // one decimal

        int currentYear = (year != null ? year : LocalDate.now().getYear());
        int currentMonth = LocalDate.now().getMonthValue();

        // Incidents per month for the selected year
        Map<Integer, Long> incidentsByMonth = new HashMap<>();
        for (Object[] row : interventionRepository.incidentsPerMonth(currentYear)) {
            Integer m = (Integer) row[0];
            Long c = ((Number) row[1]).longValue();
            incidentsByMonth.put(m, c);
        }
        int currentMonthIncidents = incidentsByMonth.getOrDefault(currentMonth, 0L).intValue();
        model.addAttribute("currentMonthIncidents", currentMonthIncidents);

        long openIncidents = interventionRepository.findByFilters(priority, null, technicianId, site)
                .stream()
                .filter(it -> it.getStatut() != null && !"RESOLU".equalsIgnoreCase(it.getStatut()))
                .count();
        model.addAttribute("openIncidents", (int) openIncidents);

        // Chart labels (FR short month names)
        List<String> labels = Arrays.stream(Month.values())
                .map(m -> switch (m) {
                    case JANUARY -> "Jan"; case FEBRUARY -> "Fév"; case MARCH -> "Mar"; case APRIL -> "Avr";
                    case MAY -> "Mai"; case JUNE -> "Juin"; case JULY -> "Juil"; case AUGUST -> "Aoû";
                    case SEPTEMBER -> "Sep"; case OCTOBER -> "Oct"; case NOVEMBER -> "Nov"; case DECEMBER -> "Déc";
                })
                .collect(Collectors.toList());

        // incidents/month array (size 12)
        List<Integer> incidentsPerMonth = new ArrayList<>(Collections.nCopies(12, 0));
        incidentsByMonth.forEach((m, c) -> incidentsPerMonth.set(m - 1, c.intValue()));

        // MTTR per month (hours) computed in-memory
        List<Double> mttrPerMonth = computeMonthlyMttrHours(currentYear);

        try {
            model.addAttribute("chartLabels", objectMapper.writeValueAsString(labels));
            model.addAttribute("incidentsPerMonth", objectMapper.writeValueAsString(incidentsPerMonth));
            model.addAttribute("mttrPerMonth", objectMapper.writeValueAsString(mttrPerMonth));
        } catch (JsonProcessingException e) {
            model.addAttribute("chartLabels", "[]");
            model.addAttribute("incidentsPerMonth", "[]");
            model.addAttribute("mttrPerMonth", "[]");
        }

        return "dashboard";
    }

    private List<Double> computeMonthlyMttrHours(int year) {
        List<List<Long>> durationsByMonth = new ArrayList<>();
        for (int i = 0; i < 12; i++) durationsByMonth.add(new ArrayList<>());

        for (Intervention it : interventionRepository.findAll()) {
            if (it.getDateCloture() == null || it.getDateOuverture() == null) continue;
            if (it.getDateOuverture().getYear() != year) continue;
            long days = ChronoUnit.DAYS.between(it.getDateOuverture(), it.getDateCloture());
            durationsByMonth.get(it.getDateOuverture().getMonthValue() - 1).add(days);
        }
        List<Double> result = new ArrayList<>();
        for (List<Long> monthDurations : durationsByMonth) {
            if (monthDurations.isEmpty()) {
                result.add(0.0);
            } else {
                double avgDays = monthDurations.stream().mapToLong(Long::longValue).average().orElse(0.0);
                result.add(avgDays * 24.0);
            }
        }
        return result;
    }
}
