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
     * Traite la vérification du code OTP
     */
    @PostMapping("/verify")
    public String verifierOtp(
            OtpVerifyRequestDTO request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Récupérer le JWT depuis la session
        String jwtToken = (String) session.getAttribute("jwtToken");
        
        if (jwtToken == null) {
            redirectAttributes.addFlashAttribute("error", "Session expirée. Veuillez vous reconnecter.");
            return "redirect:/login";
        }

        // Vérifier que l'ID du message est présent
        if (request.getIdMessageEnvoye() == null) {
            redirectAttributes.addFlashAttribute("error", "ID de message manquant. Veuillez renvoyer un code.");
            return "redirect:/otp/send";
        }

        // Appeler le service de vérification
        OtpResponseDTO response = otpService.verifierOtp(request, jwtToken);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("success", "Code OTP vérifié avec succès !");
            return "redirect:/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", response.getMessage());
            redirectAttributes.addFlashAttribute("idMessageEnvoye", request.getIdMessageEnvoye());
            return "redirect:/otp/verify?idMessageEnvoye=" + request.getIdMessageEnvoye();
        }
    }
}