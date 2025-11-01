package com.example.Maintenance_Interventions.Controller;

import com.example.Maintenance_Interventions.Repository.EquipementRepository;
import com.example.Maintenance_Interventions.Repository.InterventionRepository;
import com.example.Maintenance_Interventions.Repository.TechnicienRepository;
import com.example.Maintenance_Interventions.entities.Equipement;
import com.example.Maintenance_Interventions.entities.Intervention;
import com.example.Maintenance_Interventions.entities.InterventionPk;
import com.example.Maintenance_Interventions.entities.Technicien;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/interventions")
public class InterventionPageController {

    @Autowired
    private InterventionRepository interventionRepository;
    @Autowired
    private EquipementRepository equipementRepository;
    @Autowired
    private TechnicienRepository technicienRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("interventions", interventionRepository.findByFilters(null, null, null, null));
        return "interventions";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("intervention", new Intervention());
        model.addAttribute("equipements", equipementRepository.findAll());
        model.addAttribute("techniciens", technicienRepository.findAll());
        return "add-intervention";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute Intervention intervention, BindingResult result,
                      @RequestParam("equipementId") Long equipementId,
                      @RequestParam("technicienId") Long technicienId,
                      @RequestParam("dateOuverture") String dateOuvertureStr,
                      Model model) {
        if (result.hasErrors()) {
            model.addAttribute("equipements", equipementRepository.findAll());
            model.addAttribute("techniciens", technicienRepository.findAll());
            return "add-intervention";
        }
        Optional<Equipement> eq = equipementRepository.findById(equipementId);
        Optional<Technicien> tec = technicienRepository.findById(technicienId);
        LocalDate dateOuverture = LocalDate.parse(dateOuvertureStr);
        if (eq.isEmpty() || tec.isEmpty()) {
            result.reject("intervention.invalidRefs", "Equipement ou Technicien invalide");
            model.addAttribute("equipements", equipementRepository.findAll());
            model.addAttribute("techniciens", technicienRepository.findAll());
            return "add-intervention";
        }
        intervention.setEquipement(eq.get());
        intervention.setTechnicien(tec.get());
        intervention.setDateOuverture(dateOuverture);
        interventionRepository.save(intervention);
        return "redirect:/interventions";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam("equipementId") Long equipementId,
                       @RequestParam("technicienId") Long technicienId,
                       @RequestParam("dateOuverture") String dateOuvertureStr,
                       Model model) {
        LocalDate dateOuverture = LocalDate.parse(dateOuvertureStr);
        InterventionPk pk = new InterventionPk(equipementId, technicienId, dateOuverture);
        Intervention it = interventionRepository.findById(pk)
                .orElseThrow(() -> new IllegalArgumentException("Invalid intervention key"));
        model.addAttribute("intervention", it);
        model.addAttribute("equipements", equipementRepository.findAll());
        model.addAttribute("techniciens", technicienRepository.findAll());
        return "update-intervention";
    }

    @PostMapping("/update")
    public String update(@RequestParam("equipementId") Long equipementId,
                         @RequestParam("technicienId") Long technicienId,
                         @RequestParam("dateOuverture") String dateOuvertureStr,
                         @Valid @ModelAttribute Intervention intervention,
                         BindingResult result,
                         Model model) {
        LocalDate dateOuverture = LocalDate.parse(dateOuvertureStr);
        InterventionPk pk = new InterventionPk(equipementId, technicienId, dateOuverture);
        if (result.hasErrors()) {
            intervention.setInterventionPk(pk);
            model.addAttribute("equipements", equipementRepository.findAll());
            model.addAttribute("techniciens", technicienRepository.findAll());
            return "update-intervention";
        }
        intervention.setInterventionPk(pk);
        // allow updating non-PK fields
        List<Equipement> eqs = equipementRepository.findAll();
        List<Technicien> tcs = technicienRepository.findAll();
        model.addAttribute("equipements", eqs);
        model.addAttribute("techniciens", tcs);
        interventionRepository.save(intervention);
        return "redirect:/interventions";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("equipementId") Long equipementId,
                         @RequestParam("technicienId") Long technicienId,
                         @RequestParam("dateOuverture") String dateOuvertureStr) {
        LocalDate dateOuverture = LocalDate.parse(dateOuvertureStr);
        InterventionPk pk = new InterventionPk(equipementId, technicienId, dateOuverture);
        Intervention it = interventionRepository.findById(pk)
                .orElseThrow(() -> new IllegalArgumentException("Invalid intervention key"));
        interventionRepository.delete(it);
        return "redirect:/interventions";
    }
}
