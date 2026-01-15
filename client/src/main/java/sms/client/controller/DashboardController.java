package sms.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import sms.client.entity.Solde;
import sms.client.service.SoldeService;

@Controller
public class DashboardController {

    private final SoldeService soldeService;

    public DashboardController(SoldeService soldeService) {
        this.soldeService = soldeService;
    }

    // 🔹 AFFICHAGE DU DASHBOARD AVEC LE SOLDE
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        if (token == null) {
            return "redirect:/login";
        }

        try {
            // Récupérer le solde de l'utilisateur
            Solde solde = soldeService.getOrCreateSolde(token);
            model.addAttribute("solde", solde.getMontant());
            model.addAttribute("userId", solde.getUserId());
        } catch (Exception e) {
            model.addAttribute("solde", "0.00");
            model.addAttribute("error", e.getMessage());
        }

        return "dashboard";
    }
}

