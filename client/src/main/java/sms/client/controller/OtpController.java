package sms.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import sms.client.dto.otp.OtpResponseDTO;
import sms.client.dto.otp.OtpSendRequestDTO;
import sms.client.dto.otp.OtpVerifyRequestDTO;
import sms.client.service.OtpService;

@Controller
@RequestMapping("/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    /**
     * Affiche le formulaire de vérification OTP
     */
    @GetMapping("/verify")
    public String afficherFormulaireVerification(
            @RequestParam(required = false) Long idMessageEnvoye,
            Model model) {
        
        model.addAttribute("idMessageEnvoye", idMessageEnvoye);
        model.addAttribute("otpVerifyRequest", new OtpVerifyRequestDTO());
        
        return "verify-form";
    }

    /**
     * Affiche le formulaire pour renvoyer un code OTP
     */
    @GetMapping("/send")
    public String afficherFormulaireEnvoiOtp(Model model) {
        model.addAttribute("otpSendRequest", new OtpSendRequestDTO());
        return "otp-send"; // À créer si pas existant
    }

    /**
     * Traite la vérification du code OTP
     */
    @PostMapping("/verify")
    public String verifierOtp(
            OtpVerifyRequestDTO request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Récupérer le JWT depuis la session
        String jwtToken = (String) session.getAttribute("JWT_TOKEN");
        System.out.println("JWT Token présent: " + (jwtToken != null));
        
        if (jwtToken == null) {
            redirectAttributes.addFlashAttribute("error", "Session expirée. Veuillez vous reconnecter.");
            return "redirect:/login";
        }

        // Récupérer l'idMessageEnvoye depuis la session (pas du formulaire)
        Long idMessageEnvoye = (Long) session.getAttribute("ID_MESSAGE_ENVOYE");
        System.out.println("ID Message Envoyé (depuis session): " + idMessageEnvoye);
        
        // Utiliser l'ID de la session pour la vérification
        request.setIdMessageEnvoye(idMessageEnvoye);

        // Logger les données envoyées
        System.out.println("=== DONNÉES ENVOYÉES POUR VÉRIFICATION OTP ===");
        System.out.println("idMessageEnvoye: " + request.getIdMessageEnvoye());
        System.out.println("code: " + request.getCode());

        // Appeler le service de vérification
        OtpResponseDTO response = otpService.verifierOtp(request, jwtToken);

        // Logger la réponse
        System.out.println("=== RÉPONSE VÉRIFICATION OTP ===");
        System.out.println("Success: " + response.isSuccess());
        System.out.println("Message: " + response.getMessage());

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("success", "Code OTP vérifié avec succès !");
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", response.getMessage());
            return "redirect:/otp/verify";
        }
    }
}
