package sms.client.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import sms.client.entity.Solde;
import sms.client.service.SoldeService;

@Controller
public class SoldeController {

    private final SoldeService soldeService;

    public SoldeController(SoldeService soldeService) {
        this.soldeService = soldeService;
    }

    // 🔹 AFFICHAGE DU FORMULAIRE
    @GetMapping("/solde")
    public String showSoldeForm(HttpSession session, Model model) {

        String token = (String) session.getAttribute("JWT_TOKEN");

        if (token == null) {
            return "redirect:/login";
        }

        model.addAttribute("token", token); // si nécessaire côté view
        return "soldeForm";
    }

    // 🔹 TRAITEMENT DU FORMULAIRE
@PostMapping("/solde")
public String addSolde(
        @RequestParam String montant,
        HttpSession session,
        Model model) {

    String token = (String) session.getAttribute("JWT_TOKEN");

    if (token == null) {
        return "redirect:/login";
    }

    BigDecimal montantBD = new BigDecimal(montant);
    String methode = "addsolde";

    Solde solde = soldeService.addSoldeEtEnvoyerTransaction(
            token,
            montantBD,
            methode
    );

    model.addAttribute("userId", solde.getUserId());
    model.addAttribute("montant", solde.getMontant());
    model.addAttribute("success", "Solde ajouté et transaction envoyée");

    return "dashboard";
}

}
