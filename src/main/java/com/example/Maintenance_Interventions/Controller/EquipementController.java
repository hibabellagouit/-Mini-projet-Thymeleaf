package com.example.Maintenance_Interventions.Controller;

import com.example.Maintenance_Interventions.Repository.EquipementRepository;
import com.example.Maintenance_Interventions.entities.Equipement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping; // NOUVEL IMPORT

@Controller
@RequestMapping("/equipements") // AJOUT: Tous les mappings commencent maintenant par /equipements
public class EquipementController {

    @Autowired
    private EquipementRepository equipementRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("equipements", equipementRepository.findAll());
        return "equipements";
    }

    // URL complète: GET /equipements/signup
    @GetMapping("/signup")
    public String showSignUpForm(Equipement equipement) {
        return "add-equipement";
    }

    // URL complète: POST /equipements/addEquipement
    @PostMapping("/addEquipement")
    public String addEquipement(@Valid Equipement equipement, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-equipement";
        }

        equipementRepository.save(equipement);
        // Vous devriez rediriger vers une liste ou une page de succès,
        // ou utiliser "/equipements" si vous avez une méthode listEquipements()
        return "redirect:/equipements";
    }

    // URL complète: GET /equipements/edit/{id}
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Equipement equipement = equipementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid equipement Id: " + id));
        model.addAttribute("equipement", equipement);
        return "update-equipement";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") long id, @Valid Equipement equipement, BindingResult result) {
        if (result.hasErrors()) {
            equipement.setId(id);
            return "update-equipement";
        }
        equipement.setId(id);
        equipementRepository.save(equipement);
        return "redirect:/equipements";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id) {
        Equipement equipement = equipementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid equipement Id: " + id));
        equipementRepository.delete(equipement);
        return "redirect:/equipements";
    }
}