package sms.back_end.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpServletRequest;
import sms.back_end.entity.Historique;
import sms.back_end.security.JwtUtils;
import sms.back_end.service.HistoriquePdfExportService;
import sms.back_end.service.HistoriqueService;

@RestController
@RequestMapping("/api/historique")
public class HistoriquePdfExportController {

    private final HistoriqueService historiqueService;
    private final HistoriquePdfExportService pdfService;
    private final JwtUtils jwtUtils;

    public HistoriquePdfExportController(
            HistoriqueService historiqueService,
            HistoriquePdfExportService pdfService,
            JwtUtils jwtUtils
    ) {
        this.historiqueService = historiqueService;
        this.pdfService = pdfService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/{idEnvoi}/export/pdf")
    public ResponseEntity<byte[]> exportSmsPdf(
            @PathVariable Long idEnvoi,
            HttpServletRequest request
    ) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant");
        }

        Long userId = jwtUtils.getUserIdFromJwt(authHeader.substring(7));

        Historique historique = historiqueService.getHistoriqueByUserId(userId)
                .stream()
                .filter(h -> h.getIdEnvoi().equals(idEnvoi))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));

        JsonNode json = historiqueService.getSmsDetailsFromInfobip(historique);

        byte[] pdfData = pdfService.exportSmsDetailToPdf(json);

        String filename = "sms_detail_" + historique.getInfobipMessageId() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }
}
