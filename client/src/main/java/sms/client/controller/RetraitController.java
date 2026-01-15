package sms.client.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import sms.client.entity.Solde;
import sms.client.service.RetraitService;

@Controller
public class RetraitController {

    private final RetraitService retraitService;

    public RetraitController(RetraitService retraitService) {
        this.retraitService = retraitService;
    }

    // AFFICHAGE DU FORMULAIRE DE RETRAIT
    @GetMapping("/retrait")
    public String showRetraitForm(HttpSession session, Model model) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        if (token == null) {
            return "redirect:/login";
        }

        model.addAttribute("token", token);
        return "retrait";
    }
    

    // TRAITEMENT DU RETRAIT
    @PostMapping("/retrait")
    public String traiterRetrait(
            @RequestParam String montant,
            HttpSession session,
            Model model
    ) {
        String token = (String) session.getAttribute("JWT_TOKEN");

        if (token == null) {
            return "redirect:/login";
        }
        String methodName = "traiterRetrait";
        try {
            BigDecimal montantBD = new BigDecimal(montant);
            Solde solde = retraitService.retirerSolde(token, montantBD);
            
            model.addAttribute("success", "Retrait de " + montant + " effectué avec succès. Nouveau solde: " + solde.getMontant());
            return "dashboard";
            
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            return "retrait";
        }
    }
}

