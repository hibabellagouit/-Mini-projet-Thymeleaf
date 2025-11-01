package com.example.Maintenance_Interventions.Controller;

import com.example.Maintenance_Interventions.Repository.TechnicienRepository;
import com.example.Maintenance_Interventions.entities.Technicien;
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
@RequestMapping("/techniciens") // AJOUT: Préfice toutes les routes avec /techniciens
public class TechnicienController {

    @Autowired
    private TechnicienRepository technicienRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("techniciens", technicienRepository.findAll());
        return "techniciens";
    }

    // URL complète: GET /techniciens/signup
    @GetMapping("/signup")
    public String showSignUpForm(Technicien technicien) {
        return "add-technicien";
    }

    // URL complète: POST /techniciens/addTechnicien
    @PostMapping("/addTechnicien")
    public String addTechnicien(@Valid Technicien technicien, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-technicien";
        }

        technicienRepository.save(technicien);
        return "redirect:/techniciens"; // Considérez une redirection vers /techniciens/list
    }

    // URL complète: GET /techniciens/edit/{id}
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Technicien technicien = technicienRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Technicien Id: " + id));
        model.addAttribute("technicien", technicien);
        return "update-technicien";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") long id, @Valid Technicien technicien, BindingResult result) {
        if (result.hasErrors()) {
            technicien.setId(id);
            return "update-technicien";
        }
        technicien.setId(id);
        technicienRepository.save(technicien);
        return "redirect:/techniciens";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id) {
        Technicien technicien = technicienRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Technicien Id: " + id));
        technicienRepository.delete(technicien);
        return "redirect:/techniciens";
    }
}