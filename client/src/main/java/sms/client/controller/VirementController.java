package sms.client.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import sms.client.entity.Solde;
import sms.client.service.VirementService;

@Controller
public class VirementController {

    private final VirementService virementService;

    public VirementController(VirementService virementService) {
        this.virementService = virementService;
    }

    @GetMapping("/virement")
    public String showVirementForm(HttpSession session, Model model) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        if (token == null) {
            return "redirect:/login";
        }

        model.addAttribute("token", token);
        return "virement";
    }

    @PostMapping("/virement")
    public String traiterVirement(
            @RequestParam String montant,
            @RequestParam String destinataire,
            HttpSession session,
            Model model
    ) {
        String token = (String) session.getAttribute("JWT_TOKEN");

        if (token == null) {
            return "redirect:/login";
        }

        try {
            BigDecimal montantBD = new BigDecimal(montant);
            Solde solde = virementService.effectuerVirement(token, montantBD, destinataire);
            
            model.addAttribute("success", "Virement de " + montant + " vers " + destinataire + " effectué avec succès.");
            return "dashboard";
            
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            return "virement";
        }
    }
}

